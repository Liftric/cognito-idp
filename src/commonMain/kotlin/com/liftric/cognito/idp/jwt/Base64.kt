package com.liftric.cognito.idp.jwt

internal expect class Base64 {
    companion object {
        fun decode(input: String): String?
    }
}
