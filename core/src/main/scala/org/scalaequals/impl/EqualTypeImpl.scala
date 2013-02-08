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
import scala.language.existentials
import org.scalaequals.impl.GenStringTypeImpl.GenStringTypeMaker

/** Implementation of `ScalaEquals.Equal` type macro
  *
  * @author Alex DiCarlo
  * @version 2.0.0
  * @since 2.0.0
  */

object EqualTypeImpl {

  def equalTypeImpl(c: Context): c.universe.Template = new EqualTypeMaker[c.type](c).make

  def equalTypeImplAll(c: Context): c.universe.Template = new EqualTypeMaker[c.type](c).makeAll

  private[impl] class EqualTypeMaker[A <: Context](val c: A) extends Locator {
    type C = A
    import c.universe._

    private lazy val genStringTypeImpl = new GenStringTypeMaker[c.type](c)
    private val equalTypeName = "Equal"
    private val equalAllValsTypeName = "EqualAllVals"

    def make: Template = createTemplate(c.enclosingTemplate, genEqual(), equalTypeName)

    def makeAll: Template = createTemplate(c.enclosingTemplate, genEqualAllVals(), equalAllValsTypeName)

    def addToTemplate(template: Template): Template = {
      if (hasScalaEqualsType(equalTypeName, template.parents))
        createTemplate(template, genEqual(), equalTypeName)
      else if (hasScalaEqualsType(equalAllValsTypeName, template.parents))
        createTemplate(template, genEqualAllVals(), equalAllValsTypeName)
      else
        template
    }

    private def createTemplate(template: Template, equalMethod: Tree, equalName: String): Template = {
      val Template(parents, self, existingCode) = c.enclosingTemplate
      val filteredParents = filterScalaEqualsType(equalName, parents)
      val parentsWithEquals = filteredParents :+ Ident(typeOf[Equals].typeSymbol)
      val newMethods = List(equalMethod, genHash(), genCanEquals(parents))
      val newTemplate = Template(parentsWithEquals, self, existingCode ++ newMethods)
      genStringTypeImpl.addToTemplate(newTemplate)
    }

    private def genEqual(): Tree =
      q"override def equals(other: Any): Boolean = org.scalaequals.ScalaEquals.equal".asInstanceOf[DefDef]
    private def genEqualAllVals(): Tree =
      q"override def equals(other: Any): Boolean = org.scalaequals.ScalaEquals.equalAllVals".asInstanceOf[DefDef]
    private def genHash(): Tree =
      q"override def hashCode(): Int = org.scalaequals.ScalaEquals.hash".asInstanceOf[DefDef]
    private def genCanEquals(parents: List[Tree]): Tree =
      if (hasSuperWithCanEqual(parents))
        q"override def canEqual(other: Any): Boolean = org.scalaequals.ScalaEquals.canEquals".asInstanceOf[DefDef]
      else
        q"def canEqual(other: Any): Boolean = org.scalaequals.ScalaEquals.canEquals".asInstanceOf[DefDef]
  }
}