package com.scalaequals.test

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
