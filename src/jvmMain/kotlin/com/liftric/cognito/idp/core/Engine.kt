package com.liftric.cognito.idp.core

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.java.Java

internal actual val Engine: HttpClientEngine = Java.create()
