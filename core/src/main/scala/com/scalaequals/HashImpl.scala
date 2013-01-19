package com.scalaequals

import reflect.macros.Context

object HashImpl {
  def hash(c: Context): c.Expr[Int] = {
    import c.universe._

    def createHash(terms: Seq[TermSymbol]): Tree = {
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
            terms.map(term => Select(This(tpnme.EMPTY), term)).toList)))
    }
    val hash: Tree =
      c.enclosingClass.find(t => t.isDef && t.symbol.isMethod && t.symbol.asMethod.name == ("equals": TermName)) match {
        case Some(method) =>
          c.echo(c.enclosingPosition, method.attachments.all.toString())
          method.attachments.all.find(_.isInstanceOf[Seq[TermSymbol]]) match {
            case Some(attachments: Seq[TermSymbol]) =>
              createHash(attachments)
            case None =>
              c.error(c.enclosingPosition, "No attachments found on equals, are you sure you used Equals.equal?")
              Literal(Constant(0))
          }
        case None =>
          c.error(c.enclosingPosition, "No equals method found")
          Literal(Constant(0))
      }
    c.Expr[Int](hash)
  }
}

class Test(val x: Int, val y: Int) {
  override def equals(other: Any): Boolean = Equals.equal(other)

  override def hashCode: Int = Equals.hash
}
