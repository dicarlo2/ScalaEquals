package com.scalaequals.test

import org.scalacheck.Gen
import org.scalacheck.Arbitrary._

class FourDColoredPointTest extends EqualsFixture[FourDColoredPoint] {
  def name: String = "FourDColoredPoint"
  def classGen: Gen[FourDColoredPoint] = for {
    w <- arbitrary[Int]
    x <- arbitrary[Int]
    y <- arbitrary[Int]
    z <- arbitrary[Int]
    color <- Gen.oneOf(Color.values.toSeq)
  } yield new FourDColoredPoint(w, x, y, z, color)

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
