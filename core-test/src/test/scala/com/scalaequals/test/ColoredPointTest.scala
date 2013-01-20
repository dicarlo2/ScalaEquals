package com.scalaequals.test

import org.scalacheck.Gen
import org.scalacheck.Arbitrary.arbitrary

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
