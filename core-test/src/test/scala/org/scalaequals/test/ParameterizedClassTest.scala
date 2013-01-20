package org.scalaequals.test

import org.scalacheck.{Arbitrary, Gen}

class ParameterizedClassTest extends EqualsFixture[ParameterizedClass[String, Int]] {
  def name: String = "ParameterizedClass[String]"
  def create(string: String, num: Int): ParameterizedClass[String, Int] = new ParameterizedClass[String, Int](string, num)
  def classGen: Gen[ParameterizedClass[String, Int]] = for {
    string <- Gen.alphaStr
    num <- Arbitrary.arbitrary[Int]
  } yield create(string, num)
  def equal2ClassGen: Gen[(ParameterizedClass[String, Int], ParameterizedClass[String, Int])] = for {
    string <- Gen.alphaStr
    num <- Arbitrary.arbitrary[Int]
  } yield (create(string, num), create(string, num))
  def equal3ClassGen: Gen[(ParameterizedClass[String, Int], ParameterizedClass[String, Int], ParameterizedClass[String, Int])] = for {
    string <- Gen.alphaStr
    num <- Arbitrary.arbitrary[Int]
  } yield (create(string, num), create(string, num), create(string, num))
}
