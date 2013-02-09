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

package org.scalaequals.impl

import scala.reflect.macros.Context
import scala.language.existentials

private[scalaequals] object ProductImpl {
  def productElementImpl(c: Context) = new ProductElementMaker[c.type](c).make

  def productArityImpl(c: Context) = new ProductArityMaker[c.type](c).make

  def productPrefixImpl(c: Context) = new ProductPrefixMaker[c.type](c).make

  private[ProductImpl] class ProductElementMaker[A <: Context](val c: A) extends Locator {
    type C = A
    import c.universe._

    val productElementMethod = c.enclosingDef

    abortIf(!isProductElement(productElementMethod.symbol), badProductElementCallSite)

    def make = {
      val constrArgs = constrValsNotInherited(tpe)
      val cases = constrArgs.zipWithIndex map {case (constrArg, idx) => mkElementCase(idx, constrArg.name.toTermName)}
      val arg = argN(productElementMethod, 0).name
      c.Expr[Any](mkMatch(arg, cases :+ mkDefault(arg)))
    }

    def mkThrow(arg: Name) = Throw(mkApply(mkNew(typeOf[IndexOutOfBoundsException]), mkToString(arg)))
    def mkElementCase(n: Int, member: TermName) = CaseDef(Literal(Constant(n)), EmptyTree, mkThisSelect(member))
    def mkDefault(arg: Name) = CaseDef(Ident(nme.WILDCARD), EmptyTree, mkThrow(arg))
    def mkMatch(arg: Name, cases: List[CaseDef]) = Match(Ident(arg), cases)
  }

  private[ProductImpl] class ProductPrefixMaker[A <: Context](val c: A) extends Locator {
    type C = A
    import c.universe._

    val productPrefixMethod = c.enclosingDef

    abortIf(!isProductPrefix(productPrefixMethod.symbol), badProductPrefixCallSite)

    def make = c.Expr[String](Literal(Constant(c.enclosingImpl.symbol.name.decoded)))
  }

  private[ProductImpl] class ProductArityMaker[A <: Context](val c: A) extends Locator {
    type C = A
    import c.universe._

    val productArityMethod = c.enclosingDef

    abortIf(!isProductArity(productArityMethod.symbol), badProductArityCallSite)

    def make = c.Expr[Int](Literal(Constant(constrValsNotInherited(tpe).size)))
  }
}
