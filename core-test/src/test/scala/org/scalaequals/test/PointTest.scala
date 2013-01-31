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

case class PointArg[A: Ordering](w: A, x: A, y: A, z: A, color: Color.Value)

trait PointFixture[A, B] extends EqualsFixture[A, PointArg[B]] {
  def changeDiff(arg: PointArg[B], arg2: PointArg[B]): PointArg[B] = arg

  def diff(arg: PointArg[B], arg2: PointArg[B]): Boolean = true
}

trait PointTest[A, B, C <: A] extends PointFixture[A, B] with SubClassedEqualsFixture[A, PointArg[B], C] {
  implicit def ord: Ordering[B]

  def createToString(arg: PointArg[B]): String = s"$name(${arg.x}, ${arg.y}, ${arg.z})"

  def changeRandom(arg: PointArg[B], arg2: PointArg[B]): PointArg[B] = {
    val swapped = swap(IndexedSeq(arg.x, arg.y, arg.z), IndexedSeq(arg2.x, arg2.y, arg2.z))
    arg.copy(x = swapped(0), y = swapped(1), z = swapped(2))
  }

  def unequal(arg: PointArg[B], arg2: PointArg[B]): Boolean =
    ord.compare(arg.x, arg2.x) != 0 || ord.compare(arg.y, arg2.y) != 0 || ord.compare(arg.z, arg2.z) != 0
}

trait ColoredPointTest[A, B, C <: A]
  extends PointFixture[A, B] with SubClassedEqualsFixture[A, PointArg[B], C] {
  implicit def ord: Ordering[B]

  def createToString(arg: PointArg[B]): String = s"$name(${arg.x}, ${arg.y}, ${arg.z}, ${arg.color})"

  def changeRandom(arg: PointArg[B], arg2: PointArg[B]): PointArg[B] = {
    val swapped = swap(IndexedSeq(arg.x, arg.y, arg.z, arg.color), IndexedSeq(arg2.x, arg2.y, arg2.z, arg2.color))
    arg.copy(x = swapped(0).asInstanceOf[B], y = swapped(1).asInstanceOf[B],
      z = swapped(2).asInstanceOf[B], color = swapped(3).asInstanceOf[Color.Value])
  }

  def unequal(arg: PointArg[B], arg2: PointArg[B]): Boolean =
    ord.compare(arg.x, arg2.x) != 0 || ord.compare(arg.y, arg2.y) != 0 || ord.compare(arg.z, arg2.z) != 0 ||
      arg.color != arg2.color
}

trait FourDColoredPointTest[A, B] extends PointFixture[A, B] {
  implicit def ord: Ordering[B]

  def createToString(arg: PointArg[B]): String = s"$name(${arg.w}, ${arg.x}, ${arg.y}, ${arg.z}, ${arg.color})"

  def changeRandom(arg: PointArg[B], arg2: PointArg[B]): PointArg[B] = {
    val swapped = swap(IndexedSeq(arg.w, arg.x, arg.y, arg.z, arg.color),
      IndexedSeq(arg2.w, arg2.x, arg2.y, arg2.z, arg2.color))
    arg.copy(w = swapped(0).asInstanceOf[B], x = swapped(1).asInstanceOf[B], y = swapped(2).asInstanceOf[B],
      z = swapped(3).asInstanceOf[B], color = swapped(4).asInstanceOf[Color.Value])
  }

  def unequal(arg: PointArg[B], arg2: PointArg[B]): Boolean =
    ord.compare(arg.w, arg2.w) != 0 || ord.compare(arg.x, arg2.x) != 0 || ord.compare(arg.y, arg2.y) != 0 ||
      ord.compare(arg.z, arg2.z) != 0 || arg.color != arg2.color
}

trait IntPointArgGen {
  def gen: Gen[PointArg[Int]] = for {
    w <- arbitrary[Int]
    x <- arbitrary[Int]
    y <- arbitrary[Int]
    z <- arbitrary[Int]
    color <- Gen.oneOf(Color.values.toSeq)
  } yield PointArg(w, x, y, z, color)

