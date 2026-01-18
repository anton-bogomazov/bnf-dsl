package com.abogomazov.com.abogomazov.bnf.parser.result

import kotlin.reflect.KProperty1

class MappingBuilder<T>(private val result: ParsingResult) {
    val mappings = mutableMapOf<String, Any?>()

    infix fun Enum<*>.to(property: KProperty1<T, out Any?>) {
        val value = when (property.returnType.classifier) {
            Long::class -> result.long(this)
            else -> result.textOrNull(this)
        }
        mappings[property.name] = value
    }
}