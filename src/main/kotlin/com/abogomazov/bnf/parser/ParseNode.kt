package com.abogomazov.com.abogomazov.bnf.parser

sealed class ParseNode {
    val tags = mutableSetOf<Enum<*>>()

    fun addTags(newTags: Collection<Enum<*>>) {
        tags.addAll(newTags)
    }

    data class Leaf(val text: String) : ParseNode()
    data class Group(val children: List<ParseNode>) : ParseNode()
    object Empty : ParseNode()

    fun get(index: Int): ParseNode = (this as? Group)?.children?.getOrNull(index) ?: Empty

    fun text(): String = when(this) {
        is Leaf -> text
        is Group -> children.joinToString("") { it.text() }
        is Empty -> ""
    }
}