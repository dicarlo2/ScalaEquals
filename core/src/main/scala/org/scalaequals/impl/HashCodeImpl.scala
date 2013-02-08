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
  * @version 2.0.0
  * @since 0.2.0
  */
private[scalaequals] object HashCodeImpl {
  def hash(c: Context) = new HashMaker[c.type](c).make()

  private[HashCodeImpl] class HashMaker[A <: Context](val c: A) extends Locator {
    type C = A
    import c.universe._

    val isLazy = enclosingDef.isEmpty
    abortIf(!isLazy && !isHashCode(c.enclosingDef.symbol), badHashCallSite)
    abortIf(isLazy && findLazyHash(tpe).isEmpty, badHashCallSite)

    def make() = {
      val payload = extractPayload()
      val values = tpe.members filter {t => t.isTerm && (payload.values contains {t.name.encoded})} map {_.asTerm}
      val terms = (values map {t => Select(This(tpnme.EMPTY), t)}).toList
      val hash = if (payload.superHashCode) createHash(mkSuperHashCode :: terms) else createHash(terms)

      c.Expr[Int](hash)
    }

    def extractPayload() = {
      findEquals(c.enclosingTemplate) match {
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
    def mkSeqHash = mkSelect(TermName("scala"), TermName("util"), TermName("hashing"),
      TermName("MurmurHash3"), TermName("seqHash"))
    def mkList = mkSelect(TermName("scala"), TermName("collection"), TermName("immutable"),
      TermName("List"), TermName("apply"))
    def createHash(terms: List[Tree]) = mkApply(mkSeqHash, Apply(mkList, terms))
  }
}
