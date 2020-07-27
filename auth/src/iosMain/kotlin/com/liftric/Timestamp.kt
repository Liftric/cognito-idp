package com.liftric

import platform.QuartzCore.CACurrentMediaTime

actual object Timestamp {
    actual fun now(): Double {
        return CACurrentMediaTime()
    }
}