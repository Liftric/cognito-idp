package com.liftric

import kotlinx.coroutines.runBlocking

actual class AuthHandlerTest: AbstractAuthHandlerTest(SettingsStore(), SecretStore())

actual fun runTest(block: suspend () -> Unit) = runBlocking { block() }