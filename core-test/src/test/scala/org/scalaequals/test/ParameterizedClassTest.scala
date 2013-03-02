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

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen

case class ParameterizedClassArg(string: String, num: Int)

class ParameterizedClassTest extends EqualsFixture[ParameterizedClass[String, Int, AnyRef], ParameterizedClassArg] {
  def name: String = "ParameterizedClass[String]"

  def gen: Gen[ParameterizedClassArg] = for {
    string <- Gen.alphaStr
    num <- arbitrary[Int]
  } yield ParameterizedClassArg(string, num)

  /* Creates a T from B */
  def create(arg: ParameterizedClassArg): ParameterizedClass[String, Int, AnyRef] =
    new ParameterizedClass[String, Int, AnyRef](arg.string, arg.num)

  /* Creates a String to test toString = A(arg) */
  def createToString(arg: ParameterizedClassArg): String = s"ParameterizedClass(${arg.string}, ${arg.num})"

  /* Swaps all constructor arguments that are not part of equals from arg to arg2's values */
  def changeDiff(arg: ParameterizedClassArg, arg2: ParameterizedClassArg): ParameterizedClassArg = arg

  /* Changes one random argument that is part of equals to arg2's value */
  def changeRandom(arg: ParameterizedClassArg, arg2: ParameterizedClassArg): ParameterizedClassArg = {
    val swapped = swap(IndexedSeq(arg.string, arg.num), IndexedSeq(arg2.string, arg2.num))
    arg.copy(string = swapped(0).asInstanceOf[String], num = swapped(1).asInstanceOf[Int])
  }

  /* true if arg and arg2 differ in a field not checked by equality or there are no fields that can differ */
  def diff(arg: ParameterizedClassArg, arg2: ParameterizedClassArg): Boolean = true

  /* true if arg and arg2 differ in a field checked by equality */
  def unequal(arg: ParameterizedClassArg, arg2: ParameterizedClassArg): Boolean =
    arg.string != arg2.string || arg.num != arg2.num
}
