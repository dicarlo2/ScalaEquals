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

package org

/** == ScalaEquals ==
  *
  * ScalaEquals provides easy to use macros for generating correct equals/hashCode/canEquals
  * implementations, never look up an equals/hashCode recipe again!
  * The methods generated from `ScalaEquals` are taken directly from Programming in Scala*
  * and strictly obey the contract of equals:
  *
  *  - Reflexive
  *
  *  - Symmetric
  *
  *  - Transitive
  *
  *
  * As well as the additional properties that equals is consistent (`x.equals(y)` consistently
  * returns one of true or false) and that for any non-null value `x`, `x.equals(null)` always
  * returns false. Additionally, using `ScalaEquals.hash` will guarantee that `hashCode()`
  * is always consistent with `equals`. In the documentation, anywhere `ScalaEquals.equal`
  * is seen it is assumed that `ScalaEquals.equalAllVals` also applies, unless otherwise
  * stated.
  *
  * The typical use case is as follows:
  *
  * {{{
  * class Point(val x: Int, val y: Int) {
  *     override def equals(other: Any): Boolean = ScalaEquals.equal(other)
  *     override def hashCode(): Int = ScalaEquals.hash
  *     def canEquals(other: Any): Boolean = ScalaEquals.canEquals(other)
  * }
  *
  * // After macro expansion, the above is converted to:
  *
  * class Point(val x: Int, val y: Int) {
  *      override def equals(other: Any): Boolean = other match {
  *        case that: Point => (that canEquals this) && that.x == this.x && that.y == this.y
  *        case _ => false
  *      }
  *      override def hashCode(): Int = Objects.hash(Seq(x, y))
  *      def canEquals(other: Any): Boolean = other.isInstanceOf[Point]
  * }
  * }}}
  *
  * Things to note:
  *
  *  - If you define `hashCode()` and wish to use `ScalaEquals.hash` along with `ScalaEquals.equal`
  * (recommended), '''`hashCode()` MUST come after `equals` in the class definition.''' It is a
  * compile time error otherwise. (The 2 macros interact so that exactly the fields/methods used
  * in equals are used in `hashCode()`, and the current implementation requires `equals` come
  * before so that the macro can be expanded prior to `hash`'s expansion)
  *
  *  - The macros will intelligently pick other methods to call. Specifically, if the class extends
  * a class that also override equals, `super.equals(that)` will be called. If `canEquals` is not
  * defined, it will not be added to the `equals` call. `Scala.equals.hash` will call `super.hashCode()`
  * if and only if `super.equals(that)` is called.
  *
  *  - The macros may only be called from their respectively named methods, `ScalaEquals.equal` can
  * only be called in a method named `equals` `ScalaEquals.hash` can only be called in a method named
  * `hashCode()` `ScalaEquals.canEquals` can only be called in a method name `canEquals` This serves
  * as a safety net to ensure these methods are never called somewhere they are not supposed to be.
  *
  * See [[https://github.com/dicarlo2/ScalaEquals the github readme]] for more information.
  * Also, see [[org.scalaequals.ScalaEquals]] for additional examples.
  *
  * * Programming in Scala by Martin Odersky, Lex Spoon and Bill Venners,
  * Artima Press, 2008: Chapter 28 (Object equality) / Second Edition, 2011:
  * Chapter 30 (Object equality)
  *
  * @author Alex DiCarlo
  * @version 1.0.1
  * @since 0.3.0
  */
package object scalaequals {}
