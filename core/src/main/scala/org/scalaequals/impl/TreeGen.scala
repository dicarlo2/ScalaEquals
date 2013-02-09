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

/** TreeGen contains methods for generating general trees
  *
  * @author Alex DiCarlo
  * @version 1.2.0
  * @since 1.1.1
  */
trait TreeGen {self: Locator =>
  import c.universe._
  val tpe: Type = c.enclosingClass.symbol.asType.toType

  def mkThis = This(tpe.typeSymbol)
  def mkSuper = Super(mkThis, tpnme.EMPTY)

  def mkSelect(term: Name, member: Symbol) = Select(Ident(term), member)
  def mkSelect(fst: Name, snd: Name, rest: Name*) =
    (rest foldLeft Select(Ident(fst), snd)){case (curr, name) => Select(curr, name)}
  def mkThisSelect(member: Symbol) = Select(mkThis, member)
  def mkThisSelect(member: Name) = Select(mkThis, member)
  def mkSuperSelect(member: Name) = Select(mkSuper, member)

  def mkApply(left: Tree, right: Tree) = Apply(left, List(right))
  def mkApply(left: Tree) = Apply(left, List())
  def mkTpeApply(left: Tree, right: Tree) = TypeApply(left, List(right))

  def mkAnd(left: Tree, right: Tree) = mkApply(Select(left, _and), right)
  def mkAdd(left: Tree, right: Tree) = mkApply(Select(left, _plus), right)

  def mkString(string: String) = Literal(Constant(string))
  def mkToString(term: Name) = mkApply(mkSelect(term, _toString))
}
