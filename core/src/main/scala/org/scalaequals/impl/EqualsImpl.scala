/*
 * Copyright (c) 2013 Alex DiCarlo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.scalaequals.impl

import scala.reflect.macros.Context

/** Implementation of `ScalaEquals.equal` and `ScalaEquals.equalAllVals` macro
  *
  * @author Alex DiCarlo
  * @version 1.1.0
  * @since 0.1.0
  */
private[scalaequals] object EqualsImpl {
  def equalImpl(c: Context): c.Expr[Boolean] = {
    new EqualsMaker[c.type](c).make()
  }

  def equalAllValsImpl(c: Context): c.Expr[Boolean] = {
    new EqualsMaker[c.type](c).makeAll()
  }

  def equalParamImpl(c: Context)(param: c.Expr[Any], params: c.Expr[Any]*): c.Expr[Boolean] = {
    new EqualsMaker[c.type](c).make(param +: params)
  }

  private[EqualsImpl] class EqualsMaker[C <: Context](val c: C) {
    import c.universe._

    val selfTpe = c.enclosingClass.symbol.asType.toType
    val locator = new Locator[c.type](c)
    val selfTpeCanEqual = locator.getCanEqual(selfTpe)
    val hasSuperOverridingEquals = locator.hasSuperOverridingEquals(selfTpe)
    val isFinalOrCanEqualDefined = (selfTpeCanEqual map {_.owner == selfTpe.typeSymbol} getOrElse false) ||
      ((c.enclosingMethod.symbol.isFinal || c.enclosingClass.symbol.isFinal) && !hasSuperOverridingEquals)
    val warn = !(c.settings contains "scala-equals-no-warn")

    def make(): c.Expr[Boolean] = {
      if (c.enclosingClass.symbol.asClass.isTrait && warn)
        c.warning(c.enclosingClass.pos, Warnings.equalWithTrait)
      createCondition(constructorValsNotInherited())
    }

    def makeAll(): c.Expr[Boolean] = {
      createCondition(valsNotInherited())
    }

    def make(params: Seq[c.Expr[Any]]): c.Expr[Boolean] = {
      val values = (params map {_.tree.symbol.asTerm}).toList
      createCondition(values)
    }

    def constructorValsNotInherited(): List[TermSymbol] = valsNotInherited() filter {_.isParamAccessor}

    def valsNotInherited(): List[TermSymbol] = {
      def isVal(term: TermSymbol): Boolean = term.isStable && term.isMethod
      def isInherited(term: TermSymbol): Boolean = term.owner != selfTpe.typeSymbol || term.isOverride
      (selfTpe.members filter {_.isTerm} map {_.asTerm} filter {t => isVal(t) && !isInherited(t)}).toList
    }

    def createCondition(values: List[TermSymbol]): c.Expr[Boolean] = {
      if (!locator.isEquals(c.enclosingMethod.symbol))
        c.abort(c.enclosingMethod.pos, Errors.badEqualCallSite)

      if (!isFinalOrCanEqualDefined && warn)
        c.warning(c.enclosingMethod.pos, Warnings.notSafeToSubclass)

      val payload = EqualsPayload(values map {_.name.encoded}, hasSuperOverridingEquals || values.isEmpty)
      c.enclosingMethod.updateAttachment(payload)

      val termEquals = values map createTermEquals
      val and =
        if (hasSuperOverridingEquals) createNestedAnd(createSuperEquals() :: termEquals)
        else if (termEquals.size == 0) createSuperEquals()
        else createNestedAnd(termEquals)
      val withCanEqual = selfTpeCanEqual map {canEqual => createNestedAnd(List(createCanEqual(), and))} getOrElse and

      val arg = locator.findArgument(c.enclosingMethod)

      c.Expr[Boolean](createMatch(arg, withCanEqual))
    }

    def createCanEqual(): Apply =
      Apply(
        Select(
          Ident(newTermName("that")),
          newTermName("canEqual")),
        List(
          This(tpnme.EMPTY)))

    def createTermEquals(term: TermSymbol): Apply = {
      def createEquals(term: Symbol): Apply = {
        Apply(
          Select(
            Select(
              Ident(newTermName("that")),
              term),
            newTermName("$eq$eq")),
          List(
            Select(
              This(tpnme.EMPTY),
              term)))
      }
      def createFloatOrDoubleEquals(term: Symbol): Apply = {
        Apply(
          Select(
            Apply(
              Select(
                Select(
                  Ident(newTermName("that")),
                  term),
                newTermName("compareTo")),
              List(
                Select(
                  This(tpnme.EMPTY),
                  term))),
            newTermName("$eq$eq")),
          List(
            Literal(Constant(0))))
      }
      if (isFloatOrDouble(term)) createFloatOrDoubleEquals(term) else createEquals(term)
    }

    def isFloatOrDouble(term: TermSymbol): Boolean = c.enclosingClass exists {
      case valDef@ValDef(_, termName, _, _) if termName == term.name =>
        valDef.symbol.typeSignature =:= typeOf[Double] || valDef.symbol.typeSignature =:= typeOf[Float]
      case _ => false
    }

    def createAnd(left: Apply): Select = Select(left, newTermName("$amp$amp"))

    def createNestedAnd(terms: List[Apply]): Apply = terms match {
      case left :: x :: xs => createNestedAnd(Apply(createAnd(left), List(x)) :: xs)
      case left :: Nil => left
    }

    def createSuperEquals(): Apply =
      Apply(
        Select(
          Super(This(tpnme.EMPTY), tpnme.EMPTY),
          newTermName("equals")),
        List(
          Ident(newTermName("that"))))

    def createMatch(other: TermName, condition: Apply): Match = {
      Match(
        Ident(other),
        List(
          CaseDef(
            Bind(
              newTermName("that"),
              Typed(
                Ident(nme.WILDCARD),
                TypeTree(selfTpe))),
            condition),
          CaseDef(
            Ident(nme.WILDCARD),
            EmptyTree,
            Literal(Constant(false)))))
    }
  }

  case class EqualsPayload(values: Seq[String], superHashCode: Boolean)
}