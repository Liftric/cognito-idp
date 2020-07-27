package com.liftric.base

import kotlinx.serialization.Serializable

@Serializable
data class RequestError(val __type: String, val message: String)

@Serializable
data class AuthResponse(
    val AuthenticationResult: AuthenticationResult,
    val ChallengeParameters: Map<String, String> = mapOf()
)

@Serializable
data class AuthenticationResult(
    val AccessToken: String = "",
    val ExpiresIn: Int = 0,
    val IdToken: String = "",
    val RefreshToken: String = "",
    val TokenType: String = ""
)