package com.liftric.auth.base

import host

actual internal class Environment actual constructor() {
    actual companion object {
        actual fun variable(value: String): String? {
            return TODO(JSON.stringify(host))
//            return TODO()
        }
    }
}
