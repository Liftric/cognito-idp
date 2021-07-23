package com.liftric.cognito.idp

import kotlinx.coroutines.runBlocking

actual class IdentityProviderClientTests: AbstractIdentityProviderClientTests()

actual fun runTest(block: suspend () -> Unit) = runBlocking {
    block.invoke()
}