package com.liftric

import androidx.test.core.app.ApplicationProvider
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
actual class SettingsStoreTest: AbstractSettingsStoreTest(SettingsStore(ApplicationProvider.getApplicationContext()))
