package org.scalaequals.test

import org.scalacheck.Gen

class TypeMemberTraitTest extends EqualsFixture[TypeMemberTrait {type A = String}] {
  def name: String = "TypeMemberTrait"
  def create(string: String): TypeMemberTrait {type A = String} = new TypeMemberTrait {
    type A = String
    val a: String = string
    protected val b: String = string
    var d: String = string
    protected var e: String = string
    def g: String = string
    protected def h: String = string
  }
  def classGen: Gen[TypeMemberTrait {type A = String}] = for {
    string <- Gen.alphaStr
  } yield create(string)
  def equal2ClassGen: Gen[(TypeMemberTrait {type A = String}, TypeMemberTrait {type A = String})] = for {
    string <- Gen.alphaStr
  } yield (create(string), create(string))
  def equal3ClassGen: Gen[(TypeMemberTrait {type A = String}, TypeMemberTrait {type A = String}, TypeMemberTrait {type A = String})] = for {
    string <- Gen.alphaStr
  } yield (create(string), create(string), create(string))
}
