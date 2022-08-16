/**
 * Don't forget to check [ResponseJS.kt] when doing changes here :)
 */
package com.liftric.cognito.idp.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
data class RequestError(
    @SerialName("__type")
    val type: String,
    val message: String
)

@Serializable
data class SignInResponse(
    val AuthenticationResult: AuthenticationResult?,
    val ChallengeParameters: Map<String, String> = mapOf(),
    val ChallengeName: String?,
    val Session: String
)

@JsExport
@Serializable
data class AuthenticationResult(
    val AccessToken: String?,
    val ExpiresIn: Int?,
    val IdToken: String?,
    val RefreshToken: String?,
    val TokenType: String?,
    val NewDeviceMetadata: NewDeviceMetadata?,
)

@JsExport
@Serializable
data class NewDeviceMetadata(
    val DeviceGroupKey: String?,
    val DeviceKey: String?,
)

@JsExport
@Serializable
data class SignUpResponse(
    val CodeDeliveryDetails: CodeDeliveryDetails?,
    val UserConfirmed: Boolean,
    val UserSub: String
)

@JsExport
@Serializable
data class ResendConfirmationCodeResponse(
    val CodeDeliveryDetails: CodeDeliveryDetails
)

@JsExport
@Serializable
data class CodeDeliveryDetails(
    val AttributeName: String?,
    val DeliveryMedium: String?,
    val Destination: String?
)

@Serializable
data class GetUserResponse(
    val MFAOptions: MFAOptions?,
    val PreferredMfaSetting: String,
    val UserAttributes: List<UserAttribute> = listOf(),
    val UserMFASettingList: List<String> = listOf(),
    val Username: String
)

@JsExport
@Serializable
data class MFAOptions(
    val AttributeName: String?,
    val DeliveryMedium: String?
)

@Serializable
data class UpdateUserAttributesResponse(
    val CodeDeliveryDetailsList: List<CodeDeliveryDetails> = listOf()
)

@JsExport
@Serializable
data class GetAttributeVerificationCodeResponse(
    val CodeDeliveryDetails: CodeDeliveryDetails
)

@JsExport
@Serializable
data class ForgotPasswordResponse(
    val CodeDeliveryDetails: CodeDeliveryDetails
)

@JsExport
@Serializable
data class AssociateSoftwareTokenResponse(
    val SecretCode: String,
    val Session: String?
)

@JsExport
@Serializable
data class VerifySoftwareTokenResponse(
    val Session: String,
    val Status: String
)
