package com.liftric.base

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

fun <T> serialize(strategy: SerializationStrategy<T>, value: T): String {
    return Json {
        allowStructuredMapKeys = true
    }.encodeToString(strategy, value)
}

fun <T> parse(strategy: DeserializationStrategy<T>, value: String): T {
    return Json {
        allowStructuredMapKeys = true
    }.decodeFromString(strategy, value)
}
