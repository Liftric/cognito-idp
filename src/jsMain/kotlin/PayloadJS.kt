@file:JsExport

data class UserAttributeJS(
    val Name: String,
    val Value: String
)
data class SignUpResponseJS(
    val CodeDeliveryDetails: CodeDeliveryDetailsJS? = null,
    val UserConfirmed: Boolean = false,
    val UserSub: String
)