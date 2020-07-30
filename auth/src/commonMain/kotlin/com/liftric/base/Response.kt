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

@Serializable
data class GetUserResult(
    val MFAOptions: MFAOptions = MFAOptions(),
    val PreferredMfaSetting: String = "",
    val UserAttributes : List<UserAttribute> = listOf(),
    val UserMFASettingList: List<String> = listOf(),
    val Username: String
)

@Serializable
data class MFAOptions(
    val AttributeName: String = "",
    val DeliveryMedium: String = ""
)