package com.scalaequals

import scala.language.experimental.macros

// Need to make it work with `def` and use owner field instead of weakTypeTag to do everything
// Need to add hashCode and canEqual implementations
object Equals {
  def equal[T](other: Any): Boolean = macro EqualsImpl.equalImpl[T]

  def equalC[T](other: Any): Boolean = macro EqualsImpl.equalCImpl[T]

  def equal[T](other: Any, param: Any, params: Any*): Boolean = macro EqualsImpl.equalParamImpl[T]
}