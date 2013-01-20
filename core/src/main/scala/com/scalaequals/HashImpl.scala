package com.scalaequals

import reflect.macros.Context
import com.scalaequals.EqualsImpl.EqualsPayload
import scala.language.postfixOps

object HashImpl {
  def hash(c: Context): c.Expr[Int] = {
    new HashMaker[c.type](c).make()
  }

  private[HashImpl] class HashMaker[C <: Context](val c: C) {

    import c.universe._

    val selfSymbol: Symbol = c.enclosingClass.symbol
    val selfTpe: Type = selfSymbol.asType.toType

    def createSuperHashCode(): Apply =
      Apply(
        Select(
          Super(This(tpnme.EMPTY), tpnme.EMPTY),
          newTermName("hashCode")),
        List()
      )

    def createHash(terms: List[Tree]): Tree = {
      Apply(
        Select(
          Select(
            Select(
              Ident(newTermName("java")), newTermName("util")),
            newTermName("Objects")),
          newTermName("hash")),
        List(
          Apply(
            Select(
              Select(
                Select(
                  Ident(newTermName("scala")), newTermName("collection")),
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
          case None => c.abort(c.enclosingPosition, "No attachments found on equals, did you use Equals.equal?")
        }
        case None => c.abort(c.enclosingPosition, "No overriding equals method found")
      }
    }

    def make(): c.Expr[Int] = {
      val payload = extractPayload()
      val values =
        selfTpe.members.filter {t => t.isTerm && (payload.values contains {t.name.encoded})} map {_.asTerm}
      val terms = values map {t => Select(This(tpnme.EMPTY), t)} toList
      val hash = if (payload.superHashCode) createHash(createSuperHashCode() :: terms) else createHash(terms)
      c.Expr[Int](hash)
    }
  }
}
