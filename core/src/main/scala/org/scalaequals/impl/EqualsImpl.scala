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
  * @version 1.1.1
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

  private[EqualsImpl] class EqualsMaker[A <: Context](val c: A) extends Locator {
    type C = A
    import c.universe._
    import definitions._

    val that = c.fresh("that$": TermName)
    val tpe = c.enclosingClass.symbol.asType.toType
    val eqMethod = c.enclosingMethod
    val selfTpeCanEqual = findCanEqual(tpe)
    val superEquals = hasSuperOverridingEquals(tpe)
    val warn = !(c.settings contains "scala-equals-no-warn")

    if (!isEquals(eqMethod.symbol)) c.abort(c.enclosingMethod.pos, Errors.badEqualCallSite)
    if (!isConsistentWithInheritance(tpe, eqMethod) && warn) c.warning(eqMethod.pos, Warnings.notSafeToSubclass)

    def make() = {
      if (c.enclosingClass.symbol.asClass.isTrait && warn)
        c.warning(c.enclosingClass.pos, Warnings.equalWithTrait)
      createCondition(constructorValsNotInherited(tpe))
    }

    def makeAll() = createCondition(valsNotInherited(tpe))

    def make(params: Seq[c.Expr[Any]]) = createCondition((params map {_.tree.symbol.asTerm}).toList)

    def createCondition(values: List[Symbol]) = {
      val payload = EqualsPayload(values map {_.name.encoded}, superEquals || values.isEmpty)
      eqMethod.updateAttachment(payload)

      val termEquals = values map mkTermEquals
      val withSuper =
        if (superEquals) mkSuperEquals(that) :: termEquals
        else if (termEquals.size == 0) List(mkSuperEquals(that))
        else termEquals
      val withCanEqual = selfTpeCanEqual map {_ => mkCanEqual(that) :: withSuper} getOrElse withSuper
      val and = withCanEqual reduce {(a, b) => mkAnd(a, b)}

      val arg = findArgument(eqMethod)

      c.Expr[Boolean](mkMatch(arg, and))
    }

    def mkSelect(term: Name, member: Symbol) = Select(Ident(term), member)
    def mkSelect(term: Name, member: Name) = Select(Ident(term), member)
    def mkThis = This(tpe.typeSymbol)
    def mkSuper = Super(mkThis, tpnme.EMPTY)
    def mkThisSelect(member: Symbol) = Select(mkThis, member)
    def mkApply(left: Tree, right: Tree) = Apply(left, List(right))
    def mkAnd(left: Tree, right: Tree) = mkApply(Select(left, _and), right)
    def mkEquals(left: Tree, right: Tree) = mkApply(Select(left, _eqeq), right)
    def mkCompareTo(left: Tree, right: Tree) = mkApply(Select(left, _compareTo), right)

    def mkCanEqual(that: Name) = mkApply(mkSelect(that, _canEqual), mkThis)
    def mkSuperEquals(that: Name) = mkApply(Select(mkSuper, _equals), Ident(that))
    def mkTermEquals(term: Symbol) = {
      def isFloatOrDouble(term: Symbol) =
        term.asMethod.returnType =:= DoubleTpe || term.asMethod.returnType =:= FloatTpe
      val thatTerm = mkSelect(that, term)
      val thisTerm = mkThisSelect(term)
      if (isFloatOrDouble(term)) mkEquals(mkCompareTo(thatTerm, thisTerm), Literal(Constant(0)))
      else mkEquals(mkSelect(that, term), mkThisSelect(term))
    }

    def mkBind = Bind(that, Typed(Ident(nme.WILDCARD), TypeTree(tpe)))
    def mkCase(condition: Tree) = CaseDef(mkBind, condition)
    def mkFalseCase = CaseDef(Ident(nme.WILDCARD), EmptyTree, Literal(Constant(false)))
    def mkMatch(other: Name, condition: Tree) = Match(Ident(other), List(mkCase(condition), mkFalseCase))
  }

  case class EqualsPayload(values: Seq[String], superHashCode: Boolean)
}