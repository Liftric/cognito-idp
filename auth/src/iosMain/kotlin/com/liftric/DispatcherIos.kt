package com.liftric

import kotlin.coroutines.*
import kotlinx.coroutines.*
import platform.Foundation.NSRunLoop
import platform.darwin.*

internal actual val ApplicationDispatcher: CoroutineDispatcher =
        NsQueueDispatcher(dispatch_get_main_queue())

internal class NsQueueDispatcher(
    private val dispatchQueue: dispatch_queue_t
) : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatch_async(dispatchQueue) {
            block.run()
        }
    }
}