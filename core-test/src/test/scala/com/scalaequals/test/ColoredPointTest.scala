package com.scalaequals.test

import org.scalacheck.Gen
import org.scalacheck.Arbitrary.arbitrary

class ColoredPointTest extends EqualsFixture[ColoredPoint] {
  def name: String = "ColoredPoint"
  def classGen: Gen[ColoredPoint] =  for {
    x <- arbitrary[Int]
    y <- arbitrary[Int]
    z <- arbitrary[Int]
    color <- Gen.oneOf(Color.values.toSeq)
  } yield new ColoredPoint(x, y, z, color)

  feature("equalC calls super.equals if it is available (and not Object or AnyRef)") {
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