  val ord: Ordering[Int] = implicitly[Ordering[Int]]
}

class IntPointTest extends PointTest[Point, Int, ColoredPoint] with IntPointArgGen {
  def name: String = "Point"

  override def subClassName: String = IntColoredPointTest.name

  def create(arg: PointArg[Int]): Point = new Point(arg.x, arg.y, arg.z)

  def createSubClass(arg: PointArg[Int]): ColoredPoint = IntColoredPointTest.create(arg)
}

object IntColoredPointTest {
  def name: String = "ColoredPoint"

  def create(arg: PointArg[Int]): ColoredPoint = new ColoredPoint(arg.x, arg.y, arg.z, arg.color)
}

class IntColoredPointTest extends ColoredPointTest[ColoredPoint, Int, FourDColoredPoint] with IntPointArgGen {
  def name: String = IntColoredPointTest.name

  def subClassName: String = IntFourDColoredPointTest.name

  def create(arg: PointArg[Int]): ColoredPoint = IntColoredPointTest.create(arg)

  def createSubClass(arg: PointArg[Int]): FourDColoredPoint = IntFourDColoredPointTest.create(arg)
}

object IntFourDColoredPointTest {
  def name: String = "FourDColoredPoint"

  def create(arg: PointArg[Int]): FourDColoredPoint = new FourDColoredPoint(arg.w, arg.x, arg.y, arg.z, arg.color)
}

class IntFourDColoredPointTest extends FourDColoredPointTest[FourDColoredPoint, Int] with IntPointArgGen {
  def name: String = IntFourDColoredPointTest.name

  def create(arg: PointArg[Int]): FourDColoredPoint = IntFourDColoredPointTest.create(arg)
}

trait FloatPointArgGen {
  private def floatGen =
    Gen.chooseNum(Float.MinValue, Float.MaxValue, Float.NaN, Float.NegativeInfinity, Float.PositiveInfinity)

  def gen: Gen[PointArg[Float]] = for {
    w <- floatGen
    x <- floatGen
    y <- floatGen
    z <- floatGen
    color <- Gen.oneOf(Color.values.toSeq)
  } yield PointArg(w, x, y, z, color)

  val ord: Ordering[Float] = implicitly[Ordering[Float]]
}

trait FPointTest[A] extends FloatPointArgGen {self: EqualsFixture[A, PointArg[Float]] =>
  feature("ScalaEquals.equal uses compareTo for Float comparisons") {
    scenario(s"$name") {
      Given(s"2 equal $name x and y with Float.NaN as a value")
      val arg = new PointArg(Float.NaN, Float.NaN, Float.PositiveInfinity, Float.NegativeInfinity, Color.Blue)
      val x = create(arg)
      val y = create(arg)
      When("they are compared")
      Then("x.equals(y) is true and y.equals(x) is true")
      x.equals(y) should be(true)
      y.equals(x) should be(true)
    }
  }
}

class FloatPointTest extends PointTest[FloatPoint, Float, ColoredFloatPoint] with FPointTest[FloatPoint] {
  def name: String = "FloatPoint"

  override def subClassName: String = ColoredFloatPointTest.name

  def create(arg: PointArg[Float]): FloatPoint = new FloatPoint(arg.x, arg.y, arg.z)

  def createSubClass(arg: PointArg[Float]): ColoredFloatPoint = ColoredFloatPointTest.create(arg)
}

object ColoredFloatPointTest {
  def name: String = "ColoredFloatPoint"

  def create(arg: PointArg[Float]): ColoredFloatPoint = new ColoredFloatPoint(arg.x, arg.y, arg.z, arg.color)
}

class ColoredFloatPointTest
  extends ColoredPointTest[ColoredFloatPoint, Float, FourDColoredFloatPoint] with FPointTest[ColoredFloatPoint] {
  def name: String = ColoredFloatPointTest.name

  def subClassName: String = FourDColoredFloatPointTest.name

  def create(arg: PointArg[Float]): ColoredFloatPoint = ColoredFloatPointTest.create(arg)

  def createSubClass(arg: PointArg[Float]): FourDColoredFloatPoint = FourDColoredFloatPointTest.create(arg)
}

