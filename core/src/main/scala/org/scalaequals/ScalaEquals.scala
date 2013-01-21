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

import scala.language.experimental.macros

/** Entry point for ScalaEquals
  *
  * Examples:
  * Test equality with only vals in constructor:
  * {{{
  *   class Test(x: Int, val y: Int, private val z: Int, var a: Int) {
  *     val w: Int = x * z      // Ignored by equal
  *     override def equals(other: Any) = ScalaEquals.equal(other)
  *     override def hashCode() = ScalaEquals.hash
  *     def canEqual(other: Any) = ScalaEquals.canEquals(other)
  *   }
  *
  *   new Test(0, 1, 2, 3) == new Test(1, 1, 2, 4) // true -> y == y and z == z
  *   new Test(0, 2, 2, 3) == new Test(1, 1, 2, 4) // false -> y != y and z == z
  * }}}
  *
  *
  * Test equality with vals in constructor AND body:
  * {{{
  *   class Test(x: Int, val y: Int, private val z: Int, var a: Int) {
  *     val w: Int = x * z
  *     def q: Int = x        // Ignored by equalAllVals
  *     override def equals(other: Any) = ScalaEquals.equalAllVals(other)
  *     override def hashCode() = ScalaEquals.hash
  *     def canEqual(other: Any) = ScalaEquals.canEquals(other)
  *   }
  *
  *   new Test(0, 1, 2, 3) == new Test(1, 1, 2, 4) // false -> y == y and z == z and w != w
  *   new Test(0, 2, 2, 3) == new Test(1, 1, 2, 4) // false -> y != y and z == z and w != w
  *   new Test(1, 1, 2, 3) == new Test(1, 1, 2, 4) // true -> y == y and z == z and w == w
  * }}}
  *
  * Test equality with selected parameters:
  * {{{
  *   class Test(x: Int, val y: Int, private val z: Int, var a: Int) {
  *     def w: Int = x * z
  *     override def equals(other: Any) = ScalaEquals.equal(other, w, a)
  *     override def hashCode() = ScalaEquals.hash
  *     def canEqual(other: Any) = ScalaEquals.canEquals(other)
  *   }
  *
  *   new Test(1, 2, 2, 3) == new Test(1, 1, 2, 4) // false -> w == w and a != a
  *   new Test(1, 2, 2, 4) == new Test(1, 1, 2, 4) // true -> w == w and a == a
  * }}}
  *
  * Specifically, the above example (test equality with selected parameters) is converted
  * to the following code:
  * {{{
  *   class Test(x: Int, val y: Int, private val z: Int, var a: Int) {
  *     def w: Int = x * z
  *     override def equals(other: Any) = other match {
  *       case that: Test => (that canEqual this) && that.w == this.w && that.a == this.a
  *       case _ => false
  *     }
  *     override def hashCode() = Objects.hash(Seq(w, z))
  *     def canEqual(other: Any) = other.isInstanceOf[Test]
  *   }
  *
  *   new Test(1, 2, 2, 3) == new Test(1, 1, 2, 4) // false -> w == w and a != a
  *   new Test(1, 2, 2, 4) == new Test(1, 1, 2, 4) // true -> w == w and a == a
  * }}}
  *
  *
  * @author Alex DiCarlo
  * @version 1.0.0
  * @since 0.1.0
  */
object ScalaEquals {
  /**
   * Equality check using all private/protected/public of the class
   * defined in the constructor. Does not use inherited or overriden vals.
   * Do not use with traits, it will only generate a `super.equals(that)`
   * call, instead use `equalAllVals`
   *
   * @param other the instance to compare to
   * @return true if instance.equals(other)
   */
  def equal(other: Any): Boolean = macro EqualsImpl.equalImpl

  /**
   * Equality check using all private/protected/public/lazy vals of the class
   * defined in the constructor AND the body of the class. Does not use
   * inherited or overriden vals.
   *
   * @param other the instance to compare to
   * @return true if instance.equals(other)
   */
  def equalAllVals(other: Any): Boolean = macro EqualsImpl.equalAllValsImpl

  /**
   * Equality check using only parameters passed in to test for equality.
   *
   * Acceptable arguments include private/protected/public vals, vars, lazy vals,
   * and defs with no arguments.
   *
   * @param other the instance to test for equality
   * @param param first param to test with
   * @param params rest of the params
   * @return true if instance.equals(other)
   */
  def equal(other: Any, param: Any, params: Any*): Boolean = macro EqualsImpl.equalParamImpl

  /**
   * Looks up the elements tested in `equals` (including `super.equals`) and uses them
   * in `java.util.Objects.hash(elements)`. Works with all 3 forms of `equal`. Does not
   * work with custom `equals` implementations, one of `ScalaEquals.equal(other)`,
   * `ScalaEquals.equal(other, params)`, or `ScalaEquals.equal(other)` must be used
   *
   *
   * '''MUST BE CALLED AFTER ScalaEquals.equal IN THE CLASS DEFINITION.'''
   *
   * @return hashCode generated from fields used in `equals`
   */
  def hash: Int = macro HashCodeImpl.hash

  /**
   * Simple macro that expands to the following:
   * {{{
   *   other.isInstanceOf[Class]
   * }}}
   *
   * @param other the instance to test for equality
   * @return true if other.isInstanceOf[Class]
   */
  def canEquals(other: Any): Boolean = macro CanEqualImpl.canEquals
}