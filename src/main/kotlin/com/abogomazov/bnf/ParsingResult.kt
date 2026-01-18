package com.abogomazov.com.abogomazov.bnf

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

class MappingBuilder<T>(private val result: ParsingResult) {
    val mappings = mutableMapOf<String, Any?>()

    infix fun Enum<*>.to(property: kotlin.reflect.KProperty1<T, out Any?>) {
        val value = when (property.returnType.classifier) {
            Long::class -> result.long(this)
            else -> result.textOrNull(this)
        }
        mappings[property.name] = value
    }
}