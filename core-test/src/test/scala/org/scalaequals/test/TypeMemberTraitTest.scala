package org.scalaequals.test

import org.scalacheck.Gen

case class TypeMemberArg(a: String, b: String, d: String, e: String, g: String, h: String)

// Equals on a, b, c (construct with a, b, g)
class TypeMemberTraitTest extends EqualsFixture[TypeMemberTrait {type A = String}, TypeMemberArg] {
  def name: String = "TypeMemberTrait"

  def gen: Gen[TypeMemberArg] = for {
    a <- Gen.alphaStr
    b <- Gen.alphaStr
    d <- Gen.alphaStr
    e <- Gen.alphaStr
    g <- Gen.alphaStr
    h <- Gen.alphaStr
  } yield TypeMemberArg(a, b, d, e, g, h)

  /* Creates a T from B */
  def create(arg: TypeMemberArg): TypeMemberTrait {type A = String} = new TypeMemberTrait {
    type A = String
    val a: String = arg.a
    protected val b: String = arg.b
    var d: String = arg.d
    protected var e: String = arg.e
    def g: String = arg.g
    protected def h: String = arg.h
  }

  /* Creates a String to test toString = A(arg) */
  def createToString(arg: TypeMemberArg): String = s"TypeMemberTrait(${arg.a}, ${arg.b}, ${arg.g})"

  /* Swaps all constructor arguments that are not part of equals from arg to arg2's values */
  def changeDiff(arg: TypeMemberArg, arg2: TypeMemberArg): TypeMemberArg =
    arg.copy(d = arg2.d, e = arg2.e, h = arg2.h)

  /* Changes one random argument that is part of equals to arg2's value */
  def changeRandom(arg: TypeMemberArg, arg2: TypeMemberArg): TypeMemberArg = {
    val swapped = swap(IndexedSeq(arg.a, arg.b, arg.g), IndexedSeq(arg2.a, arg2.b, arg2.g))
    arg.copy(a = swapped(0), b = swapped(1), g = swapped(2))
  }

  /* true if arg and arg2 differ in a field not checked by equality or there are no fields that can differ */
  def diff(arg: TypeMemberArg, arg2: TypeMemberArg): Boolean =
    arg.d != arg2.d || arg.e != arg2.e || arg.h != arg2.h

  /* true if arg and arg2 differ in a field checked by equality */
  def unequal(arg: TypeMemberArg, arg2: TypeMemberArg): Boolean =
    arg.a != arg2.a || arg.b != arg2.b || arg.g != arg2.g
}
