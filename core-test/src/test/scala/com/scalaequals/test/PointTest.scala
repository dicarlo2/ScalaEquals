package com.scalaequals.test

import org.scalacheck.Gen

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
