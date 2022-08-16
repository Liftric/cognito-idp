import com.liftric.cognito.idp.core.*

/**
 * Adapted [Response.kt] classes for Typescript usage (Map and List aren't compatible for [kotlin.js.JsExport])
 */

@JsExport
data class SignInResponseJS(
    val AuthenticationResult: AuthenticationResult?,
    val ChallengeParameters: Array<MapEntry>,
    val ChallengeName: String?,
    val Session: String
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
    val MFAOptions: MFAOptions?,
    val PreferredMfaSetting: String?,
    val UserAttributes : Array<UserAttribute>,
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
    val CodeDeliveryDetailsList: Array<CodeDeliveryDetails> = arrayOf()
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
