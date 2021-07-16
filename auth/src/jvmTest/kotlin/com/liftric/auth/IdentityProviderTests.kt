package com.liftric.auth

import kotlinx.coroutines.runBlocking

actual class IdentityProviderTests: AbstractIdentityProviderTests()

actual fun runTest(block: suspend () -> Unit) = runBlocking {
    block.invoke()
}
