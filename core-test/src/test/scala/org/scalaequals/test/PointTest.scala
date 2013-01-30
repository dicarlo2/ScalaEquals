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

case class PointArg(w: Int, x: Int, y: Int, z: Int, color: Color.Value)

trait PointFixture[A] extends EqualsFixture[A, PointArg] {
  /* Swaps all constructor arguments that are not part of equals from arg to arg2's values */
  def changeDiff(arg: PointArg, arg2: PointArg): PointArg = arg

  def gen: Gen[PointArg] = FourDColoredPointTest.argGen

  /* true if arg and arg2 differ in a field not checked by equality or there are no fields that can differ */
  def diff(arg: PointArg, arg2: PointArg): Boolean = true
}

class PointTest extends PointFixture[Point] with SubClassedEqualsFixture[Point, PointArg, ColoredPoint] {
  def name: String = "Point"

  override def subClassName: String = ColoredPointTest.name

  /* Creates a T from B */
  def create(arg: PointArg): Point = new Point(arg.x, arg.y, arg.z)

  def createSubClass(arg: PointArg): ColoredPoint = ColoredPointTest.create(arg)

  /* Creates a String to test toString = A(arg) */
  def createToString(arg: PointArg): String = s"Point(${arg.x}, ${arg.y}, ${arg.z})"

  /* Changes one random argument that is part of equals to arg2's value */
  def changeRandom(arg: PointArg, arg2: PointArg): PointArg = {
    val swapped = swap(IndexedSeq(arg.x, arg.y, arg.z), IndexedSeq(arg2.x, arg2.y, arg2.z))
    arg.copy(x = swapped(0), y = swapped(1), z = swapped(2))
  }

  /* true if arg and arg2 differ in a field checked by equality */
  def unequal(arg: PointArg, arg2: PointArg): Boolean = arg.x != arg2.x || arg.y != arg2.y || arg.z != arg2.z
}
