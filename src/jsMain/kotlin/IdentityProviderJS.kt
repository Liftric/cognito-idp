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

    fun signUp(
        username: String,
        password: String,
        attributes: Array<UserAttributeJS>? = null,
        clientMetadata: Array<MapEntry>? = null,
    ): Promise<SignUpResponseJS> =
        MainScope().promise {
            provider.signUp(
                username = username,
                password = password,
                attributes = attributes?.map(UserAttributeJS::toUserAttribute)?.toList(),
                clientMetadata = clientMetadata?.associate { it.key to it.value }
            ).getOrWrapThrowable().toSignUpResponseJS()
        }

    fun confirmSignUp(username: String, confirmationCode: String): Promise<Unit> =
        MainScope().promise {
            provider.confirmSignUp(
                username = username,
                confirmationCode = confirmationCode
            ).getOrWrapThrowable()
        }

    fun resendConfirmationCode(username: String): Promise<ResendConfirmationCodeResponseJS> =
        MainScope().promise {
            provider.resendConfirmationCode(username)
                .getOrWrapThrowable().toResendConfirmationCodeResponseJS()
        }

    fun signIn(username: String, password: String): Promise<SignInResponseJS> =
        MainScope().promise {
            provider.signIn(username, password)
                .getOrWrapThrowable().let {
                    SignInResponseJS(
                        AuthenticationResult = it.AuthenticationResult?.toAuthenticationResultJS(),
                        ChallengeParameters = it.ChallengeParameters.toMapEntries(),
                        ChallengeName = it.ChallengeName,
                        Session = it.Session,
                    )
                }
        }

    fun refresh(refreshToken: String): Promise<SignInResponseJS> =
        MainScope().promise {
            provider.refresh(refreshToken)
                .getOrWrapThrowable().let {
                    SignInResponseJS(
                        AuthenticationResult = it.AuthenticationResult?.toAuthenticationResultJS(),
                        ChallengeParameters = it.ChallengeParameters.toMapEntries(),
                        ChallengeName = it.ChallengeName,
                        Session = it.Session
                    )
                }
        }

    fun getUser(accessToken: String): Promise<GetUserResponseJS> =
        MainScope().promise {
            provider.getUser(accessToken)
                .getOrWrapThrowable().let {
                    GetUserResponseJS(
                        MFAOptions = it.MFAOptions?.toMFAOptionsJS(),
                        PreferredMfaSetting = it.PreferredMfaSetting,
                        UserAttributes = it.UserAttributes.map(UserAttribute::toUserAttributeJS).toTypedArray(),
                        UserMFASettingList = it.UserMFASettingList.toTypedArray(),
                        Username = it.Username
                    )
                }
        }

    fun updateUserAttributes(
        accessToken: String,
        attributes: Array<UserAttributeJS>
    ): Promise<UpdateUserAttributesResponseJS> =
        MainScope().promise {
            provider.updateUserAttributes(
                accessToken = accessToken,
                attributes = attributes.map(UserAttributeJS::toUserAttribute).toList()
            ).getOrWrapThrowable().let {
                UpdateUserAttributesResponseJS(it.CodeDeliveryDetailsList.map(CodeDeliveryDetails::toCodeDeliveryDetailsJS).toTypedArray())
            }
        }

    fun changePassword(
        accessToken: String,
        currentPassword: String,
        newPassword: String
    ): Promise<Unit> =
        MainScope().promise {
            provider.changePassword(
                accessToken = accessToken,
                currentPassword = currentPassword,
                newPassword = newPassword
            ).getOrWrapThrowable()
        }

    fun forgotPassword(username: String, clientMetadata: Array<MapEntry>? = null): Promise<ForgotPasswordResponseJS> =
        MainScope().promise {
            provider.forgotPassword(username, clientMetadata?.associate { it.key to it.value })
                .getOrWrapThrowable().toForgotPasswordResponseJS()
        }

    fun confirmForgotPassword(
        confirmationCode: String,
        username: String,
        password: String
    ): Promise<Unit> =
        MainScope().promise {
            provider.confirmForgotPassword(
                confirmationCode = confirmationCode,
                username = username,
                password = password
            ).getOrWrapThrowable()
        }

    fun getUserAttributeVerificationCode(
        accessToken: String,
        attributeName: String,
        clientMetadata: Array<MapEntry>? = null
    ): Promise<GetAttributeVerificationCodeResponseJS> =
        MainScope().promise {
            provider.getUserAttributeVerificationCode(
                accessToken = accessToken,
                attributeName = attributeName,
                clientMetadata = clientMetadata?.associate { it.key to it.value }
            ).getOrWrapThrowable().toGetAttributeVerificationCodeResponseJS()
        }

    fun verifyUserAttribute(
        accessToken: String,
        attributeName: String,
        code: String
    ): Promise<Unit> =
        MainScope().promise {
            provider.verifyUserAttribute(
                accessToken = accessToken,
                attributeName = attributeName,
                code = code
            ).getOrWrapThrowable()
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
        smsMfaSettings: MfaSettingsJS?,
        softwareTokenMfaSettings: MfaSettingsJS?
    ): Promise<Unit> = MainScope().promise {
        provider.setUserMFAPreference(
            accessToken = accessToken,
            smsMfaSettings = smsMfaSettings?.toMfaSettings(),
            softwareTokenMfaSettings = softwareTokenMfaSettings?.toMfaSettings()
        ).getOrWrapThrowable()
    }

    fun respondToAuthChallenge(
        challengeName: String,
        challengeResponses: Array<MapEntry>,
        session: String
    ): Promise<SignInResponseJS> = MainScope().promise {
        provider.respondToAuthChallenge(
            challengeName,
            challengeResponses.associate { it.key to it.value },
            session
        ).getOrWrapThrowable().let {
            SignInResponseJS(
                AuthenticationResult = it.AuthenticationResult?.toAuthenticationResultJS(),
                ChallengeParameters = it.ChallengeParameters.toMapEntries(),
                ChallengeName = it.ChallengeName,
                Session = it.Session
            )
        }
    }

    fun associateSoftwareToken(
        accessToken: String
    ): Promise<AssociateSoftwareTokenResponseJS> = MainScope().promise {
        provider.associateSoftwareToken(
            accessToken = accessToken
        ).getOrWrapThrowable().toAssociateSoftwareTokenResponseJS()
    }

    fun associateSoftwareTokenBySession(
        session: String
    ): Promise<AssociateSoftwareTokenResponseJS> = MainScope().promise {
        provider.associateSoftwareTokenBySession(
            session = session
        ).getOrWrapThrowable().toAssociateSoftwareTokenResponseJS()
    }

    fun verifySoftwareToken(
        accessToken: String,
        friendlyDeviceName: String?,
        userCode: String
    ): Promise<VerifySoftwareTokenResponseJS> = MainScope().promise {
        provider.verifySoftwareToken(
            accessToken = accessToken,
            friendlyDeviceName = friendlyDeviceName,
            userCode = userCode
        ).getOrWrapThrowable().toVerifySoftwareTokenResponseJS()
    }

    fun verifySoftwareTokenBySession(
        session: String,
        friendlyDeviceName: String?,
        userCode: String
    ): Promise<VerifySoftwareTokenResponseJS> = MainScope().promise {
        provider.verifySoftwareTokenBySession(
            friendlyDeviceName = friendlyDeviceName,
            session = session,
            userCode = userCode
        ).getOrWrapThrowable().toVerifySoftwareTokenResponseJS()
    }

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
}
