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

/** @author Alex DiCarlo
  * @version 2.0.0
  * @since 1.0.1
  */
package object impl {
  /** @author Alex DiCarlo
    * @version 2.0.0
    * @since 0.3.0
    */
  private[impl] object Errors {
    /**
     * `ScalaEquals.canEquals` must be called from within a `canEqual` method
     */
    val badCanEqualsCallSite: String =
      "ScalaEquals.canEquals must be called from within a canEqual method. " +
        "Check that your method signature matches one of \"def canEqual(other: Any): Boolean\" or " +
        "\"override def canEqual(other: Any): Boolean\"."
    /**
     * `ScalaEquals.equal` must be called from within an `equals` method
     */
    val badEqualCallSite: String =
      "ScalaEquals.equal must be called from within an equals method. " +
        "Check that your method signature matches \"override def equals(other: Any): Boolean\""
    /**
     * `ScalaEquals.hash` must be called from within a `hashCode` method
     */
    val badHashCallSite: String =
      "ScalaEquals.hash must be called from within a hashCode method. " +
        "Check that your method signature matches \"override def hashCode(): Int\""
    /**
     * `ScalaEquals.hash` must be called in a class that also uses `ScalaEquals.equal` or `ScalaEquals.equalAllVals`
     */
    val missingEqual: String = "No attachments found on equals. Ensure that you are using one of " +
      "ScalaEquals.equal or ScalaEquals.equalAllVals in your #equals method"
    /**
     * `ScalaEquals.hash` must be called in a class that defines equals
     */
    val missingEquals: String =
      "No equals method found! Ensure you are overriding equals and that it has the correct signature."
    /**
     *  ScalaEquals.toString must be called from within a toString method. Check that your method signature matches
     *  override def toString: String"
     */
    val badToStringCallSite: String =
      "ScalaEquals.toString must be called from within a toString method. " +
        "Check that your method signature matches \"override def toString: String\""

    /**
     *  Lazy hashCode must use only vals otherwise hashCode could change, and equals would be defined incorrectly
     */
    val badLazyHashVals: String =
      "Lazy hashCode must use only vals otherwise hashCode could change, and equals would be defined incorrectly."
  }
  /** @author Alex DiCarlo
    * @version 2.0.0
    * @since 1.0.3
    */
  private[impl] object Warnings {
    /**
     * `ScalaEquals.equal` will only expand to a `super.equals(that)` call for traits.
     */
    val equalWithTrait: String =
      "ScalaEquals.equal on traits will only expand to a super.equals(that) call."

    /**
     * `ScalaEquals.genString` will only expand to `"ClassName()"` for traits
     */
    val genStringWithTrait: String =
      "ScalaEquals.genString on traits will only expand to \"ClassName()\""

    /**
     * This equals method is not guaranteed to be consistent with the equals contract if this class is subclassed.
     * One of the following must be true: `canEqual` is defined correctly, this class is `final`, `equals` is `final`
     * If this class has a superclass that redefines `equals`, only by defining `canEqual` will this method be safe
     */
    val notSafeToSubclass: String =
      "This class's equals method is not necessarily safe when subclassed, consider making this class final, " +
        "the equals method final, or creating a canEqual method. If this class has a super class that has redefined " +
        "equals, it is definitely unsafe, and can only be fixed by defining canEqual"
  }
}
