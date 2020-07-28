package com.liftric.resources

expect class Environment() {
    companion object {
        fun variable(value: String): String?
    }
}