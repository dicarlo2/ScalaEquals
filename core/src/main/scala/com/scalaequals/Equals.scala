package com.scalaequals

import reflect.macros.Context
import reflect.internal.Symbols
import scala.language.experimental.macros

object Equals {
  def equal[T](other: Any): Boolean = macro EqualsImpl.equalImpl[T]

  def equalC[T](other: Any): Boolean = macro EqualsImpl.equalCImpl[T]

  def equal[T](other: Any, param: Any, params: Any*): Boolean = macro EqualsImpl.equalParamImpl[T]
}