package com.liftric.cognito.idp.jwt

import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create

internal actual class Base64 {
    actual companion object {
        actual fun decode(input: String): String? {
            var encoded64 = input
            val remainder = encoded64.count() % 4
            if (remainder > 0) {
                encoded64 = encoded64.padEnd(input.count() + (4 - remainder), '=')
            }
            return NSData.create(encoded64, 0)?.let {
                @Suppress("USELESS_CAST") // false positive inspection: konan fails if the cast is removed
                (NSString.create(it, NSUTF8StringEncoding) as String?)
            }?: run {
                null
            }
        }
    }
}
