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
import org.scalacheck.Arbitrary._

case class DummyArg(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int, g: Int, x: Int, y: Int, t: Int, _h: Int, _q: Int) {
  def classArgString: String = s"($a, $b, $c, $d, $e, $f, $g, $t, ${_h}, ${_q})"
  def subArgString: String = s"($a, $b, $c, $d, $e, $f, $g, $x, $y, $t, ${_h}, ${_q})"
}

object DummyArg {
  def gen: Gen[DummyArg] = for {
    a <- arbitrary[Int]
    b <- arbitrary[Int]
    c <- arbitrary[Int]
    d <- arbitrary[Int]
    e <- arbitrary[Int]
    f <- arbitrary[Int]
    g <- arbitrary[Int]
    x <- arbitrary[Int]
    y <- arbitrary[Int]
    t <- arbitrary[Int]
    _h <- arbitrary[Int]
    _q <- arbitrary[Int]
  } yield DummyArg(a, b, c, d, e, f, g, x, y, t, _h, _q)
}

trait DummyFixture[A] extends EqualsFixture[A, DummyArg] {
  def gen: Gen[DummyArg] = DummyArg.gen
}

trait DummySubbedFixture[A, B <: A] extends DummyFixture[A] with SubClassedEqualsFixture[A, DummyArg, B]

trait DummySubFixture[A] {
  def create(arg: DummyArg): A
}

// Equals on b, c, d, h, i, j, q, r, and s
// On constructor: b, c, d, e, _h, _q
// Not on constructor: a, f, g, t
class DummyTest extends DummySubbedFixture[Dummy, DummySub] {
  def name: String = "Dummy"

  def sub: Boolean = false

  def create(arg: DummyArg): Dummy = new Dummy(arg.a, arg.b, arg.c, arg.d, arg.e, arg.f, arg.g, arg.t, arg._h, arg._q)

  /* Creates a String to test toString = A(arg) */
  def createToString(arg: DummyArg): String =
    s"Dummy(${arg.b}, ${arg.c}, ${arg.d}, ${arg.e}, ${arg._h}, ${arg._q})"

  def changeDiff(arg: DummyArg, arg2: DummyArg): DummyArg = arg.copy(a = arg2.a, f = arg2.f, g = arg2.g, t = arg2.t)

  def changeRandom(arg: DummyArg, arg2: DummyArg): DummyArg = {
    val swapped = swap(IndexedSeq(arg.b, arg.c, arg.d, arg.e, arg._h, arg._q),
      IndexedSeq(arg2.b, arg2.c, arg2.d, arg2.e, arg2._h, arg2._q))
    arg.copy(b = swapped(0), c = swapped(1), d = swapped(2), e = swapped(3), _h = swapped(4), _q = swapped(5))
  }

  /* true if arg and arg2 differ in a field not checked by equality*/
  def diff(arg: DummyArg, arg2: DummyArg): Boolean =
    arg.a != arg2.a || arg.f != arg2.f || arg.g != arg2.g || arg.t != arg2.t

  /* true if arg and arg2 differ in a field checked by equality */
  def unequal(arg: DummyArg, arg2: DummyArg): Boolean =
    arg.b != arg2.b || arg.c != arg2.c || arg.d != arg2.d || arg.e != arg2.e || arg._h != arg2._h || arg._q != arg2._q

  def subClassName: String = DummySubTest.dummySubName

  /* Creates a C, a subclass of A, from B */
  def createSubClass(arg: DummyArg): DummySub = DummySubTest.create(arg)
}

// DummySub - equals on super, a, x (all = DummySub.a, b, c, d, x, h, i, j, q, r, s)
// On constructor: a, x (all = a, b, c, d, e, x, _h, _q)
// Not on constructor: f, g, y, t
object DummySubTest extends DummySubFixture[DummySub] {
  def dummySubName: String = "DummySub"

  def create(arg: DummyArg): DummySub =
    new DummySub(arg.a, arg.b, arg.c, arg.d, arg.e, arg.f, arg.g, arg.x, arg.y, arg.t, arg._h, arg._q)
}

class DummySubTest extends DummyFixture[DummySub] {
  def name: String = DummySubTest.dummySubName

  def sub: Boolean = true

  def create(arg: DummyArg): DummySub = DummySubTest.create(arg)

  /* Creates a String to test toString = A(arg) */
  def createToString(arg: DummyArg): String =
    s"DummySub(${arg.a}, ${arg.b}, ${arg.c}, ${arg.d}, ${arg.e}, ${arg.x}, ${arg._h}, ${arg._q})"

  def changeDiff(arg: DummyArg, arg2: DummyArg): DummyArg =
    arg.copy(f = arg2.f, g = arg2.g, y = arg2.y, t = arg2.t)

  def changeRandom(arg: DummyArg, arg2: DummyArg): DummyArg = {
    val swapped = swap(IndexedSeq(arg.a, arg.b, arg.c, arg.d, arg.e, arg.x, arg._h, arg._q),
      IndexedSeq(arg2.a, arg2.b, arg2.c, arg2.d, arg2.e, arg2.x, arg2._h, arg2._q))
    arg.copy(a = swapped(0), b = swapped(1), c = swapped(2), d = swapped(3), e = swapped(4),
      x = swapped(5), _h = swapped(6), _q = swapped(7))
  }

