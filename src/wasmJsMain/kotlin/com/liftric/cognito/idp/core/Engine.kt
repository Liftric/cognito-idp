package com.liftric.cognito.idp.core

import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*

actual val Engine: HttpClientEngine
	get() = CIO.create()