package com.liftric.auth.base

import platform.Foundation.NSProcessInfo

actual internal class Environment actual constructor() {
    actual companion object {
        actual fun variable(value: String): String? {
            return NSProcessInfo().environment[value].toString()
        }
    }
}
