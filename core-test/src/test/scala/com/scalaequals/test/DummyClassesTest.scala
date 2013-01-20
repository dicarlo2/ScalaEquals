package com.scalaequals.test

import org.scalacheck.Gen
import org.scalacheck.Arbitrary._
import scala.Some

object DummyShared {
  type Arg = (Int, Int, Int, Int, Int, Int, Int, Int, Int)
  def dummyArgGen: Gen[Arg] = for {
    a <- arbitrary[Int]
    b <- arbitrary[Int]
    c <- arbitrary[Int]
    d <- arbitrary[Int]
    e <- arbitrary[Int]
    f <- arbitrary[Int]
    g <- arbitrary[Int]
    x <- arbitrary[Int]
    y <- arbitrary[Int]
  } yield (a, b, c, d, e, f, g, x, y)
  def dummySubName: String = "DummySub"
  def createDummySub(arg: Arg): DummySub = arg match {
    case (a, b, c, d, e, f, g, x, y) => new DummySub(a, b, c, d, e, f, g, x, y)
  }
  def dummySubGen: Option[Gen[DummySub]] = Some(for {
    arg <- dummyArgGen
  } yield createDummySub(arg))
}

class DummyTest extends EqualsFixture[Dummy] {
  def name: String = "Dummy"
  def createDummy(arg: DummyShared.Arg): Dummy = arg match {
    case (a, b, c, d, e, f, g, x, y) => new Dummy(a, b, c, d, e, f, g)
  }
  def classGen: Gen[Dummy] = for {
    arg <- DummyShared.dummyArgGen
  } yield createDummy(arg)
  def equal2ClassGen: Gen[(Dummy, Dummy)] = for {
    arg <- DummyShared.dummyArgGen
  } yield (createDummy(arg), createDummy(arg))
  def equal3ClassGen: Gen[(Dummy, Dummy, Dummy)] = for {
    arg <- DummyShared.dummyArgGen
  } yield (createDummy(arg), createDummy(arg), createDummy(arg))
  override def subClassName: String = DummyShared.dummySubName
  override def subClassGen: Option[Gen[DummySub]] = DummyShared.dummySubGen
}

class DummySubTest extends EqualsFixture[DummySub] {
  def name: String = DummyShared.dummySubName
  def classGen: Gen[DummySub] = DummyShared.dummySubGen.get
  def equal2ClassGen: Gen[(DummySub, DummySub)] = for {
    arg <- DummyShared.dummyArgGen
  } yield (DummyShared.createDummySub(arg), DummyShared.createDummySub(arg))
  def equal3ClassGen: Gen[(DummySub, DummySub, DummySub)] = for {
    arg <- DummyShared.dummyArgGen
  } yield (DummyShared.createDummySub(arg), DummyShared.createDummySub(arg), DummyShared.createDummySub(arg))
}

object DummyCShared {
  def dummySubName: String = "DummyCSub"
  def createDummyCSub(arg: DummyShared.Arg): DummyCSub = arg match {
    case (a, b, c, d, e, f, g, x, y) => new DummyCSub(a, b, c, d, e, f, g, x, y)
  }
  def dummySubGen: Option[Gen[DummyCSub]] = Some(for {
    arg <- DummyShared.dummyArgGen
  } yield createDummyCSub(arg))
}

class DummyCTest extends EqualsFixture[DummyC] {
  def name: String = "DummyC"
  def createDummyC(arg: DummyShared.Arg): DummyC = arg match {
    case (a, b, c, d, e, f, g, x, y) => new DummyC(a, b, c, d, e, f, g)
  }
  def classGen: Gen[DummyC] = for {
    arg <- DummyShared.dummyArgGen
  } yield createDummyC(arg)
  def equal2ClassGen: Gen[(DummyC, DummyC)] = for {
    arg <- DummyShared.dummyArgGen
  } yield (createDummyC(arg), createDummyC(arg))
  def equal3ClassGen: Gen[(DummyC, DummyC, DummyC)] = for {
    arg <- DummyShared.dummyArgGen
  } yield (createDummyC(arg), createDummyC(arg), createDummyC(arg))

  override def subClassName: String = DummyCShared.dummySubName
  override def subClassGen: Option[Gen[DummyCSub]] = DummyCShared.dummySubGen
}

class DummyCSubTest extends EqualsFixture[DummyCSub] {
  def name: String = DummyCShared.dummySubName
  def classGen: Gen[DummyCSub] = DummyCShared.dummySubGen.get
  def equal2ClassGen: Gen[(DummyCSub, DummyCSub)] = for {
    arg <- DummyShared.dummyArgGen
  } yield (DummyCShared.createDummyCSub(arg), DummyCShared.createDummyCSub(arg))
  def equal3ClassGen: Gen[(DummyCSub, DummyCSub, DummyCSub)] = for {
    arg <- DummyShared.dummyArgGen
  } yield (DummyCShared.createDummyCSub(arg), DummyCShared.createDummyCSub(arg), DummyCShared.createDummyCSub(arg))
}

object DummyParamsShared {
  def dummySubName: String = "DummyParamsSub"
  def createDummyParamsSub(arg: DummyShared.Arg): DummyParamsSub = arg match {
    case (a, b, c, d, e, f, g, x, y) => new DummyParamsSub(a, b, c, d, e, f, g, x, y)
  }
  def dummySubGen: Option[Gen[DummyParamsSub]] = Some(for {
    arg <- DummyShared.dummyArgGen
  } yield createDummyParamsSub(arg))
}

class DummyParamsTest extends EqualsFixture[DummyParams] {
  def name: String = "DummyParams"
  def createDummyParams(arg: DummyShared.Arg): DummyParams = arg match {
    case (a, b, c, d, e, f, g, x, y) => new DummyParams(a, b, c, d, e, f, g)
  }
  def classGen: Gen[DummyParams] = for {
    arg <- DummyShared.dummyArgGen
  } yield createDummyParams(arg)
  def equal2ClassGen: Gen[(DummyParams, DummyParams)] = for {
    arg <- DummyShared.dummyArgGen
  } yield (createDummyParams(arg), createDummyParams(arg))
  def equal3ClassGen: Gen[(DummyParams, DummyParams, DummyParams)] = for {
    arg <- DummyShared.dummyArgGen
  } yield (createDummyParams(arg), createDummyParams(arg), createDummyParams(arg))
  override def subClassName: String = DummyParamsShared.dummySubName
  override def subClassGen: Option[Gen[DummyParamsSub]] = DummyParamsShared.dummySubGen
}

class DummyParamsSubTest extends EqualsFixture[DummyParamsSub] {
  def name: String = DummyParamsShared.dummySubName
  def classGen: Gen[DummyParamsSub] = DummyParamsShared.dummySubGen.get
  def equal2ClassGen: Gen[(DummyParamsSub, DummyParamsSub)] = for {
    arg <- DummyShared.dummyArgGen
  } yield (DummyParamsShared.createDummyParamsSub(arg), DummyParamsShared.createDummyParamsSub(arg))
  def equal3ClassGen: Gen[(DummyParamsSub, DummyParamsSub, DummyParamsSub)] = for {
    arg <- DummyShared.dummyArgGen
  } yield (DummyParamsShared.createDummyParamsSub(arg), DummyParamsShared.createDummyParamsSub(arg), DummyParamsShared.createDummyParamsSub(arg))

}
