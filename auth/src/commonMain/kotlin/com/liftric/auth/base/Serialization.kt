package com.liftric.auth.base

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal inline fun <reified T> serialize(value: T): String {
    return Json {
        allowStructuredMapKeys = true
    }.encodeToString(value)
}

internal inline fun <reified T> parse(value: String): T {
    return Json {
        allowStructuredMapKeys = true
    }.decodeFromString(value)
}
