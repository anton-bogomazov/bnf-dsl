package com.abogomazov.com.abogomazov.bnf.parser

import com.abogomazov.com.abogomazov.bnf.grammar.Rule
import com.abogomazov.com.abogomazov.bnf.grammar.BnfType
import com.abogomazov.com.abogomazov.bnf.parser.result.ParsingResult

class Parser(rules: List<Rule>) {
    private val rules = rules.associateBy { it.symbol }.toMutableMap()

    fun parse(ruleName: Enum<*>, input: String): ParsingResult {
        val startRule = rules[ruleName] ?: error("Rule $ruleName not found")
        val matchResult = match(startRule.type, ParseContext(input)) ?: error("Invalid input")

        val result = mutableMapOf<Enum<*>, String>()

        fun collect(node: ParseNode) {
            if (node.tags.isNotEmpty()) {
                node.tags.forEach { tag ->
                    if (!result.containsKey(tag)) {
                        result[tag] = node.text()
                    }
                }
            }

            if (node is ParseNode.Group) {
                node.children.forEach { collect(it) }
            }
        }

        collect(matchResult.node)
        return ParsingResult(result)
    }

    private fun match(type: BnfType, ctx: ParseContext): Match? {
        val m = when (type) {
            is BnfType.Literal -> {
                if (ctx.remaining().startsWith(type.value)) {
                    Match(ParseNode.Leaf(type.value), ctx.advance(type.value.length))
                } else null
            }

            is BnfType.RegexMatch -> {
                val regex = Regex("^${type.pattern}")
                val result = regex.find(ctx.remaining())
                if (result != null) {
                    Match(ParseNode.Leaf(result.value), ctx.advance(result.value.length))
                } else null
            }

            is BnfType.Sequence -> {
                val nodes = mutableListOf<ParseNode>()
                var currentCtx = ctx
                for (part in type.parts) {
                    val m = match(part, currentCtx) ?: return null
                    nodes.add(m.node)
                    currentCtx = m.nextContext
                }
                Match(ParseNode.Group(nodes), currentCtx)
            }

            is BnfType.Choice -> {
                type.options.asSequence()
                    .mapNotNull { match(it, ctx) }
                    .firstOrNull()
            }

            is BnfType.Reference -> {
                val target = rules[type.name] ?: error("Rule ${type.name} not found")

                val m = match(target.type, ctx)

                if (m != null) {
                    m.node.addTags(type.tags)

                    m.node.tags.add(target.symbol)

                    m
                } else null
            }
        }

        if (m != null && type.tags.isNotEmpty()) {
            m.node.addTags(type.tags)
        }

        return m
    }
}
