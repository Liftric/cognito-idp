package com.liftric.cognito.idp.core

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

internal actual val Engine: HttpClientEngine = Darwin.create()

