package com.scalaequals.test

import org.scalatest.{GivenWhenThen, FeatureSpec}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalacheck.Gen

trait EqualsFixture[T] extends FeatureSpec with
                               GivenWhenThen with
                               ShouldMatchers with
                               GeneratorDrivenPropertyChecks {
  def name: String
  def classGen: Gen[T]
  def equal2ClassGen: Gen[(T, T)]
  def equal3ClassGen: Gen[(T, T, T)]
  def subClassName: String = ""
  def subClassGen: Option[Gen[_ <: T]] = None

  feature("Equals is Reflexive") {
    scenario(s"$name") {
      Given("any non-null value x")
      When("x.equals(x)")
      Then("the result is true")
      forAll(classGen) {x =>
        x.equals(x) should be(true)
      }
    }
  }

  feature("Equals is Symmetric") {
    def checkSymmetric(name: String, gen: Gen[List[T]]) {
      scenario(s"$name") {
        Given("any non-null values x and y")
        When("x.equals(y) returns true (returns false)")
        Then("y.equals(x) returns true (returns false)")
        forAll(gen suchThat {_.size == 2}) {xs =>
          val x = xs(0)
          val y = xs(1)
          x.equals(y) should equal(y.equals(x))
        }
      }
    }

    checkSymmetric(name, Gen.containerOfN[List, T](2, classGen))
    scenario(s"Equal pairs of $name") {
      forAll(equal2ClassGen) {case (x, y) =>
        if (x.equals(y)) {
          x.equals(y) should be(true)
          y.equals(x) should be(true)
        }
      }
    }

    if (subClassGen.isDefined)
      checkSymmetric(s"$name and $subClassName", Gen.sequence[List, T](Seq(classGen, subClassGen.get)))
  }

  feature("Equals is Transitive") {
    def checkTransProp(x: T, y: T, z: T) {
      if (x.equals(y) && y.equals(z))
        x.equals(z) should be(true)
      else
        x.equals(z) should be(false)
    }
    def checkTransitive(name: String, gen: Gen[List[T]]) {
      scenario(s"$name") {
        Given("any non-null values x, y, and z")
        When("x.equals(y) is true and y.equals(z) is true ")
        Then("x.equals(z) is true")
        forAll(gen suchThat {_.size == 3}) {cps =>
          checkTransProp(cps(0), cps(1), cps(2))
        }
      }
    }

    checkTransitive(name, Gen.containerOfN[List, T](3, classGen))
    scenario(s"Equal triples of $name") {
      forAll(equal3ClassGen) {case (x, y, z) =>
        checkTransProp(x, y, z)
      }
    }

    if (subClassGen.isDefined)
      checkTransitive(s"$name and $subClassName", Gen.containerOfN[List, T](3, Gen.oneOf[T](classGen, subClassGen.get)))
  }

  feature("Equals with null") {
    scenario(s"$name") {
      Given("any non-null value x")
      When("x.equals(null)")
      Then("the result is false")
      forAll(classGen) {x =>
        x.equals(null) should be(false)
      }
    }
  }

  feature("Equals is consistent with hashCode") {
    scenario(s"Equal pairs of $name") {
      Given("any non-null values x and y")
      When("x.equals(y) returns true")
      Then("x.hashCode == y.hashCode")
      forAll(equal2ClassGen) {case (x, y) =>
        x.hashCode() should equal(y.hashCode())
      }
    }
  }
}
