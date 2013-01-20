package com.scalaequals.test

import org.scalacheck.Gen

class StringHolderTest extends EqualsFixture[StringHolder] {
  def name: String = "StringHolder"
  def classGen: Gen[StringHolder] = for {
    string <- Gen.alphaStr
  } yield new StringHolder(name)
  def equal2ClassGen: Gen[(StringHolder, StringHolder)] = for {
    string <- Gen.alphaStr
  } yield (new StringHolder(name), new StringHolder(name))
  def equal3ClassGen: Gen[(StringHolder, StringHolder, StringHolder)] = for {
    string <- Gen.alphaStr
  } yield (new StringHolder(name), new StringHolder(name), new StringHolder(name))
}
