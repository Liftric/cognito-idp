package com.liftric.cognito.idp.core

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android

internal actual val Engine: HttpClientEngine = Android.create()
