package com.abogomazov.com.abogomazov.bnf.parser

data class ParseContext(val input: String, val position: Int = 0) {
    fun remaining() = input.substring(position)
    fun advance(n: Int) = copy(position = position + n)
}