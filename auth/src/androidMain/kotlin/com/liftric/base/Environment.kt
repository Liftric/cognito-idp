package com.liftric.base

actual class Environment {
    actual companion object {
        actual fun variable(value: String): String? {
            return System.getenv(value)
        }
    }
}