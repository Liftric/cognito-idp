package com.liftric.auth.jwt

internal expect class Base64 {
    companion object {
        fun decode(input: String): String?
    }
}
