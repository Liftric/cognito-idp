package com.liftric

import com.liftric.base.*
import io.ktor.client.HttpClient
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.core.String
import kotlinx.coroutines.*

internal expect val ApplicationDispatcher: CoroutineDispatcher
expect fun runBlocking(block: suspend () -> Unit)

/**
 * Authentifaction handler for AWS Cognito
 * Provides common request methods
 */
open class AuthHandler(private val configuration: Configuration): Auth {
    enum class RequestType {
        signIn, signUp, confirmSignUp, signOut, getUser, changePassword,
        deleteUser, updateUserAttributes, forgotPassword, confirmForgotPassword
    }

    private var client = HttpClient() {
        defaultRequest {
            configuration.setupDefaultRequest(headers)
            contentType(ContentType.parse(Header.AmzJson))
        }
    }

    /**
     * Runs suspended function in coroutine scope
     */
    open fun dispatch(block: suspend () -> Unit) {
        MainScope().launch(ApplicationDispatcher) {
            block()
        }
    }

    //----------
    // INTERFACE
    //----------

    override fun signUp(
        username: String,
        password: String,
        attributes: List<UserAttribute>?,
        response: (error: Error?, value: String?) -> Unit
    ) = dispatch {
        signUpRequest(username, password, attributes, response)
    }

    override fun signIn(
        username: String,
        password: String,
        response: (error: Error?, value: String?) -> Unit
    ) = dispatch {
        signInRequest(username, password, response)
    }

    override fun deleteUser(
        accessToken: String,
        response: (error: Error?, value: String?) -> Unit
    ) = dispatch {
        deleteUserRequest(accessToken, response)
    }

    override fun getUser(
        accessToken: String,
        response: (error: Error?, value: String?) -> Unit
    ) = dispatch {
        getUserRequest(accessToken, response)
    }

    override fun signOut(
        accessToken: String,
        response: (error: Error?, value: String?) -> Unit
    ) = dispatch {
        signOutRequest(accessToken, response)
    }

    override fun updateUserAttributes(
        accessToken: String,
        attributes: List<UserAttribute>,
        response: (error: Error?, value: String?) -> Unit
    ) = dispatch {
        updateUserAttributesRequest(accessToken, attributes, response)
    }

    override fun changePassword(
        accessToken: String,
        currentPassword: String,
        newPassword: String,
        response: (error: Error?, value: String?) -> Unit
    ) = dispatch {
        changePasswordRequest(accessToken, currentPassword, newPassword, response)
    }

    //----------
    // REQUESTS
    //----------

    private suspend fun signUpRequest(
        username: String,
        password: String,
        attributes: List<UserAttribute>?,
        response: (error: Error?, value: String?) -> Unit
    ) {
        request(
            RequestType.signUp,
            serialize(
                SignUp.serializer(),
                SignUp(
                    ClientId = configuration.clientId,
                    Username = username,
                    Password = password,
                    UserAttributes = attributes?: listOf()
                )
            ),
            response
        )
    }

    private suspend fun signInRequest(
        username: String,
        password: String,
        response: (error: Error?, value: String?) -> Unit
    ) {
        request(
            RequestType.signIn,
            serialize(
                Authentication.serializer(),
                Authentication(
                    AuthFlow.UserPasswordAuth,
                    configuration.clientId,
                    AuthParameters(username, password)
                )
            ),
            response
        )
    }

    private suspend fun deleteUserRequest(
        accessToken: String,
        response: (error: Error?, value: String?) -> Unit
    ) {
        request(
            RequestType.deleteUser,
            serialize(
                AccessToken.serializer(),
                AccessToken(accessToken)
            ),
            response
        )
    }

    private suspend fun signOutRequest(
        accessToken: String,
        response: (error: Error?, value: String?) -> Unit
    ) {
        request(
            RequestType.signOut,
            serialize(
                AccessToken.serializer(),
                AccessToken(accessToken)
            ),
            response
        )
    }

    private suspend fun updateUserAttributesRequest(
        accessToken: String,
        attributes: List<UserAttribute>,
        response: (error: Error?, value: String?) -> Unit
    ) {
        request(
            RequestType.updateUserAttributes,
            serialize(
                UpdateUserAttributes.serializer(),
                UpdateUserAttributes(accessToken, attributes)
            ),
            response
        )
    }

    private suspend fun changePasswordRequest(
        accessToken: String,
        currentPassword: String,
        newPassword: String,
        response: (error: Error?, value: String?) -> Unit
    ) {
        request(
            RequestType.changePassword,
            serialize(
                ChangePassword.serializer(),
                ChangePassword
                    (accessToken,
                    currentPassword,
                    newPassword)
            ),
            response
        )
    }

    private suspend fun getUserRequest(
        accessToken: String,
        response: (error: Error?, value: String?) -> Unit
    ) {
        request(
            RequestType.getUser,
            serialize(
                AccessToken.serializer(),
                AccessToken(accessToken)
            ),
            response
        )
    }

    private suspend fun request(
        type: RequestType,
        payload: String,
        completion: (error: Error?, value: String?) -> Unit
    ) {
        val response = client.post<HttpResponse>(configuration.requestUrl) {
            header(
                    Header.AmzTarget,
                    when(type) {
                        RequestType.signUp -> IdentityProviderService.SignUp
                        RequestType.confirmSignUp -> IdentityProviderService.ConfirmSignUp
                        RequestType.signIn -> IdentityProviderService.InitiateAuth
                        RequestType.signOut -> IdentityProviderService.GlobalSignOut
                        RequestType.getUser -> IdentityProviderService.GetUser
                        RequestType.changePassword -> IdentityProviderService.ChangePassword
                        RequestType.forgotPassword -> IdentityProviderService.ForgotPassword
                        RequestType.confirmForgotPassword -> IdentityProviderService.ConfirmForgotPassword
                        RequestType.deleteUser -> IdentityProviderService.DeleteUser
                        RequestType.updateUserAttributes -> IdentityProviderService.UpdateUserAttributes
                    }
            )
            body = payload
        }

        return if (response.status.value == 200) {
            completion(null, String(response.readBytes()))
        } else {
            val error = parse(RequestError.serializer(), String(response.readBytes()))
            completion(Error(error.message), null)
        }
    }
}