package com.scalaequals

import reflect.macros.Context

object CanEqualImpl {
  def canEqual(c: Context)(other: Any): c.Expr[Boolean] = {
    import c.universe._
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
