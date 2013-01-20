package com.scalaequals

import reflect.macros.Context

object CanEqualImpl {
  def canEqual(c: Context)(other: Any): c.Expr[Boolean] = {
    import c.universe._
    if (c.enclosingMethod.symbol.name != ("canEqual": TermName))
      c.abort(c.enclosingPosition, Errors.incorrectCanEqualCallSite)
    val tree =
      TypeApply(
        Select(
          Ident(
            newTermName("other")),
          newTermName("isInstanceOf")),
        List(
          Ident(c.enclosingClass.symbol)))
    c.Expr[Boolean](tree)
  }
}
