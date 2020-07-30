package com.liftric

import com.autodesk.coroutineworker.threadSafeSuspendCallback
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

    //-----------
    // INTERFACE
    //-----------

    override fun signUp(
        username: String,
        password: String,
        attributes: List<UserAttribute>?,
        response: (error: Error?, value: String?) -> Unit
    ) = dispatch {
        signUpRequest(username, password, attributes).await()
    }

    override fun signIn(
        username: String,
        password: String,
        response: (error: Error?, value: String?) -> Unit
    ) = dispatch {
        signInRequest(username, password)
    }

    override fun deleteUser(
        accessToken: String,
        response: (error: Error?, value: String?) -> Unit
    ) = dispatch {
        deleteUserRequest(accessToken)
    }

    override fun getUser(
        accessToken: String,
        response: (error: Error?, value: String?) -> Unit
    ) = dispatch {
        getUserRequest(accessToken)
    }

    override fun signOut(
        accessToken: String,
        response: (error: Error?, value: String?) -> Unit
    ) = dispatch {
        signOutRequest(accessToken)
    }

    override fun updateUserAttributes(
        accessToken: String,
        attributes: List<UserAttribute>,
        response: (error: Error?, value: String?) -> Unit
    ) = dispatch {
        updateUserAttributesRequest(accessToken, attributes)
    }

    override fun changePassword(
        accessToken: String,
        currentPassword: String,
        newPassword: String,
        response: (error: Error?, value: String?) -> Unit
    ) = dispatch {
        changePasswordRequest(accessToken, currentPassword, newPassword)
    }

    //----------
    // REQUESTS
    //----------

    internal suspend fun signUpRequest(
        username: String,
        password: String,
        attributes: List<UserAttribute>?
    ): Deferred<Pair<Error?, String?>> = dispatchAsync {
            request(
                RequestType.signUp,
                serialize(SignUp.serializer(), SignUp(
                        ClientId = configuration.clientId,
                        Username = username,
                        Password = password,
                        UserAttributes = attributes?: listOf()
                ))
            )
        }

        internal suspend fun signInRequest(
                username: String,
                password: String
        ): Deferred<Pair<Error?, String?>> = dispatchAsync {
            val payload = Authentication(
                    AuthFlow.UserPasswordAuth,
                    configuration.clientId,
                    AuthParameters(username, password)
            )

            request(
                RequestType.signIn,
                serialize(Authentication.serializer(), payload)
            )
        }

        internal suspend fun deleteUserRequest(
                accessToken: String
        ): Pair<Error?, String?> {
            val payload = AccessToken(accessToken)

            return request(
                    RequestType.deleteUser,
                    serialize(AccessToken.serializer(), payload)
            )
        }

        internal suspend fun signOutRequest(
                accessToken: String
        ): Pair<Error?, String?> {
            val payload = AccessToken(accessToken)

            return request(
                    RequestType.signOut,
                    serialize(AccessToken.serializer(), payload)
            )
        }

        internal suspend fun updateUserAttributesRequest(
                accessToken: String,
                attributes: List<UserAttribute>
        ): Pair<Error?, String?> {
            val payload = UpdateUserAttributes(accessToken, attributes)

            return request(
                    RequestType.updateUserAttributes,
                    serialize(UpdateUserAttributes.serializer(), payload)
            )
        }

        internal suspend fun changePasswordRequest(
                accessToken: String,
                currentPassword: String,
                newPassword: String
        ): Pair<Error?, String?> {
            val payload = ChangePassword(accessToken, currentPassword, newPassword)

            return request(
                    RequestType.changePassword,
                    serialize(ChangePassword.serializer(), payload)
            )
        }

        internal suspend fun getUserRequest(
                accessToken: String
        ): Pair<Error?, String?> {
            val payload = AccessToken(accessToken)

            return request(
                    RequestType.getUser,
                    serialize(AccessToken.serializer(), payload)
            )
        }

        //----------------
        // HELPER METHODS
        //----------------

        fun setClient(client: HttpClient) {
            this.client = client
        }

        open fun dispatch(block: suspend () -> Unit) {
            MainScope().launch(ApplicationDispatcher) {
                block()
            }
        }

        open fun dispatchAsync(block: suspend () -> Pair<Error?, String?>): Deferred<Pair<Error?, String?>> {
            return MainScope().async(ApplicationDispatcher) {
                block()
            }
        }

        suspend fun request(
                type: RequestType,
                payload: String
        ): Pair<Error?, String?> {
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
                Pair(null, String(response.readBytes()))
            } else {
                val error = parse(RequestError.serializer(), String(response.readBytes()))
                Pair(Error(error.message), null)
            }
        }
    }