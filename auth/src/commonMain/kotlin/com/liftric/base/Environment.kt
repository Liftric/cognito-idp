package com.liftric.base

expect class Environment() {
    companion object {
        /**
         * Accesses environment variable
         * @return Variable as string
         */
        fun variable(value: String): String?
    }
}