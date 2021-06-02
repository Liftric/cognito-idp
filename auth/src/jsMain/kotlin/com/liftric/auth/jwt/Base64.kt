package com.liftric.auth.jwt

internal actual class Base64 {
    actual companion object {
        actual fun decode(input: String): String? {
            val buffer = try {
                js("Buffer").from(input, "base64")
            } catch (e: Exception) {
                // fallback
                Buffer(input, "base64")
            } as Buffer
            val decoded = buffer.toString("utf-8")

            // reencoding to compare with given base64
            val reencoded = buffer.toString("base64")
                // removing padding!
                .replace(Regex("=+$"), "")

            // without padding this must be identical, otherwise input is invalid base64
            if (input != reencoded) return null
            return decoded
        }
    }
}

external class Buffer(input: String, encoding: String) {
    fun toString(s: String): String
}
