package com.liftric.auth.base

internal expect class Base64 {
    companion object {
        fun decode(string: String): String?
    }
}