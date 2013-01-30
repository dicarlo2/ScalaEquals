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
import org.scalacheck.Arbitrary._

class FourDColoredPointTest extends PointFixture[FourDColoredPoint] {
  def name: String = FourDColoredPointTest.name

  /* Creates a T from B */
  def create(arg: PointArg): FourDColoredPoint = FourDColoredPointTest.create(arg)

  /* Creates a String to test toString = A(arg) */
  def createToString(arg: PointArg): String =
    s"FourDColoredPoint(${arg.w}, ${arg.x}, ${arg.y}, ${arg.z}, ${arg.color})"

  /* Swaps one random pair of constructor arguments that are part of equals */
  def changeRandom(arg: PointArg, arg2: PointArg): PointArg = {
    val swapped = swap(IndexedSeq(arg.w, arg.x, arg.y, arg.z, arg.color),
      IndexedSeq(arg2.w, arg2.x, arg2.y, arg2.z, arg2.color))
    arg.copy(w = swapped(0).asInstanceOf[Int], x = swapped(1).asInstanceOf[Int], y = swapped(2).asInstanceOf[Int],
    z = swapped(3).asInstanceOf[Int], color = swapped(4).asInstanceOf[Color.Value])
  }

  /* true if arg and arg2 differ in a field checked by equality */
  def unequal(arg: PointArg, arg2: PointArg): Boolean =
    arg.w != arg2.w || arg.x != arg2.x || arg.y != arg2.y || arg.z != arg2.z || arg.color != arg2.color
}

object FourDColoredPointTest {
  def argGen: Gen[PointArg] = for {
    w <- arbitrary[Int]
    x <- arbitrary[Int]
    y <- arbitrary[Int]
    z <- arbitrary[Int]
    color <- Gen.oneOf(Color.values.toSeq)
  } yield PointArg(w, x, y, z, color)

  def name: String = "FourDColoredPoint"

  /* Creates a T from B */
  def create(arg: PointArg): FourDColoredPoint = new FourDColoredPoint(arg.w, arg.x, arg.y, arg.z, arg.color)

  def gen: Gen[FourDColoredPoint] = for {
    arg <- argGen
  } yield create(arg)
}
