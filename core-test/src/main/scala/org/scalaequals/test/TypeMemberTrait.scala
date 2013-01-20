package org.scalaequals.test

import org.scalaequals.ScalaEquals

trait TypeMemberTrait {
  type A
  val a: A
  protected val b: A
  private val c: A = a
  var d: A
  protected var e: A
  private var f: A = a
  def g: A
  protected def h: A
  private def i: A = a

  override def equals(other: Any): Boolean = ScalaEquals.equalAllVals(other)
  override def hashCode(): Int = ScalaEquals.hash
  def canEqual(other: Any): Boolean = ScalaEquals.canEquals(other)
  override def toString: String = s"TypeMemberTrait($a)"
}
