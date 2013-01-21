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

package org.scalaequals

import org.scalaequals.EqualsImpl.EqualsPayload
import reflect.macros.Context

/** Implementation of `ScalaEquals.hash` macro
  *
  * @author Alex DiCarlo
  * @version 1.0.0
  * @since 0.2.0
  */
object HashCodeImpl {
  def hash(c: Context): c.Expr[Int] = {
    new HashMaker[c.type](c).make()
  }

  private[HashCodeImpl] class HashMaker[C <: Context](val c: C) {

    import c.universe._

    val selfSymbol: Symbol = c.enclosingClass.symbol
    val selfTpe: Type = selfSymbol.asType.toType

    def createSuperHashCode(): Apply =
      Apply(
        Select(
          Super(
            This(tpnme.EMPTY),
            tpnme.EMPTY),
          newTermName("hashCode")),
        List()
      )

    def createHash(terms: List[Tree]): Tree = {
      Apply(
        Select(
          Select(
            Select(
              Ident(newTermName("java")),
              newTermName("util")),
            newTermName("Objects")),
          newTermName("hash")),
        List(
          Apply(
            Select(
              Select(
                Select(
                  Ident(newTermName("scala")),
                  newTermName("collection")),
                newTermName("Seq")),
              newTermName("apply")),
            terms)))
    }

    def extractPayload(): EqualsPayload = {
      val equalsMethod = c.enclosingClass find {t =>
        t.isDef && t.symbol.isMethod && t.symbol.asMethod.name == ("equals": TermName)
      }
      equalsMethod match {
        case Some(method) => method.attachments.get[EqualsImpl.EqualsPayload] match {
          case Some(payload) => payload
          case None => c.abort(c.enclosingPosition, Errors.incorrectHashOrdering)
        }
        case None => c.abort(c.enclosingPosition, Errors.missingEquals)
      }
    }

    def make(): c.Expr[Int] = {
      if (c.enclosingMethod.symbol.name != ("hashCode": TermName))
        c.abort(c.enclosingPosition, Errors.incorrectHashCallSite)
      val payload = extractPayload()
      val values =
        selfTpe.members.filter {t => t.isTerm && (payload.values contains {t.name.encoded})} map {_.asTerm}
      val terms = (values map {t => Select(This(tpnme.EMPTY), t)}).toList
      val hash = if (payload.superHashCode) createHash(createSuperHashCode() :: terms) else createHash(terms)
      c.Expr[Int](hash)
    }
  }
}
