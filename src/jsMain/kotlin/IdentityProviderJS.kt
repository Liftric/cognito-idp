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
        attributes: Array<UserAttribute>? = null
    ): Promise<SignUpResponse> =
        MainScope().promise {
            provider.signUp(
                username = username,
                password = password,
                attributes = attributes?.toList()
            ).getOrThrow()
        }

    fun confirmSignUp(username: String, confirmationCode: String): Promise<Unit> =
        MainScope().promise {
            provider.confirmSignUp(
                username = username,
                confirmationCode = confirmationCode
            ).getOrThrow()
        }

    fun resendConfirmationCode(username: String): Promise<ResendConfirmationCodeResponse> =
        MainScope().promise {
            provider.resendConfirmationCode(username)
                .getOrThrow()
        }

    fun signIn(username: String, password: String): Promise<SignInResponseJS> =
        MainScope().promise {
            provider.signIn(username, password)
                .getOrThrow().let {
                    SignInResponseJS(
                        AuthenticationResult = it.AuthenticationResult,
                        ChallengeParameters = it.ChallengeParameters.toMapEntries(),
                        ChallengeName = it.ChallengeName,
                        Session = it.Session,
                    )
                }
        }

    fun refresh(refreshToken: String): Promise<SignInResponseJS> =
        MainScope().promise {
            provider.refresh(refreshToken)
                .getOrThrow().let {
                    SignInResponseJS(
                        AuthenticationResult = it.AuthenticationResult,
                        ChallengeParameters = it.ChallengeParameters.toMapEntries(),
                        ChallengeName = it.ChallengeName,
                        Session = it.Session
                    )
                }
        }

    fun getUser(accessToken: String): Promise<GetUserResponseJS> =
        MainScope().promise {
            provider.getUser(accessToken)
                .getOrThrow().let {
                    GetUserResponseJS(
                        MFAOptions = it.MFAOptions,
                        PreferredMfaSetting = it.PreferredMfaSetting,
                        UserAttributes = it.UserAttributes.toTypedArray(),
                        UserMFASettingList = it.UserMFASettingList.toTypedArray(),
                        Username = it.Username
                    )
                }
        }

    fun updateUserAttributes(
        accessToken: String,
        attributes: Array<UserAttribute>
    ): Promise<UpdateUserAttributesResponseJS> =
        MainScope().promise {
            provider.updateUserAttributes(
                accessToken = accessToken,
                attributes = attributes.toList()
            ).getOrThrow().let {
                UpdateUserAttributesResponseJS(it.CodeDeliveryDetailsList.toTypedArray())
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
            ).getOrThrow()
        }

    fun forgotPassword(username: String): Promise<ForgotPasswordResponse> =
        MainScope().promise {
            provider.forgotPassword(username)
                .getOrThrow()
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
            ).getOrThrow()
        }

    fun getUserAttributeVerificationCode(
        accessToken: String,
        attributeName: String,
        clientMetadata: Array<MapEntry>? = null
    ): Promise<GetAttributeVerificationCodeResponse> =
        MainScope().promise {
            provider.getUserAttributeVerificationCode(
                accessToken = accessToken,
                attributeName = attributeName,
                clientMetadata = clientMetadata?.associate { it.key to it.value }
            ).getOrThrow()
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
            ).getOrThrow()
        }

    fun signOut(accessToken: String): Promise<Unit> =
        MainScope().promise {
            provider.signOut(accessToken)
                .getOrThrow()
        }

    fun revokeToken(refreshToken: String): Promise<Unit> =
        MainScope().promise {
            provider.revokeToken(refreshToken)
                .getOrThrow()
        }

    fun deleteUser(accessToken: String): Promise<Unit> =
        MainScope().promise {
            provider.deleteUser(accessToken)
                .getOrThrow()
        }

    fun setUserMFAPreference(
        accessToken: String,
        smsMfaSettings: MfaSettings?,
        softwareTokenMfaSettings: MfaSettings?
    ): Promise<Unit> = MainScope().promise {
        provider.setUserMFAPreference(
            accessToken = accessToken,
            smsMfaSettings = smsMfaSettings,
            softwareTokenMfaSettings = softwareTokenMfaSettings
        ).getOrThrow()
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
        ).getOrThrow().let {
            SignInResponseJS(
                AuthenticationResult = it.AuthenticationResult,
                ChallengeParameters = it.ChallengeParameters.toMapEntries(),
                ChallengeName = it.ChallengeName,
                Session = it.Session
            )
        }
    }

    fun associateSoftwareToken(
        accessToken: String
    ): Promise<AssociateSoftwareTokenResponse> = MainScope().promise {
        provider.associateSoftwareToken(
            accessToken = accessToken
        ).getOrThrow()
    }

    fun associateSoftwareTokenBySession(
        session: String
    ): Promise<AssociateSoftwareTokenResponse> = MainScope().promise {
        provider.associateSoftwareTokenBySession(
            session = session
        ).getOrThrow()
    }

    fun verifySoftwareToken(
        accessToken: String,
        friendlyDeviceName: String?,
        userCode: String
    ): Promise<VerifySoftwareTokenResponse> = MainScope().promise {
        provider.verifySoftwareToken(
            accessToken = accessToken,
            friendlyDeviceName = friendlyDeviceName,
            userCode = userCode
        ).getOrThrow()
    }

    fun verifySoftwareTokenBySession(
        session: String,
        friendlyDeviceName: String?,
        userCode: String
    ): Promise<VerifySoftwareTokenResponse> = MainScope().promise {
        provider.verifySoftwareTokenBySession(
            friendlyDeviceName = friendlyDeviceName,
            session = session,
            userCode = userCode
        ).getOrThrow()
    }
}
