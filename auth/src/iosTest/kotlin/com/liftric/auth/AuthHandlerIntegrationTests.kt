package com.liftric.auth

import kotlinx.coroutines.runBlocking

actual class AuthHandlerIntegrationTests: AbstractAuthHandlerIntegrationTests()

actual fun runTest(block: suspend () -> Unit) = runBlocking {
    block.invoke()
}