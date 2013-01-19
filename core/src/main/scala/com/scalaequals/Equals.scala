package com.scalaequals

import scala.language.experimental.macros

// Need to make it work with `def` and use owner field instead of weakTypeTag to do everything
// Need to add hashCode and canEqual implementations
object Equals {
  def equal(other: Any): Boolean = macro EqualsImpl.equalImpl

  def equalC(other: Any): Boolean = macro EqualsImpl.equalCImpl

  def equal(other: Any, param: Any, params: Any*): Boolean = macro EqualsImpl.equalParamImpl
}