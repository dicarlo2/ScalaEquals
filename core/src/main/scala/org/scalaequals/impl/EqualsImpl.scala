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
  * @version 2.0.0
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

    val selfTpe = c.enclosingImpl.symbol.asType.toType
    val locator = new Locator[c.type](c)
    val selfTpeCanEqual = locator.getCanEqual(selfTpe)
    val selfTpeLazyHash = locator.getLazyHash(selfTpe)
    val hasSuperOverridingEquals = locator.hasSuperOverridingEquals(selfTpe)
    val isFinalOrCanEqualDefined = (selfTpeCanEqual map {_.owner == selfTpe.typeSymbol} getOrElse false) ||
      ((c.enclosingDef.symbol.isFinal || c.enclosingImpl.symbol.isFinal) && !hasSuperOverridingEquals)
    val warn = !(c.settings contains "scala-equals-no-warn")

    def make(): c.Expr[Boolean] = {
      if (c.enclosingImpl.symbol.asClass.isTrait && warn)
        c.warning(c.enclosingImpl.pos, Warnings.equalWithTrait)
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
      def isInherited(term: TermSymbol): Boolean = term.owner != selfTpe.typeSymbol || term.isOverride
      (selfTpe.members filter {_.isTerm} map {_.asTerm} filter {t => isVal(t) && !isInherited(t)}).toList
    }

    def isVal(term: TermSymbol): Boolean = term.isStable && term.isMethod

    def createCondition(values: List[TermSymbol]): c.Expr[Boolean] = {
      if (!locator.isEquals(c.enclosingDef.symbol))
        c.abort(c.enclosingDef.pos, Errors.badEqualCallSite)

      if (!isFinalOrCanEqualDefined && warn)
        c.warning(c.enclosingDef.pos, Warnings.notSafeToSubclass)

      val payload = EqualsPayload(values map {_.name.encoded}, hasSuperOverridingEquals || values.isEmpty)
      c.enclosingDef.updateAttachment(payload)

      selfTpeLazyHash match {
        case None => createIt(values)
        case Some(lazyHash) =>
          if (!(values forall isVal))
            c.abort(c.enclosingDef.pos, Errors.badLazyHashVals)
          createIt(List(lazyHash.asTerm))
      }
    }

    private def createIt(values: List[TermSymbol]): c.Expr[Boolean] = {
      val termEquals = values map createTermEquals
      val and =
        if (hasSuperOverridingEquals) createNestedAnd(createSuperEquals() :: termEquals)
        else if (termEquals.size == 0) createSuperEquals()
        else createNestedAnd(termEquals)
      val withCanEqual = selfTpeCanEqual map {canEqual => createNestedAnd(List(createCanEqual(), and))} getOrElse and

      val arg = locator.findArgument(c.enclosingDef)

      c.Expr[Boolean](createMatch(arg, withCanEqual))
    }

    def createCanEqual(): Apply =
      Apply(
        Select(
          Ident(TermName("that")),
          TermName("canEqual")),
        List(
          This(tpnme.EMPTY)))

    def createTermEquals(term: TermSymbol): Apply = {
      def createEquals(term: Symbol): Apply = {
        Apply(
          Select(
            Select(
              Ident(TermName("that")),
              term),
            TermName("$eq$eq")),
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
                  Ident(TermName("that")),
                  term),
                TermName("compareTo")),
              List(
                Select(
                  This(tpnme.EMPTY),
                  term))),
            TermName("$eq$eq")),
          List(
            Literal(Constant(0))))
      }
      if (isFloatOrDouble(term)) createFloatOrDoubleEquals(term) else createEquals(term)
    }

    def isFloatOrDouble(term: TermSymbol): Boolean = c.enclosingImpl exists {
      case valDef@ValDef(_, termName, _, _) if termName == term.name =>
        valDef.symbol.typeSignature =:= typeOf[Double] || valDef.symbol.typeSignature =:= typeOf[Float]
      case _ => false
    }

    def createAnd(left: Apply): Select = Select(left, TermName("$amp$amp"))

    def createNestedAnd(terms: List[Apply]): Apply = terms match {
      case left :: x :: xs => createNestedAnd(Apply(createAnd(left), List(x)) :: xs)
      case left :: Nil => left
    }

    def createSuperEquals(): Apply =
      Apply(
        Select(
          Super(This(tpnme.EMPTY), tpnme.EMPTY),
          TermName("equals")),
        List(
          Ident(TermName("that"))))

    def createMatch(other: TermName, condition: Apply): Match = {
      Match(
        Ident(other),
        List(
          CaseDef(
            Bind(
              TermName("that"),
              Typed(
                Ident(nme.WILDCARD),
                TypeTree(selfTpe))),
            EmptyTree,
            condition),
          CaseDef(
            Ident(nme.WILDCARD),
            EmptyTree,
            Literal(Constant(false)))))
    }
  }

  case class EqualsPayload(values: Seq[String], superHashCode: Boolean)
}