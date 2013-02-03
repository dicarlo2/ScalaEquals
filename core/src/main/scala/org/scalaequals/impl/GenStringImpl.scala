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

/** Implementation of `ScalaEquals.genString` macro
  *
  * @author Alex DiCarlo
  * @version 1.1.1
  * @since 1.1.0
  */
private[scalaequals] object GenStringImpl {
  def genStringImpl(c: Context): c.Expr[String] = {
    new GenStringMaker[c.type](c).make()
  }

  def genStringParamsImpl(c: Context)(param: c.Expr[Any], params: c.Expr[Any]*): c.Expr[String] = {
    new GenStringMaker[c.type](c).make((param +: params).to[List])
  }

  private[GenStringImpl] class GenStringMaker[A <: Context](val c: A) extends Locator {
    type C = A
    import c.universe._

    val warn = !(c.settings contains "scala-equals-no-warn")

    def make(): c.Expr[String] = {
      if(c.enclosingClass.symbol.asClass.isTrait && warn)
        c.warning(c.enclosingClass.pos, Warnings.genStringWithTrait)
      makeString(findCtorArguments(c.enclosingClass, c.enclosingClass.symbol.asType.toType))
    }

    def make(params: Seq[c.Expr[Any]]): c.Expr[String] = {
      val args = (params map {_.tree.symbol.name.toTermName}).to[List]
      makeString(args)
    }

    def makeString(args: List[TermName]): c.Expr[String] = {
      if (!isToString(c.enclosingMethod.symbol))
        c.abort(c.enclosingMethod.pos, Errors.badToStringCallSite)

      val stringArgs = nestedAdd(args)
      val className = Literal(Constant(c.enclosingClass.symbol.name.toString + "("))
      val tree = stringAdd(className, stringArgs)
      c.Expr[String](tree)
    }

    def nestedAdd(terms: List[TermName]): Tree = terms match {
      case Nil => Literal(Constant(")"))
      case x :: Nil => stringAdd(Ident(x), Literal(Constant(")")))
      case x :: xs => stringAdd(stringAdd(Ident(x), Literal(Constant(", "))), nestedAdd(xs))
    }

    def stringAdd(left: Tree, right: Tree): Tree =
      Apply(
        Select(
          left,
          newTermName("$plus")),
        List(
          right))
  }
}
