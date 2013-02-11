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

/** Names contains all `TermName`s used by ScalaEquals
  *
  * @author Alex DiCarlo
  * @version 1.2.0
  * @since 1.1.1
  */
private[impl] trait Names {self: Locator =>
  import c.universe._

  val _equals = TermName("equals")
  val _canEqual = TermName("canEqual")
  val _hashCode = TermName("hashCode")
  val _toString = TermName("toString")
  val _and = TermName("$amp$amp")
  val _plus = TermName("$plus")
  val _eqeq = TermName("$eq$eq")
  val _compareTo = TermName("compareTo")
  val _isInstanceOf = TermName("isInstanceOf")
  val _productArity = TermName("productArity")
  val _productElement = TermName("productElement")
  val _productPrefix = TermName("productPrefix")
  val _constructor = TermName("<init>")
  val _mixinConstructor = TermName("$init$")
}
