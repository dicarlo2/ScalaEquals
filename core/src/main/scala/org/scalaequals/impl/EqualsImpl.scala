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

import reflect.macros.Context

/** Implementation of `ScalaEquals.equal` and `ScalaEquals.equalAllVals` macro
  *
  * @author Alex DiCarlo
  * @version 1.0.1
  * @since 0.1.0
  */
private[scalaequals] object EqualsImpl {
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
      val locator: Locator[c.type] = new Locator[c.type](c)
      val hasCanEqual: Boolean = locator.hasCanEqual(selfTpe)
      val hasSuperOverridingEquals: Boolean = locator.hasSuperOverridingEquals(selfTpe)

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

      def createCondition(values: List[TermSymbol]): c.Expr[Boolean] = {
        if (!locator.isEquals(c.enclosingMethod.symbol))
          c.abort(c.enclosingMethod.pos, Errors.badEqualCallSite)

        val payload = EqualsPayload(values map {_.name.encoded}, hasSuperOverridingEquals || values.isEmpty)
        c.enclosingMethod.updateAttachment(payload)

        val termEquals = values map createTermEquals
        val and = (hasCanEqual, hasSuperOverridingEquals) match {
          case (true, true) => createNestedAnd(createCanEqual() :: createSuperEquals() :: termEquals)
          case (false, true) => createNestedAnd(createSuperEquals() :: termEquals)
          case (true, false) if termEquals.size == 0 => createNestedAnd(List(createCanEqual(), createSuperEquals()))
          case (true, false) => createNestedAnd(createCanEqual() :: termEquals)
          case (false, false) if termEquals.size == 0 => createSuperEquals()
          case (false, false) => createNestedAnd(termEquals)
        }

        c.Expr[Boolean](createMatch(and))
      }

      def make(): c.Expr[Boolean] = {
        createCondition(locator.constructorValsNotInherited(selfTpe))
      }

      def makeAll(): c.Expr[Boolean] = {
        createCondition(locator.valsNotInherited(selfTpe))
      }

      def make(params: Seq[c.Expr[Any]]): c.Expr[Boolean] = {
        val values = (params map {_.tree.symbol.asTerm}).toList
        createCondition(values)
      }
    }
  }

  case class EqualsPayload(values: Seq[String], superHashCode: Boolean)
}