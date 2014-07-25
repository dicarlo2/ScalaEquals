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

/** Implementation of `ScalaEquals.hash` macro
  *
  * @author Alex DiCarlo
  * @version 1.2.0
  * @since 0.2.0
  */
private[scalaequals] object HashCodeImpl {

  // provides a source compatibility stub
  // in Scala 2.10.x, it will make `import compat._` compile just fine,
  // even though `c.universe` doesn't have `compat`
  // in Scala 2.11.0, it will be ignored, becase `import c.universe._`
  // brings its own `compat` in scope and that one takes precedence
  private object HasCompat { val compat = ??? }
  import HasCompat._

  def hash(c: Context) =
    new HashMaker[c.type](c, c.enclosingMethod == null).make()

  def customHash(c: Context)(hashFunction: c.Expr[Array[Any] => Int]) =
    new HashMaker[c.type](c, c.enclosingMethod == null).make(hashFunction)

  private[HashCodeImpl] class HashMaker[A <: Context](val c: A, isLazy: Boolean) extends Locator {
    type C = A
    import c.universe._
    import compat._

    abortIf(!isLazy && !isHashCode(c.enclosingMethod.symbol), badHashCallSite)
    abortIf(isLazy && findLazyHash(tpe).isEmpty, badHashCallSite)

    def make(f: c.Expr[Array[Any] => Int] = c.Expr[Array[Any] => Int](mkArrayHash)) = {
      val payload = extractPayload()
      val values = tpe.members filter {t => t.isTerm && (payload.values contains {t.name.encoded})} map {_.asTerm}
      val terms = (values map {t => Select(This(tpnme.EMPTY), t)}).toList
      val hash = if (payload.superHashCode) createHash(f, mkSuperHashCode :: terms) else createHash(f, terms)

      c.Expr[Int](hash)
    }

    def extractPayload() = {
      findEquals(c.enclosingClass) match {
        case Some(method) => method.attachments.get[EqualsImpl.EqualsPayload] match {
          case Some(payload) => payload
          case None => c.typeCheck(method).attachments.get[EqualsImpl.EqualsPayload] match {
            case Some(payload) => payload
            case None => c.abort(c.enclosingPosition, missingEqual)
          }
        }
        case None => c.abort(c.enclosingPosition, missingEquals)
      }
    }

    def mkSuperHashCode = mkApply(mkSuperSelect(_hashCode))
    def mkArrayHash = mkSelect(newTermName("scala"), newTermName("util"), newTermName("hashing"),
      newTermName("MurmurHash3"), newTermName("arrayHash"))
    def mkArray = mkTpeApply(mkSelect(newTermName("scala"), newTermName("Array"), newTermName("apply")),
      Ident(newTypeName("Any")))
    def createHash(hashFunction: c.Expr[Array[Any] => Int], terms: List[Tree]) =
      mkApply(hashFunction.tree, Apply(mkArray, terms))
  }
}
