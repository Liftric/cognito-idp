package com.liftric

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
actual class AuthHandlerTest : AbstractAuthHandlerTest(
    SettingsStore(ApplicationProvider.getApplicationContext()),
    SecretStore(ApplicationProvider.getApplicationContext())
)

actual fun runTest(block: suspend () -> Unit) = runBlocking { block() }