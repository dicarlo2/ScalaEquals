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
  * @version 1.2.0
  * @since 0.1.0
  */
private[scalaequals] object EqualsImpl {
  // provides a source compatibility stub
  // in Scala 2.10.x, it will make `import compat._` compile just fine,
  // even though `c.universe` doesn't have `compat`
  // in Scala 2.11.0, it will be ignored, becase `import c.universe._`
  // brings its own `compat` in scope and that one takes precedence
  private object HasCompat { val compat = ??? }
  import HasCompat._

  def equalImpl(c: Context): c.Expr[Boolean] = new EqualsMaker[c.type](c)(true).make()

  def equalImplNoCompareTo(c: Context): c.Expr[Boolean] = new EqualsMaker[c.type](c)(false).make()

  def equalAllValsImpl(c: Context): c.Expr[Boolean] = new EqualsMaker[c.type](c)(true).makeAll()

  def equalAllValsImplNoCompareTo(c: Context): c.Expr[Boolean] = new EqualsMaker[c.type](c)(false).makeAll()

  def equalParamImpl(c: Context)(param: c.Expr[Any], params: c.Expr[Any]*): c.Expr[Boolean] =
    new EqualsMaker[c.type](c)(true).make(param +: params)

  def equalParamImplNoCompareTo(c: Context)(param: c.Expr[Any], params: c.Expr[Any]*): c.Expr[Boolean] =
    new EqualsMaker[c.type](c)(false).make(param +: params)

  private[EqualsImpl] class EqualsMaker[A <: Context](val c: A)(compareTo: Boolean) extends Locator {
    type C = A
    import c.universe._
    import compat._
    import definitions._

    val that = c.fresh("that$": TermName)
    val eqMethod = c.enclosingMethod
    val canEqMethod = findCanEqual(tpe)
    val lazyHashMethod = findLazyHash(tpe)
    val superEquals = hasSuperOverridingEquals(tpe)

    abortIf(!isEquals(eqMethod.symbol), badEqualCallSite)
    warnIf(!isConsistentWithInheritance(tpe, eqMethod), warnings.notSafeToSubclass)

    def make() = {
      warnClassIf(c.enclosingClass.symbol.asClass.isTrait, warnings.equalWithTrait)
      makeIt(constrValsNotInherited(tpe))
    }

    def makeAll() = makeIt(valsNotInherited(tpe))

    def make(params: Seq[c.Expr[Any]]) = makeIt((params map {_.tree.symbol.asTerm}).toList)

    def makeIt(values: List[Symbol]) = {
      eqMethod.updateAttachment(EqualsPayload(values map {_.name.encoded}, superEquals || values.isEmpty))
      lazyHashMethod match {
        case Some(lazyHash) =>
          abortIf(!(values forall isVal), badLazyHashVals)
          createCondition(List(lazyHash))
        case None => createCondition(values)
      }
    }

    def createCondition(values: List[Symbol]) = {
      val termEquals = values map mkTermEquals
      val withSuper =
        if (superEquals) mkSuperEquals(that) :: termEquals
        else if (termEquals.size == 0) List(mkSuperEquals(that))
        else termEquals
      val withCanEqual = canEqMethod map {_ => mkCanEqual(that) :: withSuper} getOrElse withSuper
      val and = withCanEqual reduce {(a, b) => mkAnd(a, b)}

      val arg = argN(eqMethod, 0).name

      c.Expr[Boolean](mkMatch(arg, and))
    }

    def mkEquals(left: Tree, right: Tree) = mkApply(Select(left, _eqeq), right)
    def mkCompareTo(left: Tree, right: Tree) = mkApply(Select(left, _compareTo), right)

    def mkCanEqual(that: Name) = mkApply(mkSelect(that, _canEqual), mkThis)
    def mkSuperEquals(that: Name) = mkApply(Select(mkSuper, _equals), Ident(that))
    def mkTermEquals(term: Symbol) = {
      def isFloatOrDouble(term: Symbol) = {
        if (term.isMethod) term.asMethod.returnType =:= DoubleTpe || term.asMethod.returnType =:= FloatTpe
        else term.typeSignature =:= DoubleTpe || term.typeSignature =:= FloatTpe
      }
      val thatTerm = mkSelect(that, term.name)
      val thisTerm = mkThisSelect(term.name)
      if (compareTo && isFloatOrDouble(term)) mkEquals(mkCompareTo(thatTerm, thisTerm), Literal(Constant(0)))
      else mkEquals(thatTerm, thisTerm)
    }

    def mkBind = Bind(that, Typed(Ident(nme.WILDCARD), TypeTree(tpe)))
    def mkCase(condition: Tree) = CaseDef(mkBind, condition)
    def mkFalseCase = CaseDef(Ident(nme.WILDCARD), EmptyTree, Literal(Constant(false)))
    def mkMatch(other: Name, condition: Tree) = Match(Ident(other), List(mkCase(condition), mkFalseCase))
  }

  private[impl] case class EqualsPayload(values: Seq[String], superHashCode: Boolean)
}