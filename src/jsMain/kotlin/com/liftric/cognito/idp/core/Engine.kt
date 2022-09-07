package com.liftric.cognito.idp.core

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js

internal actual val Engine: HttpClientEngine = Js.create()

