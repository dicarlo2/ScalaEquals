package com.scalaequals.test

import com.scalaequals.Equals

class Point(val x: Int, val y: Int, private val z: Int) {
  override def equals(other: Any): Boolean = Equals.equal(other)
  override def hashCode: Int = Equals.hash
  def canEqual(other: Any): Boolean = other.isInstanceOf[Point]
}

object Color extends Enumeration {
  val Red, Orange, Yellow, Green, Blue, Indigo, Violet = Value
}

class ColoredPoint(x: Int, y: Int, z: Int, val color: Color.Value) extends Point(x, y, z) {
  override def equals(other: Any): Boolean = Equals.equalC(other)
  override def hashCode: Int = Equals.hash
  override def canEqual(other: Any): Boolean = other.isInstanceOf[ColoredPoint]
}

class FourDColoredPoint(w: Int, x: Int, y: Int, z: Int, color: Color.Value) extends ColoredPoint(x, y, z, color) {
  override def equals(other: Any): Boolean = Equals.equal(other)
  override def hashCode: Int = Equals.hash
  override def canEqual(other: Any): Boolean = other.isInstanceOf[FourDColoredPoint]
}