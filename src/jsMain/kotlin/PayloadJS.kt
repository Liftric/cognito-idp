import com.liftric.cognito.idp.core.MfaSettings
import com.liftric.cognito.idp.core.UserAttribute
import kotlinx.serialization.Serializable

@JsExport
@Serializable
data class UserAttributeJS(
    val Name: String, val Value: String
)

fun UserAttributeJS.toUserAttribute(): UserAttribute {
    return UserAttribute(Name, Value)
}

fun UserAttribute.toUserAttributeJS(): UserAttributeJS {
    return UserAttributeJS(Name, Value)
}

@JsExport
@Serializable
data class MfaSettingsJS(
    val Enabled: Boolean, val PreferredMfa: Boolean
)

fun MfaSettingsJS.toMfaSettings(): MfaSettings {
    return MfaSettings(Enabled, PreferredMfa)
}

fun MfaSettings.toMfaSettingsJS(): MfaSettingsJS {
    return MfaSettingsJS(Enabled, PreferredMfa)
}