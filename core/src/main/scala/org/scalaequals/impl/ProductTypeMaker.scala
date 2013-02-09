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

private[impl] trait ProductTypeMaker {self: Locator =>
  import c.universe._

  private val productTypeName = "Prodct"

  def addProductType(template: Template) = {
    if (hasScalaEqualsType(productTypeName, template.parents)) {
      val Template(parents, self, existingCode) = template
      val filteredParents = filterScalaEqualsType(productTypeName, parents)
      val parentsWithProduct = filteredParents :+ Ident(typeOf[Product].typeSymbol)
      val newMethods = List(genProductElement(), genProductArity(), genProductPrefix())
      Template(parentsWithProduct, self, existingCode ++ newMethods)
    } else template
  }

  private def genProductElement(): Tree =
    q"def productElement(n: Int): Any = org.scalaequals.ScalaEquals.productElement".asInstanceOf[DefDef]
  private def genProductArity(): Tree =
    q"def productArity: Int = org.scalaequals.ScalaEquals.productArity".asInstanceOf[DefDef]
  private def genProductPrefix(): Tree =
    q"override def productPrefix: String = org.scalaequals.ScalaEquals.productPrefix".asInstanceOf[DefDef]
}