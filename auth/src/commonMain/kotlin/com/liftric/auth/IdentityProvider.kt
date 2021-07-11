package com.liftric.auth

import com.liftric.auth.core.*
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.errors.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

open class IdentityProviderException(val status: HttpStatusCode?, val type: AWSException?, message: String) : Exception(message)

/** Don't forget [IdentityProviderJS] when doing changes here :) */

/**
 * AWS Cognito Identity Provider.
 * Provides common request methods.
 */
open class IdentityProvider(region: Region, clientId: String) : Provider {
    private val json = Json {
        allowStructuredMapKeys = true
    }
    private val configuration = Configuration(region, clientId)
    private val client = HttpClient {
        /**
         * When referencing members that are in the
         * IdentityProvider's scope, assign them to
         * a new variable in this scope. Needed to
         * avoid [InvalidMutationException] in iOS.
         */
        val configuration = configuration
        val json = json
        install(JsonFeature) {
            serializer = KotlinxSerializer(json)
            acceptContentTypes = listOf(
                ContentType.parse(Header.AmzJson),
                ContentType.Application.Json
            )
        }
        defaultRequest {
            configuration.setupDefaultRequest(headers)
            contentType(ContentType.parse(Header.AmzJson))
        }
    }

    override suspend fun signUp(
        username: String,
        password: String,
        attributes: List<UserAttribute>?
    ): Result<SignUpResponse> = request(
        Request.SignUp,
        SignUp(
            configuration.clientId,
            username,
            password,
            attributes?: listOf()
        )
    )

    override suspend fun confirmSignUp(
        username: String,
        confirmationCode: String
    ): Result<Unit> = request(
        Request.ConfirmSignUp,
        ConfirmSignUp(
            configuration.clientId,
            username,
            confirmationCode
        )
    )

    override suspend fun signIn(
        username: String,
        password: String
    ): Result<SignInResponse> = request(
        Request.SignIn,
        SignIn(
            Authentication.UserPasswordAuth.flow,
            configuration.clientId,
            SignIn.Parameters(username, password)
        )
    )

    override suspend fun refresh(refreshToken: String): Result<SignInResponse> = request(
        Request.SignIn,
        Refresh(
            Authentication.RefreshTokenAuth.flow,
            configuration.clientId,
            Refresh.Parameters(refreshToken)
        )
    )

    override suspend fun getUser(accessToken: String): Result<GetUserResponse> = request(
        Request.GetUser,
        AccessToken(accessToken)
    )

    override suspend fun updateUserAttributes(
        accessToken: String,
        attributes: List<UserAttribute>
    ): Result<UpdateUserAttributesResponse> = request(
        Request.UpdateUserAttributes,
        UpdateUserAttributes(
            accessToken,
            attributes
        )
    )

    override suspend fun changePassword(
        accessToken: String,
        currentPassword: String,
        newPassword: String
    ): Result<Unit> = request(
        Request.ChangePassword,
        ChangePassword(
            accessToken,
            currentPassword,
            newPassword
        )
    )

    override suspend fun forgotPassword(
        username: String
    ): Result<ForgotPasswordResponse> = request(
        Request.ForgotPassword,
        ForgotPassword(
            configuration.clientId,
            username
        )
    )

    override suspend fun confirmForgotPassword(
        confirmationCode: String,
        username: String,
        password: String
    ): Result<Unit> = request(
        Request.ConfirmForgotPassword,
        ConfirmForgotPassword(
            configuration.clientId,
            confirmationCode,
            username,
            password
        )
    )

    override suspend fun getUserAttributeVerificationCode(
        accessToken: String,
        attributeName: String,
        clientMetadata: Map<String, String>?
    ): Result<GetAttributeVerificationCodeResponse> = request(
        Request.GetUserAttributeVerificationCode,
        GetUserAttributeVerificationCode(
            accessToken,
            attributeName,
            clientMetadata
        )
    )

    override suspend fun verifyUserAttribute(
        accessToken: String,
        attributeName: String,
        code: String
    ): Result<Unit> = request(
        Request.VerifyUserAttribute,
        VerifyUserAttribute(
            accessToken,
            attributeName,
            code
        )
    )

    override suspend fun signOut(accessToken: String): Result<Unit> = request(
        Request.SignOut,
        AccessToken(accessToken)
    )

    override suspend fun revokeToken(refreshToken: String): Result<Unit> = request(
        Request.RevokeToken,
        RevokeToken(
            configuration.clientId,
            refreshToken
        )
    )

    override suspend fun deleteUser(accessToken: String): Result<Unit> = request(
        Request.DeleteUser,
        AccessToken(accessToken)
    )

    private suspend inline fun <reified T> request(type: Request, payload: Any): Result<T> = try {
        client.post<HttpResponse>(configuration.requestUrl) {
            header(Header.AmzTarget, type.value)
            body = payload
        }.run {
            when(T::class) {
                Unit::class -> Result.success(Unit as T)
                else -> Result.success(json.decodeFromString(readText()))
            }
        }
    } catch (e: ResponseException) {
        e.toIdentityProviderException()
    } catch (t: Throwable) {
        Result.failure(t)
    }

    private suspend inline fun <reified T> ResponseException.toIdentityProviderException(): Result<T> = try {
        json.decodeFromString<RequestError>(response.readText()).run {
            Result.failure(
                IdentityProviderException(
                    response.status,
                    try { AWSException.valueOf(this.type) }
                    catch (e: IllegalArgumentException) { null },
                    this.message
                )
            )
        }
    } catch (e: SerializationException) {
        Result.failure(e)
    }
}
