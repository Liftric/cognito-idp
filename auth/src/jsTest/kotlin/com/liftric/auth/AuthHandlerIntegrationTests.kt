package com.liftric.auth

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise

actual class AuthHandlerIntegrationTests : AbstractAuthHandlerIntegrationTests()

actual fun runTest(block: suspend () -> Unit): dynamic = MainScope().promise {
    block.invoke()
}
