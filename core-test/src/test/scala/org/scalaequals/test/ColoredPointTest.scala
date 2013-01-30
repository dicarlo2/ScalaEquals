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

class ColoredPointTest
  extends PointFixture[ColoredPoint] with SubClassedEqualsFixture[ColoredPoint, PointArg, FourDColoredPoint] {
  def name: String = ColoredPointTest.name

  def subClassName: String = FourDColoredPointTest.name

  /* Creates a T from B */
  def create(arg: PointArg): ColoredPoint = ColoredPointTest.create(arg)

  def createSubClass(arg: PointArg): FourDColoredPoint = FourDColoredPointTest.create(arg)

  /* Creates a String to test toString = A(arg) */
  def createToString(arg: PointArg): String = s"ColoredPoint(${arg.x}, ${arg.y}, ${arg.z}, ${arg.color})"

  /* Changes one random argument that is part of equals to arg2's value */
  def changeRandom(arg: PointArg, arg2: PointArg): PointArg = {
    val swapped = swap(IndexedSeq(arg.x, arg.y, arg.z, arg.color), IndexedSeq(arg2.x, arg2.y, arg2.z, arg2.color))
    arg.copy(x = swapped(0).asInstanceOf[Int], y = swapped(1).asInstanceOf[Int],
      z = swapped(2).asInstanceOf[Int], color = swapped(3).asInstanceOf[Color.Value])
  }

  /* true if arg and arg2 differ in a field checked by equality */
  def unequal(arg: PointArg, arg2: PointArg): Boolean =
    arg.x != arg2.x || arg.y != arg2.y || arg.z != arg2.z || arg.color != arg2.color
}

object ColoredPointTest {
  def name: String = "ColoredPoint"

  /* Creates a T from B */
  def create(arg: PointArg): ColoredPoint = new ColoredPoint(arg.x, arg.y, arg.z, arg.color)

  def gen: Gen[ColoredPoint] = for {
    arg <- FourDColoredPointTest.argGen
  } yield create(arg)
}
