package com.scalaequals.test

import com.scalaequals.ScalaEquals

// Tests that canEqual is not called if it is not available
final class StringHolder(val string: String) {
  override def equals(other: Any): Boolean = ScalaEquals.equalAllVals(other)
  override def hashCode: Int = ScalaEquals.hash
}
