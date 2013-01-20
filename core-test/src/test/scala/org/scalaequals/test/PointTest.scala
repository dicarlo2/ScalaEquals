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
import scala.Some

class PointTest extends EqualsFixture[Point] {
  def name: String = "Point"
  def createP(arg: (Int, Int, Int, Int, Color.Value)): Point = arg match {
    case (_, x, y, z, _) => new Point(x, y, z)
  }
  def classGen: Gen[Point] = for {
    arg <- FourDColoredPointTest.argGen
  } yield createP(arg)
  override def subClassName: String = "ColoredPoint"
  override def subClassGen: Option[Gen[ColoredPoint]] = Some(ColoredPointTest.classGen)
  def equal2ClassGen: Gen[(Point, Point)] = for {
    arg <- FourDColoredPointTest.argGen
  } yield (createP(arg), createP(arg))
  def equal3ClassGen: Gen[(Point, Point, Point)] = for {
    arg <- FourDColoredPointTest.argGen
  } yield (createP(arg), createP(arg), createP(arg))
}