object FourDColoredFloatPointTest {
  def name: String = "FourDColoredFloatPoint"

  def create(arg: PointArg[Float]): FourDColoredFloatPoint = new
      FourDColoredFloatPoint(arg.w, arg.x, arg.y, arg.z, arg.color)
}

class FourDColoredFloatPointTest
  extends FourDColoredPointTest[FourDColoredFloatPoint, Float] with FPointTest[FourDColoredFloatPoint] {
  def name: String = FourDColoredFloatPointTest.name

  def create(arg: PointArg[Float]): FourDColoredFloatPoint = FourDColoredFloatPointTest.create(arg)
}

trait DoublePointArgGen {
  private def doubleGen: Gen[Double] = Gen
    .chooseNum(Double.MinValue / 2, Double.MaxValue / 2, Double.NaN, Double.NegativeInfinity, Double.PositiveInfinity)

  def gen: Gen[PointArg[Double]] = for {
    w <- doubleGen
    x <- doubleGen
    y <- doubleGen
    z <- doubleGen
    color <- Gen.oneOf(Color.values.toSeq)
  } yield PointArg(w, x, y, z, color)

  val ord: Ordering[Double] = implicitly[Ordering[Double]]
}

trait DPointTest[A] extends DoublePointArgGen {self: EqualsFixture[A, PointArg[Double]] =>
  feature("ScalaEquals.equal uses compareTo for Double comparisons") {
    scenario(s"$name") {
      Given(s"2 equal $name x and y with Double.NaN as a value")
      val arg = new PointArg(Double.NaN, Double.NaN, Double.PositiveInfinity, Double.NegativeInfinity, Color.Blue)
      val x = create(arg)
      val y = create(arg)
      When("they are compared")
      Then("x.equals(y) is true and y.equals(x) is true")
      x.equals(y) should be(true)
      y.equals(x) should be(true)
    }
  }
}

class DoublePointTest extends PointTest[DoublePoint, Double, ColoredDoublePoint] with DPointTest[DoublePoint] {
  def name: String = "DoublePoint"

  override def subClassName: String = ColoredDoublePointTest.name

  def create(arg: PointArg[Double]): DoublePoint = new DoublePoint(arg.x, arg.y, arg.z)

  def createSubClass(arg: PointArg[Double]): ColoredDoublePoint = ColoredDoublePointTest.create(arg)
}

object ColoredDoublePointTest {
  def name: String = "ColoredDoublePoint"

  def create(arg: PointArg[Double]): ColoredDoublePoint = new ColoredDoublePoint(arg.x, arg.y, arg.z, arg.color)
}

class ColoredDoublePointTest
  extends ColoredPointTest[ColoredDoublePoint, Double, FourDColoredDoublePoint] with DPointTest[ColoredDoublePoint] {
  def name: String = ColoredDoublePointTest.name

  def subClassName: String = FourDColoredDoublePointTest.name

  def create(arg: PointArg[Double]): ColoredDoublePoint = ColoredDoublePointTest.create(arg)

  def createSubClass(arg: PointArg[Double]): FourDColoredDoublePoint = FourDColoredDoublePointTest.create(arg)
}

object FourDColoredDoublePointTest {
  def name: String = "FourDColoredDoublePoint"

  def create(arg: PointArg[Double]): FourDColoredDoublePoint = new
      FourDColoredDoublePoint(arg.w, arg.x, arg.y, arg.z, arg.color)
}

class FourDColoredDoublePointTest
  extends FourDColoredPointTest[FourDColoredDoublePoint, Double] with DPointTest[FourDColoredDoublePoint] {
  def name: String = FourDColoredDoublePointTest.name

  def create(arg: PointArg[Double]): FourDColoredDoublePoint = FourDColoredDoublePointTest.create(arg)
}