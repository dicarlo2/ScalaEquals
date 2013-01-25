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

/** Locator used to find elements of a type
  *
  * @author Alex DiCarlo
  * @version 1.0.2
  * @since 1.0.1
  */
private[impl] class Locator[C <: Context](val c: C) {
  import c.universe._

  private val equalsName: TermName = "equals"
  private val hashCodeName: TermName = "hashCode"
  private val canEqualName: TermName = "canEqual"

  private val anyEquals = typeOf[Any].member(equalsName)
  private val anyHashCode = typeOf[Any].member(hashCodeName)
  private val equalsCanEqual = typeOf[Equals].member(canEqualName)

  def isEquals(symbol: Symbol): Boolean = symbol.allOverriddenSymbols.contains(anyEquals)
  def isHashCode(symbol: Symbol): Boolean = symbol.allOverriddenSymbols.contains(anyHashCode)
  def isCanEqual(symbol: Symbol): Boolean =
    symbol.typeSignature =:= equalsCanEqual.typeSignature && symbol.name == canEqualName

  def hasCanEqual(tpe: Type): Boolean = isCanEqual(tpe.member(canEqualName))

  def hasSuperOverridingEquals(tpe: Type): Boolean = {
    def isOverridingEquals(tpe: Type) = isEqualsOverride(tpe.member(equalsName))
    val overriding = tpe.baseClasses map {_.asType.toType} filter isOverridingEquals
    overriding exists {oTpe => !(oTpe =:= typeOf[Object]) && !(oTpe =:= tpe)}
  }

  def findEquals(tree: Tree): Option[Tree] = tree filter {_.isDef} find {t => isEqualsOverride(t.symbol)}

  def findArgument(tree: Tree): TermName = (tree collect {
    case DefDef(_, _, _, List(List(ValDef(_, termName, _, _))), _, _) => termName
  }).head

  private def isEqualsOverride(term: Symbol): Boolean = term match {
    case equalsTerm: TermSymbol =>
      equalsTerm.alternatives map {_.asTerm} exists {_.allOverriddenSymbols.contains(anyEquals)}
    case _ => false
  }
}
