package com.scalaequals.test

import org.scalacheck.Gen
import org.scalacheck.Arbitrary._
import scala.Some

object DummyShared {
  def dummySubName: String = "DummySub"
  def dummySubGen: Option[Gen[DummySub]] = Some(for {
    a <- arbitrary[Int]
    b <- arbitrary[Int]
    c <- arbitrary[Int]
    d <- arbitrary[Int]
    e <- arbitrary[Int]
    f <- arbitrary[Int]
    g <- arbitrary[Int]
    x <- arbitrary[Int]
    y <- arbitrary[Int]
  } yield new DummySub(a, b, c, d, e, f, g, x, y))
}

class DummyTest extends EqualsFixture[Dummy] {
  def name: String = "Dummy"
  def classGen: Gen[Dummy] = for {
    a <- arbitrary[Int]
    b <- arbitrary[Int]
    c <- arbitrary[Int]
    d <- arbitrary[Int]
    e <- arbitrary[Int]
    f <- arbitrary[Int]
    g <- arbitrary[Int]
  } yield new Dummy(a, b, c, d, e, f, g)

  override def subClassName: String = DummyShared.dummySubName
  override def subClassGen: Option[Gen[DummySub]] = DummyShared.dummySubGen
}

class DummySubTest extends EqualsFixture[DummySub] {
  def name: String = DummyShared.dummySubName
  def classGen: Gen[DummySub] = DummyShared.dummySubGen.get
}

object DummyCShared {
  def dummySubName: String = "DummyCSub"
  def dummySubGen: Option[Gen[DummyCSub]] = Some(for {
    a <- arbitrary[Int]
    b <- arbitrary[Int]
    c <- arbitrary[Int]
    d <- arbitrary[Int]
    e <- arbitrary[Int]
    f <- arbitrary[Int]
    g <- arbitrary[Int]
    x <- arbitrary[Int]
    y <- arbitrary[Int]
  } yield new DummyCSub(a, b, c, d, e, f, g, x, y))
}

class DummyCTest extends EqualsFixture[DummyC] {
  def name: String = "DummyC"
  def classGen: Gen[DummyC] = for {
    a <- arbitrary[Int]
    b <- arbitrary[Int]
    c <- arbitrary[Int]
    d <- arbitrary[Int]
    e <- arbitrary[Int]
    f <- arbitrary[Int]
    g <- arbitrary[Int]
  } yield new DummyC(a, b, c, d, e, f, g)

  override def subClassName: String = DummyCShared.dummySubName
  override def subClassGen: Option[Gen[DummyCSub]] = DummyCShared.dummySubGen
}

class DummyCSubTest extends EqualsFixture[DummyCSub] {
  def name: String = DummyCShared.dummySubName
  def classGen: Gen[DummyCSub] = DummyCShared.dummySubGen.get
}

object DummyParamsShared {
  def dummySubName: String = "DummyParamsSub"
  def dummySubGen: Option[Gen[DummyParamsSub]] = Some(for {
    a <- arbitrary[Int]
    b <- arbitrary[Int]
    c <- arbitrary[Int]
    d <- arbitrary[Int]
    e <- arbitrary[Int]
    f <- arbitrary[Int]
    g <- arbitrary[Int]
    x <- arbitrary[Int]
    y <- arbitrary[Int]
  } yield new DummyParamsSub(a, b, c, d, e, f, g, x, y))
}

class DummyParamsTest extends EqualsFixture[DummyParams] {
  def name: String = "DummyParams"
  def classGen: Gen[DummyParams] = for {
    a <- arbitrary[Int]
    b <- arbitrary[Int]
    c <- arbitrary[Int]
    d <- arbitrary[Int]
    e <- arbitrary[Int]
    f <- arbitrary[Int]
    g <- arbitrary[Int]
  } yield new DummyParams(a, b, c, d, e, f, g)

  override def subClassName: String = DummyParamsShared.dummySubName
  override def subClassGen: Option[Gen[DummyParamsSub]] = DummyParamsShared.dummySubGen
}

class DummyParamsSubTest extends EqualsFixture[DummyParamsSub] {
  def name: String = DummyParamsShared.dummySubName
  def classGen: Gen[DummyParamsSub] = DummyParamsShared.dummySubGen.get
}
