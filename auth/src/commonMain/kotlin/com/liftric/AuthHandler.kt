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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy

internal expect val ApplicationDispatcher: CoroutineDispatcher

class AuthHandler(
    private val configuration: Configuration,
    private val settingsStore: SettingsStore,
    private val secretStore: SecretStore
): Auth {
    private enum class RequestType {
        signIn, signUp, confirmSignUp, signOut, getUser, changePassword,
        deleteUser, updateUserAttributes, forgotPassword, confirmForgotPassword
    }

    private val client = HttpClient() {
        defaultRequest {
            configuration.setupDefaultRequest(headers)
            contentType(ContentType.parse(Header.AmzJson))
        }
    }

    override fun signUp(
        username: String,
        password: String,
        attributes: List<UserAttribute>?,
        response: (error: Error?, value: String?) -> Unit
    ) {
        val clientId: String = configuration.clientId

        val payload = SignUp(
            Username = username,
            Password = password,
            ClientId = clientId,
            UserAttributes = attributes?: listOf()
        )

        request(
            RequestType.signUp,
            serialize(SignUp.serializer(), payload)
        ) { error, value ->
            response(error, value)
        }
    }

    override fun signIn(
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
            secure(username, password)

            value?.let {
                val obj = parse(AuthResponse.serializer(), value)
                val date = accessTokenValidUntil(obj.AuthenticationResult.ExpiresIn.toDouble())
                save(obj.AuthenticationResult.AccessToken, date)
            }

            response(error, value)
        }
    }

    override fun signOut(response: (error: Error?, value: String?) -> Unit) {
        val payload = AccessToken(
            settingsStore.string(Key.AccessToken)?: return
        )

        request(
            RequestType.signOut,
            serialize(AccessToken.serializer(), payload)
        ) { error, value ->
            value?.let {
                deleteUsernamePassword()
                deleteAccessToken()
            }

            response(error, value)
        }
    }

    override fun getUser(response: (error: Error?, value: String?) -> Unit) {
        val username: String = secretStore.vault.string(Key.Username)?: return
        val password = secretStore.vault.string(Key.Password)?: return

        if (accessTokenIsOutdated()) {
            signIn(username, password) { _,_ ->
                requestUser() { error, value ->
                    response(error, value)
                }
            }
        } else {
            requestUser() { error, value ->
                response(error, value)
            }
        }
    }

    override fun updateUserAttributes(
        attributes: List<UserAttribute>,
        response: (error: Error?, value: String?) -> Unit
    ) {
        val payload = UpdateUserAttributes(
            secretStore.vault.string(Key.AccessToken)?:return,
            attributes
        )

        request(
            RequestType.updateUserAttributes,
            serialize(UpdateUserAttributes.serializer(), payload)
        ) { error, value ->
            response(error, value)
        }
    }

    override fun changePassword(
        toNewPassword: String,
        response: (error: Error?, value: String?) -> Unit
    ) {
        val payload = ChangePassword(
            settingsStore.string(Key.AccessToken)?: return,
            secretStore.vault.string(Key.Password)?: return,
            toNewPassword
        )

        request(
            RequestType.changePassword,
            serialize(ChangePassword.serializer(), payload)
        ) { error, value ->
            secretStore.vault.set(Key.Password, toNewPassword)

            response(error, value)
        }
    }

    override fun deleteUser(response: (error: Error?, value: String?) -> Unit) {
        val payload = AccessToken(
            settingsStore.string(Key.AccessToken)?: return
        )

        request(
            RequestType.deleteUser,
            serialize(AccessToken.serializer(), payload)
        ) { error, value ->
            value?.let {
                deleteUsernamePassword()
                deleteAccessToken()
            }

            response(error, value)
        }
    }

    //----------------
    // Helper methods
    //----------------

    private fun requestUser(response: (error: Error?, value: String?) -> Unit) {
        val payload = AccessToken(
            settingsStore.string(Key.AccessToken)?: return
        )

        request(
            RequestType.getUser,
            serialize(AccessToken.serializer(), payload)
        ) { error, value ->
            response(error, value)
        }
    }

    private fun request(
        type: RequestType,
        payload: String,
        completion: (error: Error?, response: String?) -> Unit
    ) {
        MainScope().apply {
            launch(ApplicationDispatcher) {
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
        }
    }

    private fun <T> serialize(strategy: SerializationStrategy<T>, value: T): String {
        return Json(JsonConfiguration.Stable).stringify(strategy, value)
    }

    private fun <T> parse(strategy: DeserializationStrategy<T>, value: String): T {
        return Json(JsonConfiguration.Stable).parse(strategy, value)
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

    private fun save(accessToken: String, validUntil: Double) {
        settingsStore.set(Key.AccessToken, accessToken)
        settingsStore.set(Key.AccessTokenValidUntil, validUntil)
    }

    private fun secure(username: String, password: String) {
        secretStore.vault.set(Key.Username, username)
        secretStore.vault.set(Key.Password, password)
    }

    private fun deleteUsernamePassword() {
        secretStore.vault.deleteObject(Key.Username)
        secretStore.vault.deleteObject(Key.Password)
    }

    private fun deleteAccessToken() {
        settingsStore.deleteObject(Key.AccessToken)
        settingsStore.deleteObject(Key.AccessTokenValidUntil)
    }
}