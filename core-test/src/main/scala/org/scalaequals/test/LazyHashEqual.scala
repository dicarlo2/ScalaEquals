package org.scalaequals.test

import org.scalaequals.ScalaEquals

class LazyHashEqual(val a: Int, b: Int, private val c: Int, protected val d: Int) {
  override lazy val hashCode: Int = ScalaEquals.hash
  override def equals(other: Any): Boolean = ScalaEquals.equal
  override def toString: String = ScalaEquals.genString
  def canEqual(other: Any): Boolean = ScalaEquals.canEquals
}
