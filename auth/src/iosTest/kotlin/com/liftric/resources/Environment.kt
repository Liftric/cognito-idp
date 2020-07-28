package com.liftric.resources

import platform.posix.*
import kotlinx.cinterop.*

actual class Environment {
    actual companion object {
        actual fun variable(value: String): String? {
            return getenv(value)?.toKString()
        }
    }
}