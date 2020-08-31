package com.liftric.auth.base

import kotlinx.serialization.Serializable

// Error

@Serializable
data class RequestError(val __type: String, val message: String)

// Sign in

@Serializable
data class SignInResponse(
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

// Sign up

@Serializable
data class SignUpResponse(
    val CodeDeliveryDetails: CodeDeliveryDetails = CodeDeliveryDetails(),
    val UserConfirmed: Boolean = false,
    val UserSub: String = ""
)

@Serializable
data class CodeDeliveryDetails(
    val AttributeName: String = "",
    val DeliveryMedium: String = "",
    val Destination: String = ""
)

// Get user

@Serializable
data class GetUserResponse(
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

// Update attribute

@Serializable
data class UpdateUserAttributesResponse(
    val CodeDeliveryDetailsList: List<CodeDeliveryDetails> = listOf()
)
