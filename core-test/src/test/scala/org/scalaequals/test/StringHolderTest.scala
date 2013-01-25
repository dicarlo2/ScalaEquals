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

package org.scalaequals.test

import org.scalacheck.Gen

class StringHolderTest extends EqualsFixture[StringHolder, String] {
  def name: String = "StringHolder"

  def gen: Gen[String] = Gen.alphaStr

  /* Creates a T from B */
  def create(arg: String): StringHolder = new StringHolder(arg)

  /* Creates a String to test toString = A(arg) */
  def createToString(arg: String): String = s"StringHolder($arg)"

  /* Swaps all constructor arguments that are not part of equals from arg to arg2's values */
  def changeDiff(arg: String, arg2: String): String = arg

  /* Changes one random argument that is part of equals to arg2's value */
  def changeRandom(arg: String, arg2: String): String = arg2

  /* true if arg and arg2 differ in a field not checked by equality or there are no fields that can differ */
  def diff(arg: String, arg2: String): Boolean = true

  /* true if arg and arg2 differ in a field checked by equality */
  def unequal(arg: String, arg2: String): Boolean = arg != arg2
}
