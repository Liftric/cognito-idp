package com.liftric.auth.jwt

import android.util.Base64

internal actual class Base64 {
    actual companion object {
        actual fun decode(input: String): String? {
            return try {
                String(Base64.decode(input, Base64.URL_SAFE), Charsets.UTF_8)
            } catch (e: Exception) {
                null
            }
        }
    }
}
