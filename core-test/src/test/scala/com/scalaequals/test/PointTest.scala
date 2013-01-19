package com.scalaequals.test

import org.scalacheck.Arbitrary._
import org.scalacheck.Gen

class PointTest extends EqualsFixture[Point] {
  def name: String = "Point"
  def classGen: Gen[Point] = for {
    x <- arbitrary[Int]
    y <- arbitrary[Int]
    z <- arbitrary[Int]
  } yield new Point(x, y, z)
  override def subClassName: String = "ColoredPoint"
  override def subClassGen: Option[Gen[ColoredPoint]] = Some(for {
    x <- arbitrary[Int]
    y <- arbitrary[Int]
    z <- arbitrary[Int]
    color <- Gen.oneOf(Color.values.toSeq)
  } yield new ColoredPoint(x, y, z, color))
}
