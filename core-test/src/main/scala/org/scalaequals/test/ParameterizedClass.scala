package org.scalaequals.test

import org.scalaequals.ScalaEquals

class ParameterizedClass[A, B](val x: A, val y: B) {
  override def equals(other: Any): Boolean = ScalaEquals.equal
  override def hashCode(): Int = ScalaEquals.hash
  def canEqual(other: Any): Boolean = ScalaEquals.canEquals
  override def toString: String = s"ParameterizedClass($x, $y)"
}