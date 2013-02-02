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

package org.scalaequals.test

import org.scalacheck.Gen
import org.scalacheck.Arbitrary.arbitrary

class LazyHashEqualTest extends EqualsFixture[LazyHashEqual, LazyHashEqualArg] {
  def name: String = "LazyHashEqual"

  def gen: Gen[LazyHashEqualArg] = for {
    a <- arbitrary[Int]
    b <- arbitrary[Int]
    c <- arbitrary[Int]
    d <- arbitrary[Int]
    e <- arbitrary[Int]
  } yield LazyHashEqualArg(a, b, c, d, e)

  def create(arg: LazyHashEqualArg): LazyHashEqual = new LazyHashEqual(arg.a, arg.b, arg.c, arg.d, arg.e)

  def createToString(arg: LazyHashEqualArg): String =
    s"LazyHashEqual(${arg.a}, ${arg.b}, ${arg.c}, ${arg.d}, ${arg.e})"

  def changeDiff(arg: LazyHashEqualArg, arg2: LazyHashEqualArg): LazyHashEqualArg = arg.copy(b = arg2.b)

  def changeRandom(arg: LazyHashEqualArg, arg2: LazyHashEqualArg): LazyHashEqualArg = {
    val swapped = swap(IndexedSeq(arg.a, arg.c, arg.d, arg.e), IndexedSeq(arg2.a, arg2.c, arg2.d, arg2.e))
    arg.copy(a = swapped(0), c = swapped(1), d = swapped(2), e = swapped(3))
  }

  def diff(arg: LazyHashEqualArg, arg2: LazyHashEqualArg): Boolean = arg.b != arg2.b

  def unequal(arg: LazyHashEqualArg, arg2: LazyHashEqualArg): Boolean =
    arg.a != arg2.a || arg.c != arg2.c || arg.d != arg2.d || arg.e != arg2.e
}

case class LazyHashEqualArg(a: Int, b: Int, c: Int, d: Int, e: Int)
