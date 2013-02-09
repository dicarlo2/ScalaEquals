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

/** Implementation of `ScalaEquals.genString` macro
  *
  * @author Alex DiCarlo
  * @version 1.2.0
  * @since 1.1.0
  */
private[scalaequals] object GenStringImpl {
  def genStringImpl(c: Context): c.Expr[String] = new GenStringMaker[c.type](c).make()

  def genStringParamsImpl(c: Context)(param: c.Expr[Any], params: c.Expr[Any]*): c.Expr[String] =
    new GenStringMaker[c.type](c).make((param +: params).to[List])

  private[GenStringImpl] class GenStringMaker[A <: Context](val c: A) extends Locator {
    type C = A
    import c.universe._

    abortIf(!isToString(c.enclosingMethod.symbol), badGenStringCallSite)

    def make() = {
      warnClassIf(c.enclosingClass.symbol.asClass.isTrait, warnings.genStringWithTrait)
      makeString(constrArgs(tpe) map {_.name.toTermName})
    }

    def make(params: Seq[c.Expr[Any]]) = makeString((params map {_.tree.symbol.name.toTermName}).to[List])

    def makeString(args: List[TermName]) = {
      val stringArgs = mkNestedAdd(args)
      val className = mkString(c.enclosingClass.symbol.name.toString + "(")
      val tree = mkAdd(className, stringArgs)
      c.Expr[String](tree)
    }

    def mkNestedAdd(terms: List[TermName]): Tree = terms match {
      case Nil => mkString(")")
      case x :: Nil => mkAdd(Ident(x), mkString(")"))
      case x :: xs => mkAdd(mkAdd(Ident(x), mkString(", ")), mkNestedAdd(xs))
    }
  }
}
