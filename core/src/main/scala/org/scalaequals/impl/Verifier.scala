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

import scala.PartialFunction.cond

/** Verifier contains methods for querying symbols or type properties
  *
  * @author Alex DiCarlo
  * @version 1.2.0
  * @since 1.1.1
  */
trait Verifier {self: Locator =>
  import c.universe._

  def isEquals(symbol: Symbol) = symbol.allOverriddenSymbols.contains(Any_equals)
  def isHashCode(symbol: Symbol) = symbol.allOverriddenSymbols.contains(Any_hashCode)
  def isLazyHashCode(symbol: Symbol): Boolean =
    symbol.typeSignature =:= LazyHashCode_hashCode.typeSignature && symbol.allOverriddenSymbols.contains(Any_hashCode)
  def isCanEqual(symbol: Symbol) = symbol.typeSignature =:= Equals_canEqual.typeSignature && symbol.name == _canEqual
  def isToString(symbol: Symbol) = symbol.allOverriddenSymbols.contains(Any_toString)
  def isProductArity(symbol: Symbol) = symbol.typeSignature =:= Product_productArity.typeSignature
  def isProductElement(symbol: Symbol) = symbol.typeSignature =:= Product_productElement.typeSignature
  def isProductPrefix(symbol: Symbol) = symbol.typeSignature =:= Product_productPrefix.typeSignature

  def isEqualsOverride(term: Symbol) = term match {
    case eq: TermSymbol => eq.alternatives map {_.asTerm} exists {_.allOverriddenSymbols.contains(Any_equals)}
    case _ => false
  }

  def hasSuperOverridingEquals(tpe: Type) = {
    def isOverridingEquals(tpe: Type) = isEqualsOverride(tpe.member(_equals))
    val overriding = tpe.baseClasses map {_.asType.toType} filter isOverridingEquals
    overriding exists {oTpe => !(oTpe =:= typeOf[Object]) && !(oTpe =:= tpe)}
  }

  def ownsCanEqual(tpe: Type) = cond(findCanEqual(tpe)) {case Some(x) => x.owner == tpe.typeSymbol}

  def isConsistentWithInheritance(tpe: Type, eqMethod: Tree) =
    ownsCanEqual(tpe) || ((eqMethod.symbol.isFinal || tpe.typeSymbol.isFinal) && !hasSuperOverridingEquals(tpe))

  def isVal(term: Symbol) = term.isTerm && term.asTerm.isStable && term.isMethod
}
