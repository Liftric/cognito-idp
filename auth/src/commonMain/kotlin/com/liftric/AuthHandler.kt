package com.liftric

import com.liftric.base.*
import io.ktor.client.*
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.core.String
import kotlinx.coroutines.*
import kotlinx.coroutines.CoroutineDispatcher

internal expect val ApplicationDispatcher: CoroutineDispatcher

open class AuthHandler(
    private val configuration: Configuration,
    private val settingsStore: SettingsStore,
    private val secretStore: SecretStore
): Auth {
    enum class RequestType {
        signIn, signUp, confirmSignUp, signOut, getUser, changePassword,
        deleteUser, updateUserAttributes, forgotPassword, confirmForgotPassword
    }

    private val accessToken = settingsStore.string(Key.AccessToken)
    private val password = secretStore.vault.string(Key.Password)

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
        response: (error: Error?, value: String?) -> Unit
    ) = dispatch {
        deleteUserRequest(response)
    }

    override fun getUser(
        response: (error: Error?, value: String?) -> Unit
    ) {
        val username = secretStore.vault.string(Key.Username)
        val password = secretStore.vault.string(Key.Password)

        if (username == null || password == null) {
            response(Error(ErrorMessage.notSignedIn), null)
        } else {
            dispatch {
                if (accessTokenIsOutdated()) {
                    signInRequest(username, password) { _,_ ->
                        dispatch {
                            getUserRequest(response)
                        }
                    }
                } else {
                    getUserRequest(response)
                }
            }
        }
    }

    override fun signOut(
        response: (error: Error?, value: String?) -> Unit
    ) = dispatch {
        signOutRequest(response)
    }

    override fun updateUserAttributes(
        attributes: List<UserAttribute>,
        response: (error: Error?, value: String?) -> Unit
    ) = dispatch {
        updateUserAttributesRequest(attributes, response)
    }

    override fun changePassword(
        toNewPassword: String,
        response: (error: Error?, value: String?) -> Unit
    ) = dispatch {
        changePasswordRequest(toNewPassword, response)
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
        val payload = SignUp(
            ClientId = configuration.clientId,
            Username = username,
            Password = password,
            UserAttributes = attributes?: listOf()
        )

        request(
            RequestType.signUp,
            serialize(SignUp.serializer(), payload),
            response
        )
    }

    private suspend fun signInRequest(
        username: String,
        password: String,
        response: (error: Error?, value: String?) -> Unit
    ) {
        val payload = Authentication(
            AuthFlow.UserPasswordAuth,
            configuration.clientId,
            AuthParameters(username, password)
        )

        request(
            RequestType.signIn,
            serialize(Authentication.serializer(), payload)
        ) { error, value ->
            secureCredentials(username, password)

            value?.let {
                val obj = parse(AuthResponse.serializer(), value)
                val date = accessTokenValidUntil(obj.AuthenticationResult.ExpiresIn.toDouble())
                saveAccessToken(obj.AuthenticationResult.AccessToken, date)
            }

            response(error, value)
        }
    }

    private suspend fun deleteUserRequest(
        response: (error: Error?, value: String?) -> Unit
    ) {
        if (accessToken == null) {
            response(Error(ErrorMessage.notSignedIn), null)
        } else {
            val payload = AccessToken(accessToken)

            request(
                RequestType.deleteUser,
                serialize(AccessToken.serializer(), payload)
            ) { error, value ->
                value?.let {
                    deleteCredentials()
                    deleteAccessToken()
                }

                response(error, value)
            }
        }
    }

    private suspend fun signOutRequest(
        response: (error: Error?, value: String?) -> Unit
    ) {
        if (accessToken == null) {
            response(Error(ErrorMessage.notSignedIn), null)
        } else {
            val payload = AccessToken(accessToken)

            request(
                RequestType.signOut,
                serialize(AccessToken.serializer(), payload)
            ) { error, value ->
                value?.let {
                    deleteCredentials()
                    deleteAccessToken()
                }

                response(error, value)
            }
        }
    }

    private suspend fun updateUserAttributesRequest(
        attributes: List<UserAttribute>,
        response: (error: Error?, value: String?) -> Unit
    ) {
        if (accessToken == null) {
            response(Error(ErrorMessage.notSignedIn), null)
        } else {
            val payload = UpdateUserAttributes(accessToken, attributes)

            request(
                RequestType.updateUserAttributes,
                serialize(UpdateUserAttributes.serializer(), payload),
                response
            )
        }
    }

    private suspend fun changePasswordRequest(
        toNewPassword: String,
        response: (error: Error?, value: String?) -> Unit
    ) {
        if (accessToken == null || password == null) {
            response(Error(ErrorMessage.notSignedIn), null)
        } else {
            val payload = ChangePassword(accessToken, password, toNewPassword)

            request(
                RequestType.changePassword,
                serialize(ChangePassword.serializer(), payload)
            ) { error, value ->
                value?.let { secretStore.vault.set(Key.Password, toNewPassword) }
                response(error, value)
            }
        }
    }

    private suspend fun getUserRequest(
        response: (error: Error?, value: String?) -> Unit
    ) {
        if (accessToken == null) {
            response(Error(ErrorMessage.notSignedIn), null)
        } else {
            val payload = AccessToken(accessToken)

            request(
                RequestType.getUser,
                serialize(AccessToken.serializer(), payload),
                response
            )
        }
    }

    //----------------
    // HELPER METHODS
    //----------------

    fun setClient(client: HttpClient) {
        this.client = client
    }

    open fun dispatch(block: suspend () -> Unit) {
        MainScope().launch(ApplicationDispatcher) { block() }
    }

    suspend fun request(
        type: RequestType,
        payload: String,
        completion: (error: Error?, response: String?) -> Unit
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

        if (response.status.value == 200) {
            completion(null, String(response.readBytes()))
        } else {
            val error = parse(RequestError.serializer(), String(response.readBytes()))
            completion(Error(error.message), null)
        }
    }

    private fun accessTokenIsOutdated(): Boolean {
        settingsStore.double(Key.AccessTokenValidUntil)?.let {
            return Timestamp.now() > it
        }
        return false
    }

    private fun accessTokenValidUntil(offset: Double): Double {
        return Timestamp.now() + offset
    }

    private fun saveAccessToken(accessToken: String, validUntil: Double) {
        settingsStore.set(Key.AccessToken, accessToken)
        settingsStore.set(Key.AccessTokenValidUntil, validUntil)
    }

    private fun secureCredentials(username: String, password: String) {
        secretStore.vault.set(Key.Username, username)
        secretStore.vault.set(Key.Password, password)
    }

    private fun deleteCredentials() {
        secretStore.vault.deleteObject(Key.Username)
        secretStore.vault.deleteObject(Key.Password)
    }

    private fun deleteAccessToken() {
        settingsStore.deleteObject(Key.AccessToken)
        settingsStore.deleteObject(Key.AccessTokenValidUntil)
    }
}