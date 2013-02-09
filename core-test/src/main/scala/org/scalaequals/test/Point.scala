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

import org.scalaequals.ScalaEquals

class Point(val x: Int, val y: Int, val z: Int) extends Product {
  override def hashCode: Int = ScalaEquals.hash
  override def equals(other: Any): Boolean = ScalaEquals.equalAllVals
  def canEqual(other: Any): Boolean = ScalaEquals.canEquals
  override def toString: String = ScalaEquals.genString
  def productElement(n: Int): Any = ScalaEquals.productElement
  def productArity: Int = ScalaEquals.productArity
  override def productPrefix: String = ScalaEquals.productPrefix
}

object Color extends Enumeration {
  val Red, Orange, Yellow, Green, Blue, Indigo, Violet = Value
}

class ColoredPoint(x: Int, y: Int, z: Int, val color: Color.Value) extends Point(x, y, z) {
  override def equals(other: Any): Boolean = ScalaEquals.equal
  override def hashCode: Int = ScalaEquals.hash
  override def canEqual(othr: Any): Boolean = ScalaEquals.canEquals
  override def toString: String = ScalaEquals.genString
}

class FourDColoredPoint(val w: Int, x: Int, y: Int, z: Int, color: Color.Value) extends ColoredPoint(x, y, z, color) {
  override def equals(other: Any): Boolean = ScalaEquals.equalAllVals
  override def hashCode: Int = ScalaEquals.hash
  override def canEqual(a: Any): Boolean = ScalaEquals.canEquals
  override def toString: String = ScalaEquals.genString
}

class FloatPoint(val x: Float, val y: Float, val z: Float) {
  override def hashCode: Int = ScalaEquals.hash
  override def equals(other: Any): Boolean = ScalaEquals.equalAllVals
  def canEqual(other: Any): Boolean = ScalaEquals.canEquals
  override def toString: String = ScalaEquals.genString
}

class ColoredFloatPoint(x: Float, y: Float, z: Float, val color: Color.Value) extends FloatPoint(x, y, z) {
  override def equals(other: Any): Boolean = ScalaEquals.equal
  override def hashCode: Int = ScalaEquals.hash
  override def canEqual(othr: Any): Boolean = ScalaEquals.canEquals
  override def toString: String = ScalaEquals.genString
}

class FourDColoredFloatPoint(val w: Float, x: Float, y: Float, z: Float, color: Color.Value)
  extends ColoredFloatPoint(x, y, z, color) {
  override def equals(other: Any): Boolean = ScalaEquals.equalAllVals
  override def hashCode: Int = ScalaEquals.hash
  override def canEqual(a: Any): Boolean = ScalaEquals.canEquals
  override def toString: String = ScalaEquals.genString
}

class DoublePoint(val x: Double, val y: Double, val z: Double) {
  override def hashCode: Int = ScalaEquals.hash
  override def equals(other: Any): Boolean = ScalaEquals.equalAllVals
  def canEqual(other: Any): Boolean = ScalaEquals.canEquals
  override def toString: String = ScalaEquals.genString
}

class ColoredDoublePoint(x: Double, y: Double, z: Double, val color: Color.Value) extends DoublePoint(x, y, z) {
  override def equals(other: Any): Boolean = ScalaEquals.equal
  override def hashCode: Int = ScalaEquals.hash
  override def canEqual(othr: Any): Boolean = ScalaEquals.canEquals
  override def toString: String = ScalaEquals.genString
}

class FourDColoredDoublePoint(val w: Double, x: Double, y: Double, z: Double, color: Color.Value) extends ColoredDoublePoint(x, y, z, color) {
  override def equals(other: Any): Boolean = ScalaEquals.equalAllVals
  override def hashCode: Int = ScalaEquals.hash
  override def canEqual(a: Any): Boolean = ScalaEquals.canEquals
  override def toString: String = ScalaEquals.genString
}