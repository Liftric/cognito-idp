package com.liftric

actual class AuthHandlerMockTest : AbstractAuthHandlerMockTest(SettingsStore(), SecretStore())