package com.abogomazov.com.abogomazov.bnf.parser.result

import kotlin.collections.get
import kotlin.reflect.KClass

data class ParsingResult(
    private val groups: Map<Enum<*>, String>
) {
    fun text(symbol: Enum<*>) = textOrNull(symbol)!!
    fun textOrNull(symbol: Enum<*>) = groups[symbol]
    fun long(symbol: Enum<*>) = groups[symbol]!!.toLong()

    fun <T : Any> mapTo(targetClass: KClass<T>, block: MappingBuilder<T>.() -> Unit): T {
        val builder = MappingBuilder<T>(this)
        builder.block()

        val constructor = targetClass.constructors.first()

        val args = constructor.parameters.associateWith { param ->
            builder.mappings[param.name]
        }

        return constructor.callBy(args)
    }
}
