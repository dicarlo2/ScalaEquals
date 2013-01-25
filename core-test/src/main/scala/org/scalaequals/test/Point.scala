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

class Point(val x: Int, val y: Int, val z: Int) {
  override def hashCode: Int = ScalaEquals.hash
  override def equals(other: Any): Boolean = ScalaEquals.equalAllVals
  def canEqual(other: Any): Boolean = ScalaEquals.canEquals
  override def toString: String = s"Point($x, $y, $z)"
}

object Color extends Enumeration {
  val Red, Orange, Yellow, Green, Blue, Indigo, Violet = Value
}

class ColoredPoint(x: Int, y: Int, z: Int, val color: Color.Value) extends Point(x, y, z) {
  override def equals(other: Any): Boolean = ScalaEquals.equal
  override def hashCode: Int = ScalaEquals.hash
  override def canEqual(othr: Any): Boolean = ScalaEquals.canEquals
  override def toString: String = s"ColoredPoint($x, $y, $z, $color)"
}

class FourDColoredPoint(val w: Int, x: Int, y: Int, z: Int, color: Color.Value) extends ColoredPoint(x, y, z, color) {
  override def equals(other: Any): Boolean = ScalaEquals.equalAllVals
  override def hashCode: Int = ScalaEquals.hash
  override def canEqual(a: Any): Boolean = ScalaEquals.canEquals
  override def toString: String = s"FourDColoredPoint($w, $x, $y, $z, $color)"
}