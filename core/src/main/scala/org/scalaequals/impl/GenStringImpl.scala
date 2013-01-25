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
  * @version 1.1.0
  * @since 1.1.0
  */
private[scalaequals] object GenStringImpl {
  def genString(c: Context): c.Expr[String] = {
    import c.universe._

    val locator = new Locator[c.type](c)
    if (!locator.isToString(c.enclosingMethod.symbol))
      c.abort(c.enclosingMethod.pos, Errors.badToStringCallSite)

    val args = locator.constructorArgs(c.enclosingClass, c.enclosingClass.symbol.asType.toType)
    def createNestedAdd(terms: List[TermName]): Tree = terms match {
      case Nil => Literal(Constant(")"))
      case x :: Nil =>
        Apply(Select(Ident(x), newTermName("$plus")), List(Literal(Constant(")"))))
      case x :: xs =>
        val first = Apply(Select(Ident(x), newTermName("$plus")), List(Literal(Constant(", "))))
        Apply(Select(first, newTermName("$plus")), List(createNestedAdd(xs)))
    }
    val nestedAdd = createNestedAdd(args)

    val tree =
      Apply(
        Select(
          Literal(
            Constant(
              c.enclosingClass.symbol.name.toString + "(")),
          newTermName("$plus")),
        List(nestedAdd))
    c.Expr[String](tree)
  }
}