package com.liftric.auth.jwt

/**
 * Access Token containing claims specified by IETF:
 * https://tools.ietf.org/html/rfc7519#section-4
 */
interface AccessToken {
    val aud: String
    val exp: Long
    val iat: Long
    val iss: String
    val jti: String
    val nbf: Long
    val sub: String
}