package org.scalaequals.test

import org.scalaequals.ScalaEquals

// Equals on a, b, c (construct with a, b, g)
trait TypeMemberTrait {
  type A
  val a: A
  protected val b: A
  private val c: A = g
  var d: A
  protected var e: A
  private var f: A = e
  def g: A
  protected def h: A
  private def i: A = h

  override def equals(other: Any): Boolean = ScalaEquals.equalAllVals
  override def hashCode(): Int = ScalaEquals.hash
  def canEqual(some: Any): Boolean = ScalaEquals.canEquals
  override def toString: String = "TypeMemberTrait(\"%s\", \"%s\", \"%s\")".format(a, b, d)
}
