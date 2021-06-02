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
            handler
            TODO()
        }

    fun signIn(username: String, password: String): Promise<SignInResponseJS> =
        MainScope().promise {
            handler
            TODO()
        }

    fun refresh(refreshToken: String): Promise<SignInResponseJS> =
        MainScope().promise {
            handler
            TODO()
        }

    fun getUser(accessToken: String): Promise<GetUserResponseJS> =
        MainScope().promise {
            handler
            TODO()
        }

    fun updateUserAttributes(
        accessToken: String,
        attributes: Array<UserAttribute>
    ): Promise<UpdateUserAttributesResponseJS> =
        MainScope().promise {
            handler
            TODO()
        }

    fun changePassword(accessToken: String, currentPassword: String, newPassword: String): Promise<Unit> =
        MainScope().promise {
            handler
            TODO()
        }

    fun forgotPassword(username: String): Promise<ForgotPasswordResponse> =
        MainScope().promise {
            handler
            TODO()
        }

    fun confirmForgotPassword(confirmationCode: String, username: String, password: String): Promise<Unit> =
        MainScope().promise {
            handler
            TODO()
        }

    fun getUserAttributeVerificationCode(
        accessToken: String,
        attributeName: String,
        clientMetadata: Array<MapEntry>? = null
    ): Promise<GetAttributeVerificationCodeResponse> =
        MainScope().promise {
            handler
            TODO()
        }

    fun verifyUserAttribute(accessToken: String, attributeName: String, code: String): Promise<Unit> =
        MainScope().promise {
            handler
            TODO()
        }

    fun signOut(accessToken: String): Promise<Unit> =
        MainScope().promise {
            handler
            TODO()
        }

    fun deleteUser(accessToken: String): Promise<Unit> =
        MainScope().promise {
            handler
            TODO()
        }

}

