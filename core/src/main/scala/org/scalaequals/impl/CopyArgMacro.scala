package org.scalaequals.impl

import scala.reflect.macros.Context

object CopyArgMacro {
  def argImpl(c: Context)(list: c.Expr[Int])(idx: c.Expr[Int]) = new CopyArgMaker[c.type](c).makeArg(list)(idx)

  private[impl] class CopyArgMaker[A <: Context](val c: A) extends Locator {
    type C = A
    import c.universe._

    def makeArg(list: c.Expr[Int])(idx: c.Expr[Int]) = {
      val paramss = constrParamss(c.enclosingTemplate)
      val param = paramss(extractInt(list))(extractInt(idx))
      val tpe = param.symbol.asTerm.alternatives.find {_.typeSignature != null}
      TypeTree(tpe.get.typeSignature)
    }

    private def extractInt(x: c.Expr[Int]) = {
      val x1 = c.Expr[Int](c.resetAllAttrs(x.tree.duplicate))
      c.eval(x1)
    }

    private def constrParamss(body: Template) =
      (body collect {case DefDef(_, name, _, vparamss, _, _) if isConstructorName(name) => vparamss}).head
  }
}
