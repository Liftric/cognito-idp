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
                .getOrThrow().let {
                    SignUpResponseJS(it.CodeDeliveryDetails?.toJs(), it.UserConfirmed, it.UserSub)
                }
        }

    fun confirmSignUp(username: String, confirmationCode: String): Promise<Unit> =
        MainScope().promise {
            provider.confirmSignUp(username, confirmationCode)
                .getOrThrow()
        }

    fun resendConfirmationCode(username: String): Promise<ResendConfirmationCodeResponseJS> =
        MainScope().promise {
            provider.resendConfirmationCode(username)
                .getOrThrow().let { ResendConfirmationCodeResponseJS(it.CodeDeliveryDetails.toJs()) }
        }

    fun signIn(username: String, password: String): Promise<SignInResponseJS> =
        MainScope().promise {
            provider.signIn(username, password)
                .getOrThrow().let {
                    SignInResponseJS(
                        it.AuthenticationResult?.toJs(),
                        it.ChallengeParameters.map { MapEntry(it.key, it.value) }.toTypedArray()
                    )
                }
        }

    fun refresh(refreshToken: String): Promise<SignInResponseJS> =
        MainScope().promise {
            provider.refresh(refreshToken)
                .getOrThrow().let {
                    SignInResponseJS(
                        it.AuthenticationResult?.toJs(),
                        it.ChallengeParameters.map { MapEntry(it.key, it.value) }.toTypedArray()
                    )
                }
        }

    fun getUser(accessToken: String): Promise<GetUserResponseJS> =
        MainScope().promise {
            provider.getUser(accessToken)
                .getOrThrow().let {
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
                .getOrThrow().let {
                    UpdateUserAttributesResponseJS(it.CodeDeliveryDetailsList.map { it.toJs() }.toTypedArray())
                }
        }

    fun changePassword(accessToken: String, currentPassword: String, newPassword: String): Promise<Unit> =
        MainScope().promise {
            provider.changePassword(accessToken, currentPassword, newPassword)
                .getOrThrow()
        }

    fun forgotPassword(username: String): Promise<ForgotPasswordResponseJS> =
        MainScope().promise {
            provider.forgotPassword(username)
                .getOrThrow().let { ForgotPasswordResponseJS(it.CodeDeliveryDetails.toJs()) }
        }

    fun confirmForgotPassword(confirmationCode: String, username: String, password: String): Promise<Unit> =
        MainScope().promise {
            provider.confirmForgotPassword(confirmationCode, username, password)
                .getOrThrow()
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
                .getOrThrow().let { GetAttributeVerificationCodeResponseJS(it.CodeDeliveryDetails.toJs()) }
        }

    fun verifyUserAttribute(accessToken: String, attributeName: String, code: String): Promise<Unit> =
        MainScope().promise {
            provider.verifyUserAttribute(accessToken, attributeName, code)
                .getOrThrow()
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
        smsMfaSettings: MfaSettingJS?,
        softwareTokenMfaSettings: MfaSettingJS?
    ): Promise<Unit> = MainScope().promise {
        provider.setUserMFAPreference(
            accessToken,
            smsMfaSettings?.toCommon(),
            softwareTokenMfaSettings?.toCommon()
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
                it.AuthenticationResult?.toJs(),
                it.ChallengeParameters.map { MapEntry(it.key, it.value) }.toTypedArray()
            )
        }
    }

    fun associateSoftwareToken(
        accessToken: String?,
        session: String?
    ): Promise<AssociateSoftwareTokenResponseJS> = MainScope().promise {
        provider.associateSoftwareToken(accessToken, session).getOrThrow().let {
            AssociateSoftwareTokenResponseJS(it.SecretCode, it.Session)
        }
    }

    fun verifySoftwareToken(
        accessToken: String?,
        friendlyDeviceName: String?,
        session: String?,
        userCode: String
    ): Promise<VerifySoftwareTokenResponseJS> = MainScope().promise {
        provider.verifySoftwareToken(
            accessToken,
            friendlyDeviceName,
            session,
            userCode
        ).getOrThrow().let {
            VerifySoftwareTokenResponseJS(it.Session, it.Status)
        }
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

private fun MfaSettingJS.toCommon(): MfaSettings =
    MfaSettings(Enabled = Enabled, PreferredMfa = PreferredMfa)