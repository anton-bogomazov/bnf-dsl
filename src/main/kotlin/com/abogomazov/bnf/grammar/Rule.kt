package com.abogomazov.com.abogomazov.bnf.grammar

data class Rule(
    val symbol: Enum<*>,
    val type: BnfType,
)