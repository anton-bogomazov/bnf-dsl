package com.abogomazov.com.abogomazov.bnf.grammar

sealed class BnfType {
    var tags: MutableList<Enum<*>> = mutableListOf()

    data class Literal(val value: String) : BnfType()
    data class RegexMatch(val pattern: String) : BnfType()
    data class Sequence(val parts: List<BnfType>) : BnfType()
    data class Reference(val name: Enum<*>) : BnfType()
    data class Choice(val options: List<BnfType>) : BnfType()

    fun markAsSymbol(t: Enum<*>) {
        tags.add(t)
    }
}