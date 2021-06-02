@file:JsExport

import com.liftric.auth.AuthHandler
import com.liftric.auth.Configuration
import com.liftric.auth.base.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import kotlin.js.Promise

/**
 * Typescript compatible [AuthHandler] implementation.
 */
class AuthHandlerJS(origin: String, regionString: String, clientId: String) {
    private val region = Region.values().first { it.code == regionString }
    private val handler: AuthHandler = AuthHandler(Configuration(origin, region, clientId))

    fun signUp(username: String, password: String, attributes: Array<UserAttribute>? = null): Promise<SignUpResponse> =
        MainScope().promise {
            handler.signUp(username, password, attributes?.toList())
                .getOrThrow()
        }

    fun confirmSignUp(username: String, confirmationCode: String): Promise<Unit> =
        MainScope().promise {
            handler.confirmSignUp(username, confirmationCode)
                .getOrThrow()
        }

    fun signIn(username: String, password: String): Promise<SignInResponseJS> =
        MainScope().promise {
            handler.signIn(username, password)
                .getOrThrow().let {
                    SignInResponseJS(
                        it.AuthenticationResult,
                        it.ChallengeParameters.map { MapEntry(it.key, it.value) }.toTypedArray()
                    )
                }
        }

    fun refresh(refreshToken: String): Promise<SignInResponseJS> =
        MainScope().promise {
            handler.refresh(refreshToken)
                .getOrThrow().let {
                    SignInResponseJS(
                        it.AuthenticationResult,
                        it.ChallengeParameters.map { MapEntry(it.key, it.value) }.toTypedArray()
                    )
                }
        }

    fun getUser(accessToken: String): Promise<GetUserResponseJS> =
        MainScope().promise {
            handler.getUser(accessToken)
                .getOrThrow().let {
                    GetUserResponseJS(
                        it.MFAOptions,
                        it.PreferredMfaSetting,
                        it.UserAttributes.toTypedArray(),
                        it.UserMFASettingList.toTypedArray(),
                        it.Username
                    )
                }
        }

    fun updateUserAttributes(
        accessToken: String,
        attributes: Array<UserAttribute>
    ): Promise<UpdateUserAttributesResponseJS> =
        MainScope().promise {
            handler.updateUserAttributes(accessToken, attributes.toList())
                .getOrThrow().let {
                    UpdateUserAttributesResponseJS(it.CodeDeliveryDetailsList.toTypedArray())
                }
        }

    fun changePassword(accessToken: String, currentPassword: String, newPassword: String): Promise<Unit> =
        MainScope().promise {
            handler.changePassword(accessToken, currentPassword, newPassword)
                .getOrThrow()
        }

    fun forgotPassword(username: String): Promise<ForgotPasswordResponse> =
        MainScope().promise {
            handler.forgotPassword(username)
                .getOrThrow()
        }

    fun confirmForgotPassword(confirmationCode: String, username: String, password: String): Promise<Unit> =
        MainScope().promise {
            handler.confirmForgotPassword(confirmationCode, username, password)
                .getOrThrow()
        }

    fun getUserAttributeVerificationCode(
        accessToken: String,
        attributeName: String,
        clientMetadata: Array<MapEntry>? = null
    ): Promise<GetAttributeVerificationCodeResponse> =
        MainScope().promise {
            handler.getUserAttributeVerificationCode(
                accessToken,
                attributeName,
                clientMetadata?.associate { it.key to it.value })
                .getOrThrow()
        }

    fun verifyUserAttribute(accessToken: String, attributeName: String, code: String): Promise<Unit> =
        MainScope().promise {
            handler.verifyUserAttribute(accessToken, attributeName, code)
                .getOrThrow()
        }

    fun signOut(accessToken: String): Promise<Unit> =
        MainScope().promise {
            handler.signOut(accessToken)
                .getOrThrow()
        }

    fun deleteUser(accessToken: String): Promise<Unit> =
        MainScope().promise {
            handler.deleteUser(accessToken)
                .getOrThrow()
        }
}

