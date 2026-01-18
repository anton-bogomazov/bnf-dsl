package com.abogomazov

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

data class Match(val node: ParseNode, val nextContext: ParseContext)

data class ParseContext(val input: String, val position: Int = 0) {
    fun remaining() = input.substring(position)
    fun advance(n: Int) = copy(position = position + n)
}