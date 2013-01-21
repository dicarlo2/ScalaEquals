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

package org.scalaequals

import reflect.macros.Context
import reflect.internal.Symbols

/** Implementation of `ScalaEquals.equal` and `ScalaEquals.equalAllVals` macro
  *
  * @author Alex DiCarlo
  * @version 1.0.0
  * @since 0.1.0
  */
object EqualsImpl {
  def equalImpl(c: Context)(other: c.Expr[Any]): c.Expr[Boolean] = {
    val eqm = new EqualsMaker[c.type](c)
    new eqm.EqualsMakerInner(other).make()
  }

  def equalAllValsImpl(c: Context)(other: c.Expr[Any]): c.Expr[Boolean] = {
    val eqm = new EqualsMaker[c.type](c)
    new eqm.EqualsMakerInner(other).makeAll()
  }

  def equalParamImpl(c: Context)
    (other: c.Expr[Any], param: c.Expr[Any], params: c.Expr[Any]*): c.Expr[Boolean] = {
    val eqm = new EqualsMaker[c.type](c)
    new eqm.EqualsMakerInner(other).make(param +: params)
  }

  private[EqualsImpl] class EqualsMaker[C <: Context](val c: C) {
    class EqualsMakerInner(other: c.Expr[Any]) {

      import c.universe._

      val selfTpe: Type = c.enclosingClass.symbol.asType.toType
      val hasCanEqual: Boolean = !selfTpe.member("canEqual": TermName).isInstanceOf[Symbols#NoSymbol]
      val hasSuperClassWithEquals: Boolean = {
        val overriding = selfTpe.baseClasses map {_.asType.toType} filter {_.member("equals": TermName).isOverride}
        overriding exists {tpe => !(tpe =:= typeOf[AnyRef] || tpe =:= typeOf[Object] || tpe =:= selfTpe)}
      }

      def createCanEqual(): Apply =
        Apply(
          Select(
            Ident(newTermName("that")),
            newTermName("canEqual")),
          List(
            This(tpnme.EMPTY)))

      def createTermEquals(term: TermSymbol): Apply = {
        def createEquals(term: Symbol): Apply =
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
        if (term.isMethod) createEquals(term) else createEquals(term.getter)
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
            Ident(newTermName("that")))
        )

      def createMatch(condition: Apply): Match = {
        Match(
          Ident(other.tree.symbol.asTerm),
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
              Literal(Constant(false))
            )
          ))
      }

      def createCondition(values: Seq[TermSymbol]): c.Expr[Boolean] = {
        if (c.enclosingMethod.symbol.name != ("equals": TermName))
          c.abort(c.enclosingPosition, Errors.incorrectEqualCallSite)
        val payload = EqualsPayload(values map {_.name.encoded}, hasSuperClassWithEquals || values.size == 0)
        c.enclosingMethod.updateAttachment(payload)
        val termEquals = (values map createTermEquals).toList
        val and = (hasCanEqual, hasSuperClassWithEquals) match {
          case (true, true) => createNestedAnd(createCanEqual() :: createSuperEquals() :: termEquals)
          case (false, true) => createNestedAnd(createSuperEquals() :: termEquals)
          case (true, false) if termEquals.size == 0 => createNestedAnd(List(createCanEqual(), createSuperEquals()))
          case (true, false) => createNestedAnd(createCanEqual() :: termEquals)
          case (false, false) if termEquals.size == 0 => createSuperEquals()
          case (false, false) => createNestedAnd(termEquals)
        }
        c.Expr[Boolean](createMatch(and))
      }

      def isVal(term: TermSymbol): Boolean = term.isStable && term.isMethod
      def isInherited(term: TermSymbol): Boolean = term.owner != selfTpe.typeSymbol || term.isOverride
      def isAccessible(term: TermSymbol): Boolean = term.owner == selfTpe.typeSymbol || !term.isPrivate

      def notInheritedVal(): Seq[TermSymbol] =
        (selfTpe.members filter {_.isTerm} map {_.asTerm} filter {t => isVal(t) && !isInherited(t)}).toSeq

      def make(): c.Expr[Boolean] = {
        val values = notInheritedVal() filter  {_.isParamAccessor}
        createCondition(values)
      }

      def makeAll(): c.Expr[Boolean] = {
        val values = notInheritedVal()
        createCondition(values)
      }

      def make(params: Seq[c.Expr[Any]]): c.Expr[Boolean] = {
        val values = params map {_.tree.symbol.asTerm}
        createCondition(values)
      }
    }
  }

  case class EqualsPayload(values: Seq[String], superHashCode: Boolean)
}