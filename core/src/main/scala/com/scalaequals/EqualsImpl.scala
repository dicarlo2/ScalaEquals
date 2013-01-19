package com.scalaequals

import reflect.macros.Context
import reflect.internal.Symbols
import scala.language.experimental.macros

object EqualsImpl {
  def equalImpl(c: Context)(other: c.Expr[Any]): c.Expr[Boolean] = {
    val eqm = new EqualsMaker[c.type](c)
    new eqm.EqualsMakerInner(other).make()
  }

  def equalCImpl(c: Context)(other: c.Expr[Any]): c.Expr[Boolean] = {
    val eqm = new EqualsMaker[c.type](c)
    new eqm.EqualsMakerInner(other).makeC()
  }

  def equalParamImpl(c: Context)
    (other: c.Expr[Any], param: c.Expr[Any], params: c.Expr[Any]*): c.Expr[Boolean] = {
    val eqm = new EqualsMaker[c.type](c)
    new eqm.EqualsMakerInner(other).make(param +: params)
  }

  private[EqualsImpl] class EqualsMaker[C <: Context](val c: C) {
    class EqualsMakerInner(other: c.Expr[Any]) {

      import c.universe._

      private val selfSymbol: Symbol = c.enclosingClass.symbol
      private val selfTpe: Type = selfSymbol.asType.toType

      def findOwner(owner: Symbol): Symbol = if (owner.isType) owner.asType else findOwner(owner.owner)

      def hasCanEqual: Boolean = !selfTpe.member("canEqual": TermName).isInstanceOf[Symbols#NoSymbol]

      def hasSuperClassWithEquals: Boolean = {
        val overriding = selfTpe.baseClasses map {_.asType.toType} filter {_.member("equals": TermName).isOverride}
        overriding exists {tpe => !(tpe =:= typeOf[AnyRef] || tpe =:= typeOf[Object] || tpe =:= selfTpe)}
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

      def createMatch(condition: Apply): Match = {
        Match(Ident(other.tree.symbol.asTerm), List(
          CaseDef(Bind(newTermName("that"), Typed(Ident(nme.WILDCARD), Ident(selfSymbol))), condition),
          CaseDef(Ident(nme.WILDCARD), EmptyTree, Literal(Constant(false)))
        ))
      }

      def createCondition(values: Seq[TermSymbol]): c.Expr[Boolean] = {
        val termEquals = (values map {createTermEquals(_)}).toList
        val and = (hasCanEqual, hasSuperClassWithEquals) match {
          case (true, true) => createNestedAnd(createCanEqual() :: createSuperEquals() :: termEquals)
          case (false, true) => createNestedAnd(createSuperEquals() :: termEquals)
          case (true, false) if termEquals.size == 0 => createNestedAnd(List(createCanEqual(), createSuperEquals()))
          case (true, false) => createNestedAnd(createCanEqual() :: termEquals)
          case (false, false) if termEquals.size == 0 => createSuperEquals()
          case (false, false) => createNestedAnd(termEquals)
        }
        c.Expr[Boolean](createMatch(and))
      }

      def make(): c.Expr[Boolean] = {
        c.enclosingClass
        val values = selfTpe.members filter {_.isTerm} map {_.asTerm} filter
          {m => m.isVal && !m.getter.isInstanceOf[Symbols#NoSymbol]}
        createCondition(values.toSeq.reverse)
      }

      def makeC(): c.Expr[Boolean] = {
        val values = selfTpe.members filter {_.isTerm} map {_.asTerm} filter
          {m => m.isVal && m.isParamAccessor && !m.getter.isInstanceOf[Symbols#NoSymbol]}
        createCondition(values.toSeq.reverse)
      }

      def make(params: Seq[c.Expr[Any]]): c.Expr[Boolean] = {
        val terms = params map {_.tree.symbol.asTerm}
        createCondition(terms)
      }
    }
  }
}