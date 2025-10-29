import com.liftric.cognito.idp.core.*
import kotlinx.serialization.Serializable

/**
 * Adapted [Response.kt] classes for Typescript usage (Map and List aren't compatible for [kotlin.js.JsExport])
 */

@JsExport
data class SignInResponseJS(
    val AuthenticationResult: AuthenticationResultJS?,
    val ChallengeParameters: Array<MapEntry>,
    val ChallengeName: String?,
    val Session: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as SignInResponseJS

        if (AuthenticationResult != other.AuthenticationResult) return false
        if (!ChallengeParameters.contentEquals(other.ChallengeParameters)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = AuthenticationResult.hashCode()
        result = 31 * result + ChallengeParameters.contentHashCode()
        return result
    }
}

@JsExport
data class GetUserResponseJS(
    val MFAOptions: MFAOptionsJS?,
    val PreferredMfaSetting: String?,
    val UserAttributes: Array<UserAttributeJS>,
    val UserMFASettingList: Array<String>,
    val Username: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as GetUserResponseJS

        if (MFAOptions != other.MFAOptions) return false
        if (PreferredMfaSetting != other.PreferredMfaSetting) return false
        if (!UserAttributes.contentEquals(other.UserAttributes)) return false
        if (!UserMFASettingList.contentEquals(other.UserMFASettingList)) return false
        if (Username != other.Username) return false

        return true
    }

    override fun hashCode(): Int {
        var result = MFAOptions.hashCode()
        result = 31 * result + PreferredMfaSetting.hashCode()
        result = 31 * result + UserAttributes.contentHashCode()
        result = 31 * result + UserMFASettingList.contentHashCode()
        result = 31 * result + Username.hashCode()
        return result
    }
}

@JsExport
data class UpdateUserAttributesResponseJS(
    val CodeDeliveryDetailsList: Array<CodeDeliveryDetailsJS> = arrayOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as UpdateUserAttributesResponseJS

        if (!CodeDeliveryDetailsList.contentEquals(other.CodeDeliveryDetailsList)) return false

        return true
    }

    override fun hashCode(): Int {
        return CodeDeliveryDetailsList.contentHashCode()
    }
}

@JsExport
data class MapEntry(val key: String, val value: String)

internal fun Map<String, String>.toMapEntries(): Array<MapEntry> = entries.map {
    MapEntry(it.key, it.value)
}.toTypedArray()

@JsExport
@Serializable
data class NewDeviceMetadataJS(
    val DeviceGroupKey: String?,
    val DeviceKey: String?,
)

fun NewDeviceMetadata.toNewDeviceMetadataJS(): NewDeviceMetadataJS {
    return NewDeviceMetadataJS(DeviceGroupKey, DeviceKey)
}

@JsExport
@Serializable
data class AuthenticationResultJS(
    val AccessToken: String?,
    val ExpiresIn: Int?,
    val IdToken: String?,
    val RefreshToken: String?,
    val TokenType: String?,
    val NewDeviceMetadata: NewDeviceMetadataJS?,
)

fun AuthenticationResult.toAuthenticationResultJS(): AuthenticationResultJS {
    return AuthenticationResultJS(
        AccessToken,
        ExpiresIn,
        IdToken,
        RefreshToken,
        TokenType,
        NewDeviceMetadata?.toNewDeviceMetadataJS()
    )
}

@JsExport
@Serializable
data class SignUpResponseJS(
    val CodeDeliveryDetails: CodeDeliveryDetailsJS?,
    val UserConfirmed: Boolean,
    val UserSub: String
)

fun SignUpResponse.toSignUpResponseJS(): SignUpResponseJS {
    return SignUpResponseJS(CodeDeliveryDetails?.toCodeDeliveryDetailsJS(), UserConfirmed, UserSub)
}

@JsExport
@Serializable
data class ResendConfirmationCodeResponseJS(
    val CodeDeliveryDetails: CodeDeliveryDetailsJS
)

fun ResendConfirmationCodeResponse.toResendConfirmationCodeResponseJS(): ResendConfirmationCodeResponseJS {
    return ResendConfirmationCodeResponseJS(CodeDeliveryDetails.toCodeDeliveryDetailsJS())
}

@JsExport
@Serializable
data class CodeDeliveryDetailsJS(
    val AttributeName: String?,
    val DeliveryMedium: String?,
    val Destination: String?
)

fun CodeDeliveryDetails.toCodeDeliveryDetailsJS(): CodeDeliveryDetailsJS {
    return CodeDeliveryDetailsJS(AttributeName, DeliveryMedium, Destination)
}

@JsExport
@Serializable
data class MFAOptionsJS(
    val AttributeName: String?,
    val DeliveryMedium: String?
)

fun MFAOptions.toMFAOptionsJS(): MFAOptionsJS {
    return MFAOptionsJS(AttributeName, DeliveryMedium)
}

@JsExport
@Serializable
data class GetAttributeVerificationCodeResponseJS(
    val CodeDeliveryDetails: CodeDeliveryDetailsJS
)

fun GetAttributeVerificationCodeResponse.toGetAttributeVerificationCodeResponseJS(): GetAttributeVerificationCodeResponseJS {
    return GetAttributeVerificationCodeResponseJS(CodeDeliveryDetails.toCodeDeliveryDetailsJS())
}

@JsExport
@Serializable
data class ForgotPasswordResponseJS(
    val CodeDeliveryDetails: CodeDeliveryDetailsJS
)

fun ForgotPasswordResponse.toForgotPasswordResponseJS(): ForgotPasswordResponseJS {
    return ForgotPasswordResponseJS(CodeDeliveryDetails.toCodeDeliveryDetailsJS())
}

@JsExport
@Serializable
data class AssociateSoftwareTokenResponseJS(
    val SecretCode: String,
    val Session: String?
)

fun AssociateSoftwareTokenResponse.toAssociateSoftwareTokenResponseJS(): AssociateSoftwareTokenResponseJS {
    return AssociateSoftwareTokenResponseJS(SecretCode, Session)
}

@JsExport
@Serializable
data class VerifySoftwareTokenResponseJS(
    val Session: String?,
    val Status: String
)

fun VerifySoftwareTokenResponse.toVerifySoftwareTokenResponseJS(): VerifySoftwareTokenResponseJS {
    return VerifySoftwareTokenResponseJS(Session, Status)
}