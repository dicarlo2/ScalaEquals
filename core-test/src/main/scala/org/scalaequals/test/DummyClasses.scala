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

class Dummy(
    a: Int,
    val b: Int,
    protected val c: Int,
    private val d: Int,
    var e: Int,
    private var f: Int,
    protected var g: Int,
    t: => Int) {
  val h: Int = a
  protected val i: Int = b
  private val j: Int = c
  var k: Int = d
  protected val l: Int = e
  private val m: Int = f
  def n: Int = g
  protected def o: Int = h
  private def p: Int = i
  lazy val q: Int = k
  protected lazy val r: Int = l
  private lazy val s: Int = m

  override def equals(other: Any): Boolean = ScalaEquals.equalAllVals(other)

  def canEqual(other: Any): Boolean = ScalaEquals.canEquals(other)

  override def hashCode: Int = ScalaEquals.hash

  override def toString: String = s"Dummy($a, $b, $c, $d, $e, $f, $g)"
}

class DummySub(
    val a: Int,
    override val b: Int,
    override val c: Int,
    d: Int,
    e: Int,
    f: Int,
    g: Int,
    val x: Int,
    var y: Int,
    t: => Int) extends Dummy(a, b, c, d, e, f, g, t) {
  override val h: Int = b
  override def o: Int = a
  override lazy val q: Int = c

  override def equals(other: Any): Boolean = ScalaEquals.equalAllVals(other)

  override def canEqual(other: Any): Boolean = ScalaEquals.canEquals(other)

  override def hashCode: Int = ScalaEquals.hash

  override def toString: String = s"DummySub($a, $b, $c, $d, $e, $f, $g)"
}

class DummyC(
    a: Int,
    val b: Int,
    protected val c: Int,
    private val d: Int,
    var e: Int,
    private var f: Int,
    protected var g: Int,
    t: => Int) {
  val h: Int = a
  protected val i: Int = b
  private val j: Int = c
  var k: Int = d
  protected val l: Int = e
  private val m: Int = f
  def n: Int = g
  protected def o: Int = i
  private def p: Int = j
  lazy val q: Int = k
  protected lazy val r: Int = l
  private lazy val s: Int = m

  override def equals(other: Any): Boolean = ScalaEquals.equal(other)

  def canEqual(other: Any): Boolean = ScalaEquals.canEquals(other)

  override def hashCode: Int = ScalaEquals.hash

  override def toString: String = s"DummyC($a, $b, $c, $d, $e, $f, $g)"
}

class DummyCSub(
    val a: Int,
    override val b: Int,
    override val c: Int,
    d: Int,
    e: Int,
    f: Int,
    g: Int,
    val x: Int,
    var y: Int,
    t: => Int) extends DummyC(a, b, c, d, e, f, g, t) {
  override val h: Int = b
  override def o: Int = a
  override lazy val q: Int = c

  override def equals(other: Any): Boolean = ScalaEquals.equal(other)

  override def canEqual(other: Any): Boolean = ScalaEquals.canEquals(other)

  override def hashCode: Int = ScalaEquals.hash

  override def toString: String = s"DummyCSub($a, $b, $c, $d, $e, $f, $g)"
}

class DummyParams(
    a: Int,
    val b: Int,
    protected val c: Int,
    private val d: Int,
    var e: Int,
    private var f: Int,
    protected var g: Int,
    t: => Int) {
  val h: Int = a
  protected val i: Int = b
  private val j: Int = c
  var k: Int = d
  protected val l: Int = e
  private val m: Int = f
  def n: Int = g
  protected def o: Int = i
  private def p: Int = j
  lazy val q: Int = k
  protected lazy val r: Int = l
  private lazy val s: Int = m

  override def equals(other: Any): Boolean = ScalaEquals.equal(other, b, c, e, f, k, n, o, p, q, r, s)

  def canEqual(other: Any): Boolean = ScalaEquals.canEquals(other)

  override def hashCode: Int = ScalaEquals.hash

  override def toString: String = s"DummyParams($a, $b, $c, $d, $e, $f, $g)"
}

class DummyParamsSub(
    val a: Int,
    override val b: Int,
    override val c: Int,
    d: Int,
    e: Int,
    f: Int,
    g: Int,
    val x: Int,
    var y: Int,
    t: => Int) extends DummyParams(a, b, c, d, e, f, g, t) {
  override val h: Int = b
  override def o: Int = a
  override lazy val q: Int = c

  override def equals(other: Any): Boolean = ScalaEquals.equal(other, a, b, h, x, y, o, c)

  override def canEqual(other: Any): Boolean = ScalaEquals.canEquals(other)

  override def hashCode: Int = ScalaEquals.hash

  override def toString: String = s"DummyParamsSub($a, $b, $c, $d, $e, $f, $g)"
}