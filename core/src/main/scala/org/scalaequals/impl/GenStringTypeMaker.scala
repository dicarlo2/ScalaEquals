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


/** Implementation of `ScalaEquals.GenString` type macro
  *
  * @author Alex DiCarlo
  * @version 2.0.0
  * @since 2.0.0
  */
private[impl] trait GenStringTypeMaker {self: Locator =>
  import c.universe._

  private val genStringTypeName = "GenString"

  def addGenStringType(template: Template) = {
    if (hasScalaEqualsType(genStringTypeName, template.parents)) {
      val Template(parents, self, body) = template
      val filteredParents = filterScalaEqualsType(genStringTypeName, parents)
      Template(filteredParents, self, body ++ Seq(genString()))
    } else template
  }

  private def genString(): Tree =
    q"override def toString: String = org.scalaequals.ScalaEquals.genString".asInstanceOf[DefDef]
}
