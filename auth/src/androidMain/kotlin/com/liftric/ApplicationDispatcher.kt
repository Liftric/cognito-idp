package com.liftric

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val ApplicationDispatcher: CoroutineDispatcher = Dispatchers.IO

actual fun runBlocking(block: suspend () -> Unit) = kotlinx.coroutines.runBlocking { block() }