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

class ColoredPointTest extends EqualsFixture[ColoredPoint] {
  def name: String = "ColoredPoint"
  def classGen: Gen[ColoredPoint] = ColoredPointTest.classGen
  def equal2ClassGen: Gen[(ColoredPoint, ColoredPoint)] = ColoredPointTest.equal2ClassGen
  def equal3ClassGen: Gen[(ColoredPoint, ColoredPoint, ColoredPoint)] = ColoredPointTest.equal3ClassGen
  override def subClassName: String = "FourDColoredPoint"
  override def subClassGen: Option[Gen[FourDColoredPoint]] = Some(FourDColoredPointTest.classGen)

  feature("equal calls super.equals if it is available (and not Object or AnyRef)") {
    scenario("ColoredPoints with different (private) z values") {
      Given("2 ColoredPoints, x and y, with different z values")
      val x = new ColoredPoint(1, 2, 3, Color.Blue)
      val y = new ColoredPoint(1, 2, 4, Color.Blue)
      When("x.equals(y)")
      Then("the result is false")
      x.equals(y) should be(false)
    }
  }
}

object ColoredPointTest {
  private[test] def createCP(arg: (Int, Int, Int, Int, Color.Value)): ColoredPoint = arg match {
    case (_, x, y, z, color) => new ColoredPoint(x, y, z, color)
  }

  private[test] def classGen: Gen[ColoredPoint] = for {
    arg <- FourDColoredPointTest.argGen
  } yield createCP(arg)

  private[test] def equal2ClassGen: Gen[(ColoredPoint, ColoredPoint)] = for {
    arg <- FourDColoredPointTest.argGen
  } yield (createCP(arg), createCP(arg))

  private[test] def equal3ClassGen: Gen[(ColoredPoint, ColoredPoint, ColoredPoint)] = for {
    arg <- FourDColoredPointTest.argGen
  } yield (createCP(arg), createCP(arg), createCP(arg))
}
