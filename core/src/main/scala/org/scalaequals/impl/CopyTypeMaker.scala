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

trait CopyTypeMaker {self: Locator =>
  import c.universe._
  import Flag._

  private val copyTypeName = "Copy"

  def addCopyType(template: Template) = {
    if (hasScalaEqualsType(copyTypeName, template.parents)) {
      val Template(parents, self, body) = template
      val filteredParents = filterScalaEqualsType(copyTypeName, parents)
      val meth = caseClassCopyMeth(c.enclosingImpl.asInstanceOf[ClassDef])
      //      echo(meth.toString)
      Template(filteredParents, self, body :+ meth.get)
    } else template
  }

  final def mmap[A, B](xss: List[List[A]])(f: A => B) =
    xss map (_ map f)

  def copyUntyped[T <: Tree](tree: T): T =
    returning[T](tree.duplicate)(UnTyper traverse _)

  object UnTyper extends Traverser {
    override def traverse(tree: Tree) = {
      if (tree.canHaveAttrs) {
        tree.setType(null)
        if (tree.symbol != null) tree.symbol = NoSymbol
      }
      super.traverse(tree)
    }
  }

  /** Apply a function and return the passed value */
  def returning[T](x: T)(f: T => Unit): T = { f(x); x }

  private def constrParamss(cdef: ClassDef): List[List[ValDef]] = {
    (cdef.impl.body.collect({case DefDef(_, name, _, vparamss, _, _) if isConstructorName(name) => vparamss})).head
  }

  private def firstConstructor(cdef: ClassDef) =
    (cdef.impl.body.collect {case x: DefDef if isConstructorName(x.name) => x}).head

  final val SYNTHETIC = 1 << 21
  // symbol is compiler-generated (compare with ARTIFACT)
  private def classType(cdef: ClassDef, tparams: List[TypeDef], symbolic: Boolean = true): Tree = {
    val tycon = if (symbolic) treeBuild.mkAttributedRef(cdef.symbol) else Ident(cdef.name)
    if (tparams.isEmpty) tycon else AppliedTypeTree(tycon, tparams map toIdent)
  }

  private def toIdent(x: DefTree) = {
    val i = Ident(x.name)
    i.asInstanceOf[TreeContextApi].setPos(x.pos.focus)
  }

  final val REPEATED_PARAM_CLASS_NAME = TermName("<repeated>")
  final val JAVA_REPEATED_PARAM_CLASS_NAME = TermName("<repeated...>")

  //  def isRepeatedParamType(tpt: Tree) = tpt match {
  //    case TypeTree() => definitions.isRepeatedParamType(tpt.tpe)
  //    case AppliedTypeTree(Select(_, REPEATED_PARAM_CLASS_NAME), _) => true
  //    case AppliedTypeTree(Select(_, JAVA_REPEATED_PARAM_CLASS_NAME), _) => true
  //    case _ => false
  //  }

  def caseClassCopyMeth(cdef: ClassDef): Option[DefDef] = {
    //    val typedClassParamss = constrParamss(cdef)
    val classParamss = mmap(constrParamss(cdef))(copyUntyped[ValDef])

    if (cdef.symbol.asClass.isAbstractClass) None
    else {
      def makeCopyParam(vd: ValDef, list: Int, idx: Int, putDefault: Boolean) = {
        val rhs = if (putDefault) toIdent(vd) else EmptyTree
        val flags = PARAM
        val flags2 = if (vd.mods.hasFlag(IMPLICIT)) flags | IMPLICIT else flags
        val flags3 = if (putDefault) flags2 | DEFAULTPARAM else flags2
        val tptTree =
                  DependentTypeTree(DependentTypeTree(Select(Select(Select(Ident(TermName("org")), TermName("scalaequals")),
                  TermName("ScalaEquals")),
                  TypeName("CopyArg")), List(Literal(Constant(list)))), List(Literal(Constant(idx))))
        val tpt = atPos(vd.pos.focus)(tptTree)
        treeCopy.ValDef(vd, Modifiers(flags3), vd.name, tpt, rhs)
      }

      val tparams = cdef.tparams //map copyUntypedInvariant
      val paramss = classParamss match {
          case Nil => Nil
          case ps :: pss =>
            val defaults = ps.zipWithIndex map {case (vd, idx) => makeCopyParam(vd, 0, idx, putDefault = true)}
            //            val rest = pss map  {_.zipWithIndex map {case (vd, idx) => makeCopyParam(vd, idx, putDefault = false)}}
            val rest = pss.zipWithIndex map {
              case (list, listIdx) => list.zipWithIndex map {
                case (vd, idx) => makeCopyParam(vd, listIdx + 1, idx, putDefault = false)
              }
            }
            defaults :: rest
          //            ps.zipWithIndex.map((vd, idx) => makeCopyParam(vd, putDefault = true)) :: mmap(pss)(makeCopyParam(_, putDefault = false))
        }

      val classTpe = classType(cdef, tparams)
      val argss = mmap(paramss)(toIdent)
      val body: Tree = New(classTpe, argss)
      val copyDefDef = atPos(cdef.pos.focus)(
        DefDef(Modifiers(/*SYNTHETIC.toLong.asInstanceOf[FlagSet]*/), "copy2": TermName, tparams, paramss,
          TypeTree(), body)
      )
      Some(copyDefDef)
    }
  }
}