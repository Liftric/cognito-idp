package com.liftric.base

expect class Environment() {
    companion object {
        fun variable(value: String): String?
    }
}