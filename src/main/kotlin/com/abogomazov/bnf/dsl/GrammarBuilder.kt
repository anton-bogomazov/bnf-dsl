package com.abogomazov.com.abogomazov.bnf.dsl

import com.abogomazov.com.abogomazov.bnf.grammar.BnfType
import com.abogomazov.com.abogomazov.bnf.grammar.Grammar
import com.abogomazov.com.abogomazov.bnf.grammar.Rule

@BnfDsl
class GrammarBuilder {
    private val rules = mutableListOf<Rule>()

    fun symbol(name: Enum<*>, block: RuleBuilder.() -> BnfType) {
        val builder = RuleBuilder()
        val type = builder.block()
        type.markAsSymbol(name)
        val rule = Rule(name, type)

        rules.add(rule)
    }

    fun build() = Grammar(rules)
}