  /* true if arg and arg2 differ in a field not checked by equality*/
  def diff(arg: DummyArg, arg2: DummyArg): Boolean =
    arg.f != arg2.f || arg.g != arg2.g || arg.y != arg2.y || arg.t != arg2.t

  /* true if arg and arg2 differ in a field checked by equality */
  def unequal(arg: DummyArg, arg2: DummyArg): Boolean =
    arg.a != arg2.a || arg.b != arg2.b || arg.c != arg2.c || arg.d != arg2.d || arg.e != arg2.e ||
      arg.x != arg2.x || arg._h != arg2._h || arg._q != arg2._q
}

// Equals on b, c, d
// On constructor: b, c, d
// Not on constructor: a, e, f, g, t, _h, _q
class DummyCTest extends DummySubbedFixture[DummyC, DummyCSub] {
  def name: String = "DummyC"

  def sub: Boolean = false

  def create(arg: DummyArg): DummyC =
    new DummyC(arg.a, arg.b, arg.c, arg.d, arg.e, arg.f, arg.g, arg.t, arg._h, arg._q)

  /* Creates a String to test toString = A(arg) */
  def createToString(arg: DummyArg): String =
    s"DummyC(${arg.b}, ${arg.c}, ${arg.d})"

  def changeDiff(arg: DummyArg, arg2: DummyArg): DummyArg =
    arg.copy(a = arg2.a, e = arg2.e, f = arg2.f, g = arg2.g, t = arg2.t, _h = arg2._h, _q = arg2._q)

  def changeRandom(arg: DummyArg, arg2: DummyArg): DummyArg = {
    val swapped = swap(IndexedSeq(arg.b, arg.c, arg.d), IndexedSeq(arg2.b, arg2.c, arg2.d))
    arg.copy(b = swapped(0), c = swapped(1), d = swapped(2))
  }

  /* true if arg and arg2 differ in a field not checked by equality*/
  def diff(arg: DummyArg, arg2: DummyArg): Boolean =
    arg.a != arg2.a || arg.e != arg2.e || arg.f != arg2.f || arg.g != arg2.g || arg.t != arg2.t ||
      arg._h != arg2._h || arg._q != arg2._q

  /* true if arg and arg2 differ in a field checked by equality */
  def unequal(arg: DummyArg, arg2: DummyArg): Boolean =
    arg.b != arg2.b || arg.c != arg2.c || arg.d != arg2.d

  def subClassName: String = DummyCSubTest.dummySubName

  /* Creates a C, a subclass of A, from B */
  def createSubClass(arg: DummyArg): DummyCSub = DummyCSubTest.create(arg)
}

// Equals on super, a, x (all = DummySub.a, b, c, d, x)
// On constructor: a, x (all = a, b, c, d, x)
// Not on constructor: e, f, g, y, t, _h, _q
object DummyCSubTest extends DummySubFixture[DummyCSub] {
  def dummySubName: String = "DummyCSub"

  def create(arg: DummyArg): DummyCSub =
    new DummyCSub(arg.a, arg.b, arg.c, arg.d, arg.e, arg.f, arg.g, arg.x, arg.y, arg.t, arg._h, arg._q)
}

class DummyCSubTest extends DummyFixture[DummyCSub] {
  def name: String = DummyCSubTest.dummySubName

  def sub: Boolean = true

  def create(arg: DummyArg): DummyCSub = DummyCSubTest.create(arg)

  /* Creates a String to test toString = A(arg) */
  def createToString(arg: DummyArg): String =
    s"DummyCSub(${arg.a}, ${arg.b}, ${arg.c}, ${arg.d}, ${arg.x})"

  def changeDiff(arg: DummyArg, arg2: DummyArg): DummyArg =
    arg.copy(e = arg2.e, f = arg2.f, g = arg2.g, y = arg2.y, t = arg2.t, _h = arg2._h, _q = arg2._q)

  def changeRandom(arg: DummyArg, arg2: DummyArg): DummyArg = {
    val swapped = swap(IndexedSeq(arg.a, arg.b, arg.c, arg.d, arg.x),
      IndexedSeq(arg2.a, arg2.b, arg2.c, arg2.d, arg2.x))
    arg.copy(a = swapped(0), b = swapped(1), c = swapped(2), d = swapped(3), x = swapped(4))
  }

  /* true if arg and arg2 differ in a field not checked by equality*/
  def diff(arg: DummyArg, arg2: DummyArg): Boolean =
    arg.e != arg2.e || arg.f != arg2.f || arg.g != arg2.g || arg.y != arg2.y || arg.t != arg2.t ||
      arg._h != arg2._h || arg._q != arg2._q

  /* true if arg and arg2 differ in a field checked by equality */
  def unequal(arg: DummyArg, arg2: DummyArg): Boolean =
    arg.a != arg2.a || arg.b != arg2.b || arg.c != arg2.c || arg.d != arg2.d || arg.x != arg2.x
}

