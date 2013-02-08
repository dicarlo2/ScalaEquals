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

import scala.reflect.macros.{Context, Macro}
import org.scalaequals.impl.EqualTypeImpl.EqualTypeMaker

/** Implementation of `ScalaEquals.GenString` type macro
  *
  * @author Alex DiCarlo
  * @version 2.0.0
  * @since 2.0.0
  */

object GenStringTypeImpl {
  def genStringTypeImpl(c: Context): c.universe.Template = new GenStringTypeMaker[c.type](c).make

  private[impl] class GenStringTypeMaker[A <: Context](val c: A) extends Locator {
    type C = A
    import c.universe._

    private lazy val equalTypeImpl = new EqualTypeMaker[c.type](c)
    def make: Template = addGenString(c.enclosingTemplate)

    def addToTemplate(template: Template): Template =
      if (hasScalaEqualsType("GenString", template.parents)) addGenString(template) else template

    private def addGenString(template: Template): Template = {
      val Template(parents, self, body) = template
      val filteredParents = filterScalaEqualsType("GenString", parents)
      val newTemplate = Template(filteredParents, self, body ++ Seq(genString()))
      equalTypeImpl.addToTemplate(newTemplate)
    }

    private def genString(): Tree =
      q"override def toString: String = org.scalaequals.ScalaEquals.genString".asInstanceOf[DefDef]
  }
}
