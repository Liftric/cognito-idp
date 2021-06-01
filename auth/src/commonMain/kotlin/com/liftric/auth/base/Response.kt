@file:JsExport

package com.liftric.auth.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.js.JsExport

@Serializable
data class RequestError(
    @SerialName("__type")
    val type: String,
    val message: String
)

@Serializable
data class SignInResponse(
    val AuthenticationResult: AuthenticationResult,
    val ChallengeParameters: Map<String, String> = mapOf(),
    @Transient
    val ChallengeParametersJs: Array<MapEntry> = ChallengeParameters
        .map { MapEntry(it.key, it.value) }
        .toTypedArray()
)

@Serializable
data class MapEntry(val key: String, val value: String)

@Serializable
data class AuthenticationResult(
    val AccessToken: String = "",
    val ExpiresIn: Int = 0,
    val IdToken: String = "",
    val RefreshToken: String = "",
    val TokenType: String = ""
)

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

@Serializable
data class GetUserResponse(
    val MFAOptions: MFAOptions = MFAOptions(),
    val PreferredMfaSetting: String = "",
    val UserAttributes: Array<UserAttribute> = arrayOf(),
    val UserMFASettingList: Array<String> = arrayOf(),
    val Username: String
)

@Serializable
data class MFAOptions(
    val AttributeName: String = "",
    val DeliveryMedium: String = ""
)

@Serializable
data class UpdateUserAttributesResponse(
    val CodeDeliveryDetailsList: Array<CodeDeliveryDetails> = arrayOf()
)

@Serializable
data class GetAttributeVerificationCodeResponse(
    val CodeDeliveryDetails: CodeDeliveryDetails = CodeDeliveryDetails()
)

@Serializable
data class ForgotPasswordResponse(
    val CodeDeliveryDetails: CodeDeliveryDetails = CodeDeliveryDetails()
)
