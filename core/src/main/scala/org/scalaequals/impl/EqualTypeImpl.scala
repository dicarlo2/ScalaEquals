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

import scala.reflect.macros.{Macro, Context}
import org.scalaequals.ScalaEquals

/** Implementation of `ScalaEquals.Equal` type macro
  *
  * @author Alex DiCarlo
  * @version 2.0.0
  * @since 2.0.0
  */
trait EqualTypeImpl extends Macro {
  import c.universe._

  private val locator = new Locator[c.type](c)

  def make: c.Tree = {
    val Template(parents, self, existingCode) = c.enclosingTemplate
    val filteredParents = filterScalaEquals(parents)
    val newMethods = List(genEquals(), genHashCode(), genCanEqual(parents))
    Template(filteredParents, self, existingCode ++ newMethods)
  }

  def genEquals(): Tree =
    q"override def equals(other: Any): Boolean = org.scalaequals.ScalaEquals.equal".asInstanceOf[DefDef]
  def genHashCode(): Tree =
    q"override def hashCode(): Int = org.scalaequals.ScalaEquals.hash".asInstanceOf[DefDef]
  def genCanEqual(parents: List[Tree]): Tree =
    if (locator.hasSuperWithCanEqual(parents))
      q"override def canEqual(other: Any): Boolean = org.scalaequals.ScalaEquals.canEquals".asInstanceOf[DefDef]
    else
      q"def canEqual(other: Any): Boolean = org.scalaequals.ScalaEquals.canEquals".asInstanceOf[DefDef]

  private def filterScalaEquals(parents: List[Tree]): List[Tree] = {
    def isScalaEquals(tree: Tree): Boolean = tree match {
      case Ident(TypeName("Equal")) => true
      case Select(Ident(TermName("ScalaEquals")), TypeName("Equal")) => true
      case _ => false
    }
    parents filterNot {_ exists isScalaEquals}
  }
}
