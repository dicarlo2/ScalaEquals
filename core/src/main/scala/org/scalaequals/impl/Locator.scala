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

/** Locator used to find elements of a type
  *
  * @author Alex DiCarlo
  * @version 2.0.0
  * @since 1.0.1
  */
private[impl] trait Locator extends Names with Signatures with Verifier with TreeGen with Errors {
  type C <: Context
  val c: C
  import c.universe._

  def constructorValsNotInherited(tpe: Type) = valsNotInherited(tpe) filter {_.isParamAccessor}

  def valsNotInherited(tpe: Type) = {
    def isInherited(term: Symbol) = term.owner != tpe.typeSymbol || term.isOverride
    (tpe.members filter {_.isTerm} map {_.asTerm} filter {t => isVal(t) && !isInherited(t)}).toList
  }

  def findCanEqual(tpe: Type) = {
    val tpeCanEqual = tpe.member(_canEqual)
    if (isCanEqual(tpeCanEqual)) Some(tpeCanEqual) else None
  }

  def findLazyHash(tpe: Type) = {
    val tpeHashCode = tpe.member(_hashCode)
    if (isLazyHashCode(tpeHashCode)) Some(tpeHashCode) else None
  }

  def findEquals(tpe: Type) = {
    val tpeEquals = tpe.member(_equals)
    if (isEquals(tpeEquals)) Some(tpeEquals) else None
  }

  def findEquals(tree: Tree) = tree filter {_.isDef} find {t => isEqualsOverride(t.symbol)}

  def findArgument(tree: Tree) = (tree collect {
    case DefDef(_, _, _, List(List(ValDef(_, termName, _, _))), _, _) => termName
  }).head

  def findCtorArguments(tree: Tree, tpe: Type) = (tree collect {
    case ClassDef(_, name, _, Template(_, _, body)) if name == tpe.typeSymbol.name.toTypeName =>
      body takeWhile {
        case x: ValDef => true
        case _ => false
      } map {
        case ValDef(_, termName, _, _) => termName
      }
  }).head

  def hasScalaEqualsType(name: String, parents: List[Tree]): Boolean =
    parents exists {_ exists {t => isScalaEqualsType(name, t)}}

  def filterScalaEqualsType(name: String, parents: List[Tree]): List[Tree] =
    parents filterNot {_ exists {t => isScalaEqualsType(name, t)}}

  private def isScalaEqualsType(name: String, tree: Tree): Boolean = tree match {
    case Ident(TypeName(`name`)) => true
    case Select(Ident(TermName("ScalaEquals")), TypeName(`name`)) => true
    case _ => false
  }
}