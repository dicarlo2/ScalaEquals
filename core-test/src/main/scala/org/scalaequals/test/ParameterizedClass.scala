package org.scalaequals.test

import org.scalaequals.ScalaEquals.Equal
import org.scalaequals.ScalaEquals

class ParameterizedClass[A, B](val x: A, val y: B) extends Equal {
  override def toString: String = ScalaEquals.genString
}