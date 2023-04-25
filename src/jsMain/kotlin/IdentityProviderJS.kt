@file:JsExport

import com.liftric.cognito.idp.IdentityProviderClient
import com.liftric.cognito.idp.core.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import kotlin.js.Promise

/**
 * Typescript compatible [IdentityProviderClient] implementation.
 */
class IdentityProviderClientJS(region: String, clientId: String) {
    private val provider: IdentityProviderClient = IdentityProviderClient(region, clientId)

    fun signUp(username: String, password: String, attributes: Array<UserAttributeJS>? = null): Promise<SignUpResponseJS> =
        MainScope().promise {
            provider.signUp(username, password, attributes?.toList()?.map { UserAttribute(it.Name, it.Value) })
                .getOrWrapThrowable().let {
                    SignUpResponseJS(it.CodeDeliveryDetails?.toJs(), it.UserConfirmed, it.UserSub)
                }
        }

    fun confirmSignUp(username: String, confirmationCode: String): Promise<Unit> =
        MainScope().promise {
            provider.confirmSignUp(username, confirmationCode)
                .getOrWrapThrowable()
        }

    fun resendConfirmationCode(username: String): Promise<ResendConfirmationCodeResponseJS> =
        MainScope().promise {
            provider.resendConfirmationCode(username)
                .getOrWrapThrowable().let { ResendConfirmationCodeResponseJS(it.CodeDeliveryDetails.toJs()) }
        }

    fun signIn(username: String, password: String): Promise<SignInResponseJS> =
        MainScope().promise {
            provider.signIn(username, password)
                .getOrWrapThrowable().let {
                    SignInResponseJS(
                        it.AuthenticationResult?.toJs(),
                        it.ChallengeParameters.map { MapEntry(it.key, it.value) }.toTypedArray()
                    )
                }
        }

    fun refresh(refreshToken: String): Promise<SignInResponseJS> =
        MainScope().promise {
            provider.refresh(refreshToken)
                .getOrWrapThrowable().let {
                    SignInResponseJS(
                        it.AuthenticationResult?.toJs(),
                        it.ChallengeParameters.map { MapEntry(it.key, it.value) }.toTypedArray()
                    )
                }
        }

    fun getUser(accessToken: String): Promise<GetUserResponseJS> =
        MainScope().promise {
            provider.getUser(accessToken)
                .getOrWrapThrowable().let {
                    GetUserResponseJS(
                        it.MFAOptions?.let { MFAOptionsJS(it.AttributeName, it.DeliveryMedium) },
                        it.PreferredMfaSetting,
                        it.UserAttributes.map { UserAttributeJS(it.Name,it.Value) }.toTypedArray(),
                        it.UserMFASettingList.toTypedArray(),
                        it.Username
                    )
                }
        }

    fun updateUserAttributes(
        accessToken: String,
        attributes: Array<UserAttributeJS>
    ): Promise<UpdateUserAttributesResponseJS> =
        MainScope().promise {
            provider.updateUserAttributes(accessToken, attributes.toList().map { UserAttribute(it.Name, it.Value) })
                .getOrWrapThrowable().let {
                    UpdateUserAttributesResponseJS(it.CodeDeliveryDetailsList.map { it.toJs() }.toTypedArray())
                }
        }

    fun changePassword(accessToken: String, currentPassword: String, newPassword: String): Promise<Unit> =
        MainScope().promise {
            provider.changePassword(accessToken, currentPassword, newPassword)
                .getOrWrapThrowable()
        }

    fun forgotPassword(username: String): Promise<ForgotPasswordResponseJS> =
        MainScope().promise {
            provider.forgotPassword(username)
                .getOrWrapThrowable().let { ForgotPasswordResponseJS(it.CodeDeliveryDetails.toJs()) }
        }

    fun confirmForgotPassword(confirmationCode: String, username: String, password: String): Promise<Unit> =
        MainScope().promise {
            provider.confirmForgotPassword(confirmationCode, username, password)
                .getOrWrapThrowable()
        }

    fun getUserAttributeVerificationCode(
        accessToken: String,
        attributeName: String,
        clientMetadata: Array<MapEntry>? = null
    ): Promise<GetAttributeVerificationCodeResponseJS> =
        MainScope().promise {
            provider.getUserAttributeVerificationCode(
                accessToken,
                attributeName,
                clientMetadata?.associate { it.key to it.value })
                .getOrWrapThrowable().let { GetAttributeVerificationCodeResponseJS(it.CodeDeliveryDetails.toJs()) }
        }

    fun verifyUserAttribute(accessToken: String, attributeName: String, code: String): Promise<Unit> =
        MainScope().promise {
            provider.verifyUserAttribute(accessToken, attributeName, code)
                .getOrWrapThrowable()
        }

    fun signOut(accessToken: String): Promise<Unit> =
        MainScope().promise {
            provider.signOut(accessToken)
                .getOrWrapThrowable()
        }

    fun revokeToken(refreshToken: String): Promise<Unit> =
        MainScope().promise {
            provider.revokeToken(refreshToken)
                .getOrWrapThrowable()
        }

    fun deleteUser(accessToken: String): Promise<Unit> =
        MainScope().promise {
            provider.deleteUser(accessToken)
                .getOrWrapThrowable()
        }

    fun setUserMFAPreference(
        accessToken: String,
        smsMfaSettings: MfaSettings?,
        softwareTokenMfaSettings: MfaSettings?
    ): Promise<Unit> = MainScope().promise {
        provider.setUserMFAPreference(
            accessToken,
            smsMfaSettings,
            softwareTokenMfaSettings
        ).getOrWrapThrowable()
    }

    fun respondToAuthChallenge(
        challengeName: String,
        challengeResponses: Map<String, String>,
        session: String
    ): Promise<SignInResponseJS> = MainScope().promise {
        provider.respondToAuthChallenge(
            challengeName,
            challengeResponses,
            session
        ).getOrWrapThrowable().let {
            SignInResponseJS(
                it.AuthenticationResult?.toJs(),
                it.ChallengeParameters.map { MapEntry(it.key, it.value) }.toTypedArray()
            )
        }
    }

    fun associateSoftwareToken(
        accessToken: String?,
        session: String?
    ): Promise<AssociateSoftwareTokenResponse> = MainScope().promise {
        provider.associateSoftwareToken(accessToken, session).getOrWrapThrowable()
    }

    fun verifySoftwareToken(
        accessToken: String?,
        friendlyDeviceName: String?,
        session: String?,
        userCode: String
    ): Promise<VerifySoftwareTokenResponse> = MainScope().promise {
        provider.verifySoftwareToken(
            accessToken,
            friendlyDeviceName,
            session,
            userCode
        ).getOrWrapThrowable()
    }
}

private fun AuthenticationResult.toJs(): AuthenticationResultJS =
    AuthenticationResultJS(
        AccessToken = AccessToken,
        ExpiresIn = ExpiresIn,
        IdToken = IdToken,
        RefreshToken = RefreshToken,
        TokenType = TokenType
    )

private fun CodeDeliveryDetails.toJs(): CodeDeliveryDetailsJS =
    CodeDeliveryDetailsJS(AttributeName = AttributeName, DeliveryMedium = DeliveryMedium, Destination = Destination)

private fun <T> Result<T>.getOrWrapThrowable(): T = when (value) {
    is Result.Failure -> {
        val wrapped: IdentityProviderExceptionJs = when(val t = value.exception) {
            is IdentityProviderException -> {
                when(t) {
                    is IdentityProviderException.CodeMismatch -> IdentityProviderExceptionJs.CodeMismatch(t.status.value, t.message)
                    is IdentityProviderException.ConcurrentModification -> IdentityProviderExceptionJs.ConcurrentModification(t.status.value, t.message)
                    is IdentityProviderException.EnableSoftwareTokenMFA -> IdentityProviderExceptionJs.EnableSoftwareTokenMFA(t.status.value, t.message)
                    is IdentityProviderException.ExpiredCode -> IdentityProviderExceptionJs.ExpiredCode(t.status.value, t.message)
                    is IdentityProviderException.InternalError -> IdentityProviderExceptionJs.InternalError(t.status.value, t.message)
                    is IdentityProviderException.InvalidLambdaResponse -> IdentityProviderExceptionJs.InvalidLambdaResponse(t.status.value, t.message)
                    is IdentityProviderException.InvalidParameter -> IdentityProviderExceptionJs.InvalidParameter(t.status.value, t.message)
                    is IdentityProviderException.InvalidPassword -> IdentityProviderExceptionJs.InvalidPassword(t.status.value, t.message)
                    is IdentityProviderException.InvalidUserPoolConfiguration -> IdentityProviderExceptionJs.InvalidUserPoolConfiguration(t.status.value, t.message)
                    is IdentityProviderException.LimitExceeded -> IdentityProviderExceptionJs.LimitExceeded(t.status.value, t.message)
                    is IdentityProviderException.NotAuthorized -> IdentityProviderExceptionJs.NotAuthorized(t.status.value, t.message)
                    is IdentityProviderException.PasswordResetRequired -> IdentityProviderExceptionJs.PasswordResetRequired(t.status.value, t.message)
                    is IdentityProviderException.ResourceNotFound -> IdentityProviderExceptionJs.ResourceNotFound(t.status.value, t.message)
                    is IdentityProviderException.SoftwareTokenMFANotFound -> IdentityProviderExceptionJs.SoftwareTokenMFANotFound(t.status.value, t.message)
                    is IdentityProviderException.TooManyFailedAttempts -> IdentityProviderExceptionJs.TooManyFailedAttempts(t.status.value, t.message)
                    is IdentityProviderException.TooManyRequests -> IdentityProviderExceptionJs.TooManyRequests(t.status.value, t.message)
                    is IdentityProviderException.UnexpectedLambda -> IdentityProviderExceptionJs.UnexpectedLambda(t.status.value, t.message)
                    is IdentityProviderException.Unknown -> IdentityProviderExceptionJs.Unknown(t.status.value, t.type, t.message)
                    is IdentityProviderException.UserLambdaValidation -> IdentityProviderExceptionJs.UserLambdaValidation(t.status.value, t.message)
                    is IdentityProviderException.UserNotConfirmed -> IdentityProviderExceptionJs.UserNotConfirmed(t.status.value, t.message)
                    is IdentityProviderException.UserNotFound -> IdentityProviderExceptionJs.UserNotFound(t.status.value, t.message)
                }
            }
            else -> IdentityProviderExceptionJs.NonCognitoException(t)
        }
        throw wrapped
    }
    else -> value as T
}
