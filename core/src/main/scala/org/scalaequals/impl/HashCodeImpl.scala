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
import org.scalaequals.impl.EqualsImpl.EqualsPayload

/** Implementation of `ScalaEquals.hash` macro
  *
  * @author Alex DiCarlo
  * @version 1.1.1
  * @since 0.2.0
  */
private[scalaequals] object HashCodeImpl {
  def hash(c: Context): c.Expr[Int] = new HashMaker[c.type](c).make()

  private[HashCodeImpl] class HashMaker[A <: Context](val c: A) extends Locator {
    type C = A
    import c.universe._

    if (!isHashCode(c.enclosingMethod.symbol)) c.abort(c.enclosingMethod.pos, Errors.badHashCallSite)

    def make() = {
      val payload = extractPayload()
      val values = tpe.members filter {t => t.isTerm && (payload.values contains {t.name.encoded})} map {_.asTerm}
      val terms = (values map {t => Select(This(tpnme.EMPTY), t)}).toList
      val hash = if (payload.superHashCode) createHash(mkSuperHashCode :: terms) else createHash(terms)

      c.Expr[Int](hash)
    }

    def extractPayload() = {
      findEquals(c.enclosingClass) match {
        case Some(method) => method.attachments.get[EqualsImpl.EqualsPayload] match {
          case Some(payload) => payload
          case None => c.typeCheck(method).attachments.get[EqualsImpl.EqualsPayload] match {
            case Some(payload) => payload
            case None => c.abort(c.enclosingPosition, Errors.missingEqual)
          }
        }
        case None => c.abort(c.enclosingPosition, Errors.missingEquals)
      }
    }

    def mkSuperHashCode = mkApply(mkSuperSelect(_hashCode))
    def mkSeqHash = mkSelect(newTermName("scala"), newTermName("util"), newTermName("hashing"),
      newTermName("MurmurHash3"), newTermName("seqHash"))
    def mkList = mkSelect(newTermName("scala"), newTermName("collection"), newTermName("immutable"),
      newTermName("List"), newTermName("apply"))
    def createHash(terms: List[Tree]) = mkApply(mkSeqHash, Apply(mkList, terms))
  }
}
