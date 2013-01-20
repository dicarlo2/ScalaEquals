package com.scalaequals.test

import com.scalaequals.Equals

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

  override def equals(other: Any): Boolean = Equals.equalAllVals(other)

  def canEqual(other: Any): Boolean = Equals.canEqual(other)

  override def hashCode: Int = Equals.hash

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

  override def equals(other: Any): Boolean = Equals.equalAllVals(other)

  override def canEqual(other: Any): Boolean = Equals.canEqual(other)

  override def hashCode: Int = Equals.hash

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

  override def equals(other: Any): Boolean = Equals.equal(other)

  def canEqual(other: Any): Boolean = Equals.canEqual(other)

  override def hashCode: Int = Equals.hash

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

  override def equals(other: Any): Boolean = Equals.equal(other)

  override def canEqual(other: Any): Boolean = Equals.canEqual(other)

  override def hashCode: Int = Equals.hash

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

  override def equals(other: Any): Boolean = Equals.equal(other, b, c, e, f, k, n, o, p, q, r, s)

  def canEqual(other: Any): Boolean = Equals.canEqual(other)

  override def hashCode: Int = Equals.hash

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

  override def equals(other: Any): Boolean = Equals.equal(other, a, b, h, x, y, o, c)

  override def canEqual(other: Any): Boolean = Equals.canEqual(other)

  override def hashCode: Int = Equals.hash

  override def toString: String = s"DummyParamsSub($a, $b, $c, $d, $e, $f, $g)"
}