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
import org.scalaequals.ScalaEquals.{Equal, EqualAllVals}

// Equals on b, c, d, h, i, j, q, r, and s
class Dummy(
    a: Int,
    val b: Int,
    protected val c: Int,
    private val d: Int,
    var e: Int,
    private var f: Int,
    protected var g: Int,
    t: => Int,
    _h: Int,
    _q: Int) extends EqualAllVals {
  val h: Int = _h
  protected val i: Int = b
  private val j: Int = c
  var k: Int = a
  protected var l: Int = e
  private var m: Int = t
  def n: Int = g
  protected def o: Int = h
  private def p: Int = i
  lazy val q: Int = _q
  protected lazy val r: Int = e
  private lazy val s: Int = e

  override def toString: String = ScalaEquals.genString(b, c, d, e, _h, _q)
}

// Equals on super, a, x
class DummySub(
    val a: Int,
    override val b: Int,
    c: Int,
    d: Int,
    e: Int,
    f: Int,
    g: Int,
    val x: Int,
    var y: Int,
    t: => Int,
    _h: Int,
    _q: Int) extends Dummy(a, b, c, d, e, f, g, t, _h, _q) {
  override val h: Int = _q
  override def o: Int = a
  override lazy val q: Int = _h

  override def hashCode: Int = ScalaEquals.hash

  override def equals(test: Any): Boolean = ScalaEquals.equalAllVals

  override def canEqual(test: Any): Boolean = ScalaEquals.canEquals

  // TODO: Add `d` back in once SI-7028 is fixed
  override def toString: String = ScalaEquals.genString(a, b, c, e, x, _h, _q)
}

// Equals on b, c, d
class DummyC(
    a: Int,
    val b: Int,
    protected val c: Int,
    private val d: Int,
    var e: Int,
    private var f: Int,
    protected var g: Int,
    t: => Int,
    _h: Int,
    _q: Int) extends Equal {
  val h: Int = _h
  protected val i: Int = b
  private val j: Int = c
  var k: Int = a
  protected var l: Int = e
  private var m: Int = t
  def n: Int = g
  protected def o: Int = h
  private def p: Int = i
  lazy val q: Int = _q
  protected lazy val r: Int = e
  private lazy val s: Int = e

  override def toString: String = ScalaEquals.genString(b, c, d)
}

// Equals on super, a, x
class DummyCSub(
    val a: Int,
    override val b: Int,
    c: Int,
    d: Int,
    e: Int,
    f: Int,
    g: Int,
    val x: Int,
    var y: Int,
    t: => Int,
    _h: Int,
    _q: Int) extends DummyC(a, b, c, d, e, f, g, t, _h, _q) with Equal {
  override val h: Int = _q
  override def o: Int = a
  override lazy val q: Int = _h

//  TODO: Add `d` back in once SI-7028 is fixed
  override def toString: String = ScalaEquals.genString(a, b, c, x)
}

// Equals on b, c, e, f, k, n, o, p, q, r, s
class DummyParams(
    a: Int,
    val b: Int,
    protected val c: Int,
    private val d: Int,
    var e: Int,
    private var f: Int,
    protected var g: Int,
    t: => Int,
    _h: Int,
    _q: Int) {
  val h: Int = _h
  protected val i: Int = b
  private val j: Int = c
  var k: Int = a
  protected var l: Int = e
  private var m: Int = t
  def n: Int = g
  protected def o: Int = h
  private def p: Int = i
  lazy val q: Int = _q
  protected lazy val r: Int = e
  private lazy val s: Int = e

  def canEqual(other: Any): Boolean = ScalaEquals.canEquals

  override def equals(other: Any): Boolean = ScalaEquals.equal(b, c, e, f, k, n, o, p, q, r, s)

  override def hashCode: Int = ScalaEquals.hash

  override def toString: String = ScalaEquals.genString(a, b, c, e, f, g, _h, _q)
}

// Equals on super, a, b, h, y, o
class DummyParamsSub(
    val a: Int,
    override val b: Int,
    c: Int,
    d: Int,
    e: Int,
    f: Int,
    g: Int,
    val x: Int,
    var y: Int,
    t: => Int,
    _h: Int,
    _q: Int) extends DummyParams(a, b, c, d, e, f, g, t, _h, _q) {
  override val h: Int = _q
  override def o: Int = a
  override lazy val q: Int = _h

  override def canEqual(other: Any): Boolean = ScalaEquals.canEquals

  override def hashCode: Int = ScalaEquals.hash

  override def toString: String = ScalaEquals.genString(a, b, c, e, f, g, y, _h, _q)

  override def equals(other: Any): Boolean = ScalaEquals.equal(a, b, h, y, o)
}