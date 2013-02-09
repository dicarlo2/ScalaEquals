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

package org.scalaequals.impl

/** Errors contains all error strings and helper functions for aborting or warning
  *
  * @author Alex DiCarlo
  * @version 1.2.0
  * @since 1.1.1
  */
trait Errors {self: Locator =>
  val warn = !(c.settings contains "scala-equals-no-warn")

  def abortIf(failed: Boolean, msg: String) { if (failed) c.abort(c.enclosingMethod.pos, msg) }
  def warnIf(failed: Boolean, msg: String) { if (failed && warn) c.warning(c.enclosingMethod.pos, msg) }
  def warnClassIf(failed: Boolean, msg: String) { if (failed && warn) c.warning(c.enclosingClass.pos, msg) }

  /* `ScalaEquals.canEquals` must be called from within a `canEqual` method */
  val badCanEqualsCallSite =
    "ScalaEquals.canEquals must be called from within a canEqual method. " +
      "Check that your method signature matches one of \"def canEqual(other: Any): Boolean\" or " +
      "\"override def canEqual(other: Any): Boolean\"."

  /* `ScalaEquals.equal` must be called from within an `equals` method */
  val badEqualCallSite =
    "ScalaEquals.equal must be called from within an equals method. " +
      "Check that your method signature matches \"override def equals(other: Any): Boolean\""

  /* `ScalaEquals.hash` must be called from within a `hashCode` method */
  val badHashCallSite =
    "ScalaEquals.hash must be called from within a hashCode method. " +
      "Check that your method signature matches \"override def hashCode(): Int\""

  /* `ScalaEquals.hash` must be called in a class that also uses `ScalaEquals.equal` or `ScalaEquals.equalAllVals` */
  val missingEqual =
    "No attachments found on equals. Ensure that you are using one of " +
      "ScalaEquals.equal or ScalaEquals.equalAllVals in your #equals method"

  /* `ScalaEquals.hash` must be called in a class that defines `equals` */
  val missingEquals =
    "No equals method found! Ensure you are overriding equals and that it has the correct signature."

  /* `ScalaEquals.genString` must be called from within a `toString` method. */
  val badGenStringCallSite =
    "ScalaEquals.genString must be called from within a toString method. " +
      "Check that your method signature matches \"override def toString: String\""

  /* Lazy `hashCode` must use only `val`s otherwise `hashCode` could change and `equals` would be incorrect */
  val badLazyHashVals: String =
    "Lazy hashCode must use only vals otherwise hashCode could change, and equals would be defined incorrectly."

  /* `ScalaEquals.productElement` must be called from within a `productElement` method */
  val badProductElementCallSite =
    "ScalaEquals.productElement must be called from within a productElement method. " +
      "Check that your method signature matches \"def productElement(n: Int): Any\""

  /* `ScalaEquals.productArity` must be called from within a `productArity` method */
  val badProductArityCallSite =
    "ScalaEquals.productArity must be called from within a productArity method. " +
      "Check that your method signature matches \"def productArity: Int\""

  /* `ScalaEquals.productPrefix` must be called from within a `productPrefix` method */
  val badProductPrefixCallSite =
    "ScalaEquals.productPrefix must be called from within a productPrefix method. " +
      "Check that your method signature matches \"override def productPrefix: String\""

  object warnings {
    /* `ScalaEquals.equal` will only expand to a `super.equals(that)` call for traits. */
    val equalWithTrait =
      "ScalaEquals.equal on traits will only expand to a super.equals(that) call."

    /* `ScalaEquals.genString` will only expand to `"ClassName()"` for traits */
    val genStringWithTrait =
      "ScalaEquals.genString on traits will only expand to \"ClassName()\""

    /*
     * This equals method is not guaranteed to be consistent with the equals contract if this class is subclassed.
     * One of the following must be true: `canEqual` is defined correctly, this class is `final`, `equals` is `final`
     * If this class has a superclass that redefines `equals`, only by defining `canEqual` will this method be safe
     */
    val notSafeToSubclass =
      "This class's equals method is not necessarily safe when subclassed, consider making this class final, " +
        "the equals method final, or creating a canEqual method. If this class has a super class that has redefined " +
        "equals, it is definitely unsafe, and can only be fixed by defining canEqual"
  }
}
