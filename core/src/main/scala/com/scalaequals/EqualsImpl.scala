package com.scalaequals

import reflect.macros.Context
import reflect.internal.Symbols

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

      val selfSymbol: Symbol = c.enclosingClass.symbol
      val selfTpe: Type = selfSymbol.asType.toType
      val hasCanEqual: Boolean = !selfTpe.member("canEqual": TermName).isInstanceOf[Symbols#NoSymbol]
      val hasSuperClassWithEquals: Boolean = {
        val overriding = selfTpe.baseClasses map {_.asType.toType} filter {_.member("equals": TermName).isOverride}
        overriding exists {tpe => !(tpe =:= typeOf[AnyRef] || tpe =:= typeOf[Object] || tpe =:= selfTpe)}
      }

      def createCanEqual(): Apply =
        Apply(
          Select(
            Ident(newTermName("that")),
            newTermName("canEqual")),
          List(
            This(tpnme.EMPTY)))

      def createTermEquals(term: TermSymbol): Apply = {
        def createEquals(term: Symbol): Apply =
          Apply(
            Select(
              Select(
                Ident(newTermName("that")),
                term),
              newTermName("$eq$eq")),
            List(
              Select(
                This(tpnme.EMPTY),
                term)))
        if (term.isMethod) createEquals(term) else createEquals(term.getter)
      }

      def createAnd(left: Apply): Select = Select(left, newTermName("$amp$amp"))

      def createNestedAnd(terms: List[Apply]): Apply = terms match {
        case left :: x :: xs => createNestedAnd(Apply(createAnd(left), List(x)) :: xs)
        case left :: Nil => left
      }

      def createSuperEquals(): Apply =
        Apply(
          Select(
            Super(This(tpnme.EMPTY), tpnme.EMPTY),
            newTermName("equals")),
          List(
            Ident(newTermName("that")))
        )

      def createMatch(condition: Apply): Match = {
        Match(
          Ident(other.tree.symbol.asTerm),
          List(
            CaseDef(
              Bind(
                newTermName("that"),
                Typed(
                  Ident(nme.WILDCARD),
                  Ident(selfSymbol))),
              condition),
            CaseDef(
              Ident(nme.WILDCARD),
              EmptyTree,
              Literal(Constant(false))
            )
        ))
      }

      def createCondition(values: Seq[TermSymbol]): c.Expr[Boolean] = {
        if (c.enclosingMethod.symbol.name != ("equals": TermName))
          c.abort(c.enclosingPosition, Errors.incorrectEqualCallSite)
        val payload = EqualsPayload(values map {_.name.encoded}, hasSuperClassWithEquals || values.size == 0)
        c.enclosingMethod.updateAttachment(payload)
        val termEquals = (values map createTermEquals).toList
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
        val values = params map {_.tree.symbol.asTerm}
        createCondition(values)
      }
    }
  }

  case class EqualsPayload(values: Seq[String], superHashCode: Boolean)
}