// Equals on       b, c, e, f, k, n, o , p, q , r, s
// On constructor: b, c, e, f, a, g, _h, b, _q, e, e (Set(a, b, c, e, f, g, _h, _q))
// Not on constructor: d, t
class DummyParamsTest extends DummySubbedFixture[DummyParams, DummyParamsSub] {
  def name: String = "DummyParams"

  def sub: Boolean = false

  def create(arg: DummyArg): DummyParams =
    new DummyParams(arg.a, arg.b, arg.c, arg.d, arg.e, arg.f, arg.g, arg.t, arg._h, arg._q)

  /* Creates a String to test toString = A(arg) */
  def createToString(arg: DummyArg): String =
    s"DummyParams(${arg.a}, ${arg.b}, ${arg.c}, ${arg.e}, ${arg.f}, ${arg.g}, ${arg._h}, ${arg._q})"

  def changeDiff(arg: DummyArg, arg2: DummyArg): DummyArg =
    arg.copy(d = arg2.d, t = arg2.t)

  def changeRandom(arg: DummyArg, arg2: DummyArg): DummyArg = {
    val swapped = swap(IndexedSeq(arg.a, arg.b, arg.c, arg.e, arg.f, arg.g, arg._h, arg._q),
      IndexedSeq(arg2.a, arg2.b, arg2.c, arg2.e, arg2.f, arg2.g, arg2._h, arg2._q))
    arg.copy(a = swapped(0), b = swapped(1), c = swapped(2), e = swapped(3), f = swapped(4), g = swapped(5),
      _h = swapped(6), _q = swapped(7))
  }

  /* true if arg and arg2 differ in a field not checked by equality*/
  def diff(arg: DummyArg, arg2: DummyArg): Boolean =
    arg.d != arg2.d || arg.t != arg2.t

  /* true if arg and arg2 differ in a field checked by equality */
  def unequal(arg: DummyArg, arg2: DummyArg): Boolean =
    arg.a != arg2.a || arg.b != arg2.b || arg.c != arg2.c || arg.e != arg2.e || arg.f != arg2.f || arg.g != arg2.g ||
      arg._h != arg2._h || arg._q != arg2._q

  def subClassName: String = DummyParamsSubTest.dummySubName

  /* Creates a C, a subclass of A, from B */
  def createSubClass(arg: DummyArg): DummyParamsSub = DummyParamsSubTest.create(arg)
}

// Equals on super, super, a, b, h , y, o
// On constructor:       , a, b, _q, y, a (Set(a, b, _q, y), all = a, b, c, e, f, g, y, _h, _q)
// Not on constructor:  d, x, t
object DummyParamsSubTest extends DummySubFixture[DummyParamsSub] {
  def dummySubName: String = "DummyParamsSub"

  def create(arg: DummyArg): DummyParamsSub =
    new DummyParamsSub(arg.a, arg.b, arg.c, arg.d, arg.e, arg.f, arg.g, arg.x, arg.y, arg.t, arg._h, arg._q)
}

class DummyParamsSubTest extends DummyFixture[DummyParamsSub] {
  def name: String = DummyParamsSubTest.dummySubName

  def sub: Boolean = true

  def create(arg: DummyArg): DummyParamsSub = DummyParamsSubTest.create(arg)

  /* Creates a String to test toString = A(arg) */
  def createToString(arg: DummyArg): String =
    s"DummyParamsSub(${arg.a}, ${arg.b}, ${arg.c}, ${arg.e}, ${arg.f}, ${arg.g}, ${arg.y}, ${arg._h}, ${arg._q})"

  def changeDiff(arg: DummyArg, arg2: DummyArg): DummyArg =
    arg.copy(d = arg2.d, x = arg2.x, t = arg.t)

  def changeRandom(arg: DummyArg, arg2: DummyArg): DummyArg = {
    val swapped = swap(IndexedSeq(arg.a, arg.b, arg.c, arg.e, arg.f, arg.g, arg.y, arg._h, arg._q),
      IndexedSeq(arg2.a, arg2.b, arg2.c, arg2.e, arg2.f, arg2.g, arg2.y, arg2._h, arg2._q))
    arg.copy(a = swapped(0), b = swapped(1), c = swapped(2), e = swapped(3), f = swapped(4), g = swapped(5),
      y = swapped(6), _h = swapped(7), _q = swapped(8))
  }

  /* true if arg and arg2 differ in a field not checked by equality*/
  def diff(arg: DummyArg, arg2: DummyArg): Boolean =
     arg.t != arg2.t || arg.d != arg2.d || arg.x != arg2.x

  /* true if arg and arg2 differ in a field checked by equality */
  def unequal(arg: DummyArg, arg2: DummyArg): Boolean =
    arg.a != arg2.a || arg.b != arg2.b || arg.c != arg2.c  || arg.e != arg2.e || arg.f != arg2.f ||
      arg.g != arg2.g || arg.y != arg2.y  || arg._h != arg2._h || arg._q != arg2._q
}
