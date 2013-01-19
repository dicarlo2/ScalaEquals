package com.scalaequals

import reflect.macros.Context
import reflect.internal.Symbols
import scala.language.experimental.macros

object Equals {
  def equal[T](other: Any): Boolean = macro equalImpl[T]

  def equal[T](other: Any, param: Any, params: Any*): Boolean = macro equalParamImpl[T]

  def equalImpl[T: c.WeakTypeTag](c: Context)(other: c.Expr[Any]): c.Expr[Boolean] = {
    val eqm = new EqualsMaker[c.type](c)
    new eqm.EqualsMakerInner[T](other).make()
  }

  def equalParamImpl[T: c.WeakTypeTag](c: Context)
    (other: c.Expr[Any], param: c.Expr[Any], params: c.Expr[Any]*): c.Expr[Boolean] = {
    val eqm = new EqualsMaker[c.type](c)
    new eqm.EqualsMakerInner[T](other).make(param +: params)
  }

  class EqualsMaker[C <: Context](val c: C) {
    class EqualsMakerInner[T: c.WeakTypeTag](other: c.Expr[Any]) {

      import c.universe._

      def hasCanEqual: Boolean =
        !c.weakTypeTag[T].tpe.member("canEqual": TermName).isInstanceOf[Symbols#NoSymbol]

      def hasSuperClassWithEquals: Boolean = {
        val overriding =
          c.weakTypeTag[T].tpe.baseClasses map {_.asType.toType} filter {_.member("equals": TermName).isOverride}
        overriding exists {tpe => !(tpe =:= typeOf[AnyRef] || tpe =:= typeOf[Object] || tpe =:= c.weakTypeTag[T].tpe)}
      }

      def createCanEqual(): Apply =
        Apply(Select(Ident(newTermName("that")), newTermName("canEqual")), List(This(tpnme.EMPTY)))

      def createTermEquals(term: c.universe.TermSymbol): Apply =
        Apply(Select(Select(Ident(newTermName("that")), term.getter), newTermName("$eq$eq")),
          List(Select(This(tpnme.EMPTY), term.getter)))

      def createAnd(left: Apply): Select = Select(left, newTermName("$amp$amp"))

      def createNestedAnd(terms: List[Apply]): Apply = terms match {
        case left :: x :: xs => createNestedAnd(Apply(createAnd(left), List(x)) :: xs)
        case left :: Nil => left
      }

      def createSuperEquals(): Apply =
        Apply(Select(Super(This(tpnme.EMPTY), tpnme.EMPTY), newTermName("equals")), List(Ident(newTermName("that"))))

      def createCondition(values: Seq[TermSymbol], superEquals: Boolean = false): c.Expr[Boolean] = {
        val termEquals = (values map {createTermEquals(_)}).toList
        val terms = if (superEquals) createSuperEquals() :: termEquals else termEquals
        val and = if (hasCanEqual) createNestedAnd(createCanEqual() :: terms) else createNestedAnd(terms)
        val eqExpr = c.Expr[Boolean](and)
        reify {
          other.splice match {
            case that: T => eqExpr.splice
            case _ => false
          }
        }
      }

      def make(): c.Expr[Boolean] = {
        val values = c.weakTypeTag[T].tpe.members filter {_.isTerm} map {_.asTerm} filter {_.isVal}
        createCondition(values.toSeq)
      }

      def make(params: Seq[c.Expr[Any]]): c.Expr[Boolean] = {
        val terms = params map {_.tree.symbol.asTerm}
        createCondition(terms)
      }
    }
  }
}
//
//class Test1(val x: Int) {
//  override def equals(other: Any): Boolean = Equals.equal[Test1](other, x)
//
//  def canEqual(other: Any): Boolean = other.isInstanceOf[Test1]
//}
//
//// value <none> is not a member of Test2
//class Test2(override val x: Int, val y: Int) extends Test1(x) {
//  override def equals(other: Any): Boolean = Equals.equal[Test2](other, x, y)
//
//  override def canEqual(other: Any): Boolean = other.isInstanceOf[Test2]
//}
//
//object hello {
//  val test1 = new Test1(1)
//  val test11 = new Test1(1)
//  val test111 = new Test1(2)
//  val test2 = new Test2(1, 2)
//  val test22 = new Test2(1, 2)
//  val test222 = new Test2(2, 2)
//}