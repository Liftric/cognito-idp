package com.liftric

import kotlinx.coroutines.*

internal actual val ApplicationDispatcher: CoroutineDispatcher = Dispatchers.Main

actual fun runBlocking(block: suspend () -> Unit) = kotlinx.coroutines.runBlocking { block() }