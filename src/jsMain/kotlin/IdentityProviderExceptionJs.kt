@file:JsExport

sealed class IdentityProviderExceptionJs(open val status: Int, override val message: String?) : Exception(message) {
    class CodeMismatch(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class ConcurrentModification(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class EnableSoftwareTokenMFA(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class ExpiredCode(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class InternalError(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class InvalidLambdaResponse(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class InvalidParameter(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class InvalidPassword(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class InvalidUserPoolConfiguration(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class LimitExceeded(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class NotAuthorized(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class PasswordResetRequired(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class ResourceNotFound(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class SoftwareTokenMFANotFound(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class TooManyFailedAttempts(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class TooManyRequests(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class UnexpectedLambda(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class UserLambdaValidation(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class UserNotConfirmed(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class UserNotFound(override val status: Int, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class Unknown(override val status: Int, val type: String, override val message: String?) : IdentityProviderExceptionJs(status, message)
    class NonCognitoException(
        override val cause: Throwable?,
        override val message: String? = "unknown non-cognito exception occurred, check the cause"
    ) : IdentityProviderExceptionJs(-999, message)
}
