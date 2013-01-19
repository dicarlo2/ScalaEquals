package com.scalaequals.test

import org.scalacheck.Gen

class StringHolderTest extends EqualsFixture[StringHolder] {
  def name = "StringHolder"
  def classGen = for {
    string <- Gen.alphaStr
  } yield new StringHolder(name)
}
