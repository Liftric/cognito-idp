package com.liftric.cognito.idp.core

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.DarwinLegacy

internal actual val Engine: HttpClientEngine = DarwinLegacy.create()

