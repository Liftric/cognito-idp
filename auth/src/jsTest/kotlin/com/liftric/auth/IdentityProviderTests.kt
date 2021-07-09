package com.liftric.auth

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise

actual class IdentityProviderTests : AbstractIdentityProviderTests()

actual fun runTest(block: suspend () -> Unit): dynamic = MainScope().promise {
    block.invoke()
}
