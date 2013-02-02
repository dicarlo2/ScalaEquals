package org.scalaequals.test

import org.scalaequals.ScalaEquals

class LazyHashEqual(val a: Int, b: Int, private val c: Int, protected val d: Int, e: Int) {
  private lazy val f: Int = e
  override lazy val hashCode: Int = ScalaEquals.hash
  override def equals(other: Any): Boolean = ScalaEquals.equalAllVals
  override def toString: String = ScalaEquals.genString
  def canEqual(other: Any): Boolean = ScalaEquals.canEquals
}
