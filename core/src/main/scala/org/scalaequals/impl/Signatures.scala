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

/** Signatures contains symbols to be used for their type signatures and for other querying
  *
  * @author Alex DiCarlo
  * @version 1.2.0
  * @since 1.1.1
  */
private[impl] trait Signatures {self: Locator =>
  import c.universe._
  import definitions._

  private val EqualsTpe = typeOf[scala.Equals]
  private val LazyHashCodeTpe = typeOf[LazyHashCode]
  private val ProductTpe = typeOf[scala.Product]

  val Any_equals = AnyTpe.member(_equals)
  val Any_hashCode = AnyTpe.member(_hashCode)
  val Any_toString = AnyTpe.member(_toString)
  val Equals_canEqual = EqualsTpe.member(_canEqual)
  val LazyHashCode_hashCode = LazyHashCodeTpe.member(_hashCode)

  private[Signatures] trait LazyHashCode {
    override lazy val hashCode: Int = 10
  }
}
