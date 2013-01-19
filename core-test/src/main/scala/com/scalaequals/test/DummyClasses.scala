package com.scalaequals.test

import com.scalaequals.Equals

class Dummy(
  a: Int,
  val b: Int,
  protected val c: Int,
  private val d: Int,
  var e: Int,
  private var f: Int,
  protected var g: Int) {

  val h: Int = a
  protected val i: Int = b
  private val j: Int = c
  var k: Int = d
  protected val l: Int = e
  private val m: Int = f
  def n: Int = g
  protected def o: Int = i
  private def p: Int = j

  override def equals(other: Any): Boolean = Equals.equal[Dummy](other)

  def canEqual(other: Any): Boolean = other.isInstanceOf[Dummy]

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
  var y: Int)
  extends Dummy(a, b, c, d, e, f, g) {
  override val h: Int = b
  override def o: Int = a

  override def equals(other: Any): Boolean = Equals.equal[DummySub](other)

  override def canEqual(other: Any): Boolean = other.isInstanceOf[DummySub]

  override def toString: String = s"DummySub($a, $b, $c, $d, $e, $f, $g)"
}

class DummyC(
  a: Int,
  val b: Int,
  protected val c: Int,
  private val d: Int,
  var e: Int,
  private var f: Int,
  protected var g: Int) {

  val h: Int = a
  protected val i: Int = b
  private val j: Int = c
  var k: Int = d
  protected val l: Int = e
  private val m: Int = f
  def n: Int = g
  protected def o: Int = i
  private def p: Int = j

  override def equals(other: Any): Boolean = Equals.equalC[DummyC](other)

  def canEqual(other: Any): Boolean = other.isInstanceOf[DummyC]

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
  var y: Int)
  extends DummyC(a, b, c, d, e, f, g) {
  override val h: Int = b
  override def o: Int = a

  override def equals(other: Any): Boolean = Equals.equalC[DummyCSub](other)

  override def canEqual(other: Any): Boolean = other.isInstanceOf[DummyCSub]

  override def toString: String = s"DummyCSub($a, $b, $c, $d, $e, $f, $g)"
}

class DummyParams(
  a: Int,
  val b: Int,
  protected val c: Int,
  private val d: Int,
  var e: Int,
  private var f: Int,
  protected var g: Int) {

  val h: Int = a
  protected val i: Int = b
  private val j: Int = c
  var k: Int = d
  protected val l: Int = e
  private val m: Int = f
  def n: Int = g
  protected def o: Int = i
  private def p: Int = j

  override def equals(other: Any): Boolean = Equals.equal[DummyParams](other, b, c, e, f, k)

  def canEqual(other: Any): Boolean = other.isInstanceOf[DummyParams]

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
  var y: Int)
  extends DummyParams(a, b, c, d, e, f, g) {
  override val h: Int = b
  override def o: Int = a

  override def equals(other: Any): Boolean = Equals.equal[DummyParamsSub](other, a, b, h, x, y)

  override def canEqual(other: Any): Boolean = other.isInstanceOf[DummyParamsSub]

  override def toString: String = s"DummyParamsSub($a, $b, $c, $d, $e, $f, $g)"
}