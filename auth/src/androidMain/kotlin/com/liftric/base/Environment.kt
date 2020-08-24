package com.liftric.base

actual internal class Environment {
    actual companion object {
        actual fun variable(value: String): String? {
            return System.getenv(value)
        }
    }
}