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

class FourDColoredPointTest extends EqualsFixture[FourDColoredPoint] {
  def name: String = "FourDColoredPoint"
  def classGen: Gen[FourDColoredPoint] =
    FourDColoredPointTest.classGen
  def equal2ClassGen: Gen[(FourDColoredPoint, FourDColoredPoint)] =
    FourDColoredPointTest.equal2ClassGen
  def equal3ClassGen: Gen[(FourDColoredPoint, FourDColoredPoint, FourDColoredPoint)] =
    FourDColoredPointTest.equal3ClassGen

  feature("equal calls super.equals if available (and not Object or AnyRef") {
    scenario("FourDColoredPoint with different (private) z values") {
      Given("2 FourDColoredPoint, x and y, with different z values")
      val x = new FourDColoredPoint(1, 2, 3, 4, Color.Blue)
      val y = new FourDColoredPoint(1, 2, 3, 5, Color.Blue)
      When("x.equals(y)")
      Then("the result is false")
      x.equals(y) should be(false)
    }
  }
}

object FourDColoredPointTest {
  private[test] val argGen: Gen[(Int, Int, Int, Int, Color.Value)] = for {
    w <- arbitrary[Int]
    x <- arbitrary[Int]
    y <- arbitrary[Int]
    z <- arbitrary[Int]
    color <- Gen.oneOf(Color.values.toSeq)
  } yield (w, x, y, z, color)

  private[test] def create4DP(arg: (Int, Int, Int, Int, Color.Value)): FourDColoredPoint = arg match {
    case (w, x, y, z, color) => new FourDColoredPoint(w, x, y, z, color)
  }

  private[test] def classGen: Gen[FourDColoredPoint] = for {
    arg <- FourDColoredPointTest.argGen
  } yield create4DP(arg)

  private[test] def equal2ClassGen: Gen[(FourDColoredPoint, FourDColoredPoint)] = for {
    arg <- FourDColoredPointTest.argGen
  } yield (create4DP(arg), create4DP(arg))

  private[test] def equal3ClassGen: Gen[(FourDColoredPoint, FourDColoredPoint, FourDColoredPoint)] = for {
    arg <- FourDColoredPointTest.argGen
  } yield (create4DP(arg), create4DP(arg), create4DP(arg))
}
