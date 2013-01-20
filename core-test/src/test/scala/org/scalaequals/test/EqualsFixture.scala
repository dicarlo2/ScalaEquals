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

import org.scalatest.{GivenWhenThen, FeatureSpec}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalacheck.Gen

trait EqualsFixture[T] extends FeatureSpec with
                               GivenWhenThen with
                               ShouldMatchers with
                               GeneratorDrivenPropertyChecks {
  implicit override val generatorDrivenConfig = PropertyCheckConfig(minSuccessful = 10000, workers = 4)
  def name: String
  def classGen: Gen[T]
  def equal2ClassGen: Gen[(T, T)]
  def equal3ClassGen: Gen[(T, T, T)]
  def subClassName: String = ""
  def subClassGen: Option[Gen[_ <: T]] = None

  feature("ScalaEquals is Reflexive") {
    scenario(s"$name") {
      Given("any non-null value x")
      When("x.equals(x)")
      Then("the result is true")
      forAll(classGen) {x =>
        x.equals(x) should be(true)
      }
    }
  }

  feature("ScalaEquals is Symmetric") {
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
      Given("2 equal non-null values x and y")
      Then("x.equals(y) and y.equals(x) is true")
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

  feature("ScalaEquals is Transitive") {
    def checkTransProp(x: T, y: T, z: T) {
      if (x.equals(y) && y.equals(z))
        x.equals(z) should be(true)
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
      Given("any non-null values x, y, and z")
      When("x.equals(y) is true and y.equals(z) is true ")
      Then("x.equals(z) is true")
      forAll(equal3ClassGen) {case (x, y, z) =>
        checkTransProp(x, y, z)
      }
    }

    if (subClassGen.isDefined)
      checkTransitive(s"$name and $subClassName", Gen.containerOfN[List, T](3, Gen.oneOf[T](classGen, subClassGen.get)))
  }

  feature("ScalaEquals with null") {
    scenario(s"$name") {
      Given("any non-null value x")
      When("x.equals(null)")
      Then("the result is false")
      forAll(classGen) {x =>
        x.equals(null) should be(false)
      }
    }
  }

  feature("ScalaEquals is consistent with hashCode") {
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
