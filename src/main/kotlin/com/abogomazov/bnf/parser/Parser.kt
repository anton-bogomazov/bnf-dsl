package com.abogomazov.com.abogomazov.bnf.parser

import com.abogomazov.com.abogomazov.bnf.grammar.Rule
import com.abogomazov.com.abogomazov.bnf.grammar.BnfType
import com.abogomazov.com.abogomazov.bnf.parser.result.ParsingResult

class Parser(rules: List<Rule>) {
    private val rules = rules.associateBy { it.symbol }.toMutableMap()

    fun parse(ruleName: Enum<*>, input: String): ParsingResult {
        val startRule = rules[ruleName]
            ?: throw IllegalArgumentException("Rule $ruleName not found in grammar")

        val context = ParseContext(input)
        val matchResult = match(startRule.type, context)
            ?: throw IllegalStateException("Syntax error: could not match $ruleName at position ${context.position}")

        val tagsMap = collectTags(matchResult.node)
        return ParsingResult(tagsMap)
    }

    private fun match(type: BnfType, ctx: ParseContext) =
        when (type) {
            is BnfType.Literal -> matchLiteral(type, ctx)
            is BnfType.RegexMatch -> matchRegex(type, ctx)
            is BnfType.Sequence -> matchSequence(type, ctx)
            is BnfType.Choice -> matchChoice(type, ctx)
            is BnfType.Reference -> matchReference(type, ctx)
        }?.also { it.node.addTags(type.tags) }

    private fun matchLiteral(type: BnfType.Literal, ctx: ParseContext) =
        if (ctx.remaining().startsWith(type.value)) {
            Match(
                node = ParseNode.Leaf(type.value),
                nextContext = ctx.advance(type.value.length),
            )
        } else null

    private fun matchRegex(type: BnfType.RegexMatch, ctx: ParseContext): Match? {
        val res = "^${type.pattern}".toRegex().find(ctx.remaining())
        return if (res != null) {
            Match(
                node = ParseNode.Leaf(res.value),
                nextContext = ctx.advance(res.value.length),
            )
        } else null
    }

    private fun matchSequence(type: BnfType.Sequence, ctx: ParseContext): Match? {
        val nodes = mutableListOf<ParseNode>()
        var currentCtx = ctx
        for (part in type.parts) {
            val m = match(part, currentCtx) ?: return null
            nodes.add(m.node)
            currentCtx = m.nextContext
        }
        return Match(ParseNode.Group(nodes), currentCtx)
    }

    private fun matchChoice(type: BnfType.Choice, ctx: ParseContext): Match? =
        type.options.firstNotNullOfOrNull { match(it, ctx) }

    private fun matchReference(type: BnfType.Reference, ctx: ParseContext): Match? {
        val target = rules[type.name] ?: error("Rule ${type.name} not found")
        return match(target.type, ctx)?.also {
            it.node.tags.add(target.symbol)
        }
    }

    private fun collectTags(root: ParseNode): Map<Enum<*>, String> {
        val result = mutableMapOf<Enum<*>, String>()

        fun walk(node: ParseNode) {
            if (node is ParseNode.Group) {
                node.children.forEach { walk(it) }
            }

            node.tags.forEach { tag ->
                result[tag] = node.text()
            }
        }

        walk(root)
        return result
    }
}
