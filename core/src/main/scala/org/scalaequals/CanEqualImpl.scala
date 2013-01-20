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

/** Implementation of `ScalaEquals.canEquals` macro
  *
  * @author Alex DiCarlo
  * @version 0.3.0
  * @since 0.3.0
  */
object CanEqualImpl {
  def canEquals(c: Context)(other: Any): c.Expr[Boolean] = {
    import c.universe._
    if (c.enclosingMethod.symbol.name != ("canEqual": TermName))
      c.abort(c.enclosingPosition, Errors.incorrectCanEqualCallSite)
    val tree =
      TypeApply(
        Select(
          Ident(
            newTermName("other")),
          newTermName("isInstanceOf")),
        List(
          TypeTree(c.enclosingClass.symbol.asType.toType)))
    c.Expr[Boolean](tree)
  }
}
