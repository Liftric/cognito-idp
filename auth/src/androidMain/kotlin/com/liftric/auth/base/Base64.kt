package com.liftric.auth.base

import android.util.Base64

internal actual class Base64 {
    actual companion object {
        actual fun decode(string: String): String? {
            return String(Base64.decode(string, Base64.URL_SAFE),  Charsets.UTF_8)
        }
    }
}
