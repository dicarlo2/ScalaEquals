package org.scalaequals.test

import org.scalaequals.ScalaEquals

class ParameterizedClass[A, +B, -C](val x: A, val y: B) {
  def testContravariant(param: C): Boolean = true
  override def equals(other: Any): Boolean = ScalaEquals.equal
  override def hashCode(): Int = ScalaEquals.hash
  def canEqual(other: Any): Boolean = ScalaEquals.canEquals
  override def toString: String = ScalaEquals.genString
}