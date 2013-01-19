package com.scalaequals.test

import com.scalaequals.Equals

// Tests that canEqual is not called if it is not available
final class StringHolder(val string: String) {
  override def equals(other: Any): Boolean = Equals.equal(other)
}
