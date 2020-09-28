package com.liftric.auth.jwt

import android.util.Base64
import java.io.UnsupportedEncodingException

internal actual class Base64 {
    actual companion object {
        actual fun decode(string: String): String? {
            return try {
                String(Base64.decode(string, Base64.URL_SAFE), Charsets.UTF_8)
            } catch (e: Exception) {
                null
            }
        }
    }
}