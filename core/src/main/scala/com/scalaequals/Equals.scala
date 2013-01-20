package com.scalaequals

import scala.language.experimental.macros

object Equals {
  /**
   * Equality check using all `val`s of class, private/protected/public and defined in constructor or in body.
   *
   * @param other
   * @return true if instance.equals(other)
   */
  def equal(other: Any): Boolean = macro EqualsImpl.equalImpl

  /**
   * Equality check using all `val`s of constructor, private/protected/public
   *
   * @param other
   * @return true if instance.equals(other)
   */
  def equalC(other: Any): Boolean = macro EqualsImpl.equalCImpl

  /**
   * Equality check using only parameters passed in to test for equality. Example:
   *
   * final class Test(val x: Int, var y: Int) {
   *   override def equals(other: Any): Boolean = Equals.equal(other, x, y)
   * }
   *
   * Can use private/protected/public vals, vars, lazy vals, and defs with no arguments
   *
   * @param other
   * @param param first param to test with
   * @param params rest of params
   * @return true if instance.equals(other)
   */
  def equal(other: Any, param: Any, params: Any*): Boolean = macro EqualsImpl.equalParamImpl

  def hash: Int = macro HashImpl.hash

  def canEqual(other: Any): Boolean = macro CanEqualImpl.canEqual
}