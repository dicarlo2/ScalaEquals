/*
 * Copyright (c) 2013 Alex DiCarlo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.scalaequals

import org.scalaequals.impl.{HashCodeImpl, EqualsImpl}
import scala.language.experimental.macros

/** Entry point for custom ScalaEquals macros. Functions here are not guaranteed
  * to produce correct equals/hashCode implementations if used incorrectly.
  * See individual function documentation for additional details.
  *
  * @author Alex DiCarlo
  * @version 1.2.0
  * @since 1.2.0
  */
object ScalaEqualsExtend {
  /**
   * Equality check using all private/protected/public vals of the class
   * defined in the constructor. Does not use inherited or overriden vals.
   * Do not use with traits, it will only generate a `super.equals(that)`
   * call, instead use `equalAllVals`. Does NOT use `compareTo` for `Float`
   * and `Double` values
   *
   * @return true if instance.equals(other)
   */
  def equalNoCompareTo: Boolean = macro EqualsImpl.equalImplNoCompareTo

  /**
   * Equality check using all private/protected/public/lazy vals of the class
   * defined in the constructor AND the body of the class. Does not use
   * inherited or overriden vals. Does NOT use `compareTo` for `Float`
   * and `Double` values
   *
   * @param compareTo true if `compareTo` should be used for `Float` and
   *                  `Double` values
   * @return true if instance.equals(other)
   */
  def equalAllValsNoCompareTo: Boolean = macro EqualsImpl.equalAllValsImplNoCompareTo

  /**
   * Equality check using only parameters passed in to test for equality.
   *
   * Acceptable arguments include private/protected/public vals, vars, lazy vals,
   * and defs with no arguments. Does NOT use `compareTo` for `Float` and `Double`
   * values
   *
   * @param param first param to test with
   * @param params rest of the params
   * @return true if instance.equals(other)
   */
  def equalNoCompareTo(param: Any, params: Any*): Boolean = macro EqualsImpl.equalParamImplNoCompareTo

  /**
   * Equality check using only parameters passed in to test for equality.
   *
   * Acceptable arguments include private/protected/public vals, vars, lazy vals,
   * and defs with no arguments. Do note that it is possible the implementation created
   * by this macro will be inconsistent with the `equals`/`hashCode` contract if you
   * use `var`s or `def`s where the value changes. Use this function only if you know
   * what you are doing, and proceed with caution.
   *
   * @param param first param to test with
   * @param params rest of the params
   * @return true if instance.equals(other)
   */
  def equal(param: Any, params: Any*): Boolean = macro EqualsImpl.equalParamImpl

  /**
   * Looks up the elements tested in `equals` (including `super.equals`) and uses them
   * in `hashFunction(Array(elements))`. Works with all 3 forms of `equal`. Does not
   * work with custom `equals` implementations, one of `ScalaEquals.equal`,
   * `ScalaEquals.equal(params)`, or `ScalaEquals.equalAllVals` must be used (or their compareTo
   * equivalents). Use this function only if you know what you are doing, and proceed with caution.
   *
   * @param hashFunction to use to produce a hashCode for the elements used in `equals`
   * @return hashCode generated from fields used in `equals`
   */
  def hash(hashFunction: Array[Any] => Int): Int = macro HashCodeImpl.customHash
}
