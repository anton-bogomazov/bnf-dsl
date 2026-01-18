package com.abogomazov

@DslMarker
annotation class BnfDsl

data class Rule(
    val symbol: Enum<*>,
    val type: BnfType,
)

@BnfDsl
class GrammarBuilder {
    private val rules = mutableListOf<Rule>()

    fun symbol(name: Enum<*>, block: RuleBuilder.() -> BnfType) {
        val builder = RuleBuilder()

        val type = builder.block().apply { tags = mutableListOf(name) }
        val rule = Rule(name, type)

        rules.add(rule)
    }

    fun build() = Grammar(rules)
}

class RuleBuilder {
    fun seq(vararg parts: Any) =
        BnfType.Sequence(parts.map { toBnf(it) })

    fun oneOf(vararg options: Any) =
        BnfType.Choice(options.map { toBnf(it) })

    val String.l get() = BnfType.Literal(this)
    val String.r get() = BnfType.RegexMatch(this)
    val Enum<*>.nt get() = BnfType.Reference(this)

    private fun toBnf(item: Any): BnfType = when(item) {
        is BnfType -> item
        is String -> item.l
        is Enum<*> -> item.nt
        else -> error("Unsupported type")
    }
}

fun bnf(block: GrammarBuilder.() -> Unit): Grammar {
    return GrammarBuilder().apply(block).build()
}