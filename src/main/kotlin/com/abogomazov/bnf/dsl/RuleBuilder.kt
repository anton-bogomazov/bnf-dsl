package com.abogomazov.com.abogomazov.bnf.dsl

import com.abogomazov.com.abogomazov.bnf.grammar.BnfType
import com.abogomazov.com.abogomazov.bnf.grammar.Grammar

fun format(block: GrammarBuilder.() -> Unit): Grammar {
    return GrammarBuilder().apply(block).build()
}

class RuleBuilder {
    fun seq(vararg parts: BnfType) =
        BnfType.Sequence(parts.toList())

    fun oneOf(vararg options: BnfType) =
        BnfType.Choice(options.toList())

    val String.l get() = BnfType.Literal(this)
    val String.r get() = BnfType.RegexMatch(this)
    val Enum<*>.ref get() = BnfType.Reference(this)
}