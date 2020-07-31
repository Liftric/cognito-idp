package com.liftric.base

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

fun <T> serialize(strategy: SerializationStrategy<T>, value: T): String {
    return Json(JsonConfiguration.Stable).stringify(strategy, value)
}

fun <T> parse(strategy: DeserializationStrategy<T>, value: String): T {
    return Json(JsonConfiguration.Stable).parse(strategy, value)
}