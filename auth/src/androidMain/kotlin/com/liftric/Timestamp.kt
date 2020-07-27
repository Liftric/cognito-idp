package com.liftric

actual object Timestamp {
    actual fun now(): Double {
        return (System.currentTimeMillis()).toDouble() / 1000
    }
}