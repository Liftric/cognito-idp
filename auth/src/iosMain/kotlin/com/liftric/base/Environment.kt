package com.liftric.base

import platform.Foundation.NSProcessInfo

actual class Environment actual constructor() {
    actual companion object {
        actual fun variable(value: String): String? {
            return NSProcessInfo().environment[value].toString()
        }
    }
}