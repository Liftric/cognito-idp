package com.liftric.cognito.idp

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise

actual class IdentityProviderClientTests : AbstractIdentityProviderClientTests()

actual fun runTest(block: suspend () -> Unit) {
    MainScope().promise {
        block.invoke()
    }
}
