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

/** Don't forget [IdentityProviderJS] when doing changes here :) */

/**
 * AWS Cognito Identity Provider.
 * Provides common request methods.
 */
open class IdentityProvider(private val configuration: Configuration) : Provider {
    private val json = Json {
        allowStructuredMapKeys = true
    }
    private val client = HttpClient {
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
        RequestType.signUp,
        SignUp(
            configuration.clientId,
            username,
            password,
            attributes ?: listOf()
        )
    )

    override suspend fun confirmSignUp(
        username: String,
        confirmationCode: String
    ): Result<Unit> = request(
        RequestType.confirmSignUp,
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
        RequestType.signIn,
        Authentication(
            AuthFlow.UserPasswordAuth,
            configuration.clientId,
            AuthParameters(username, password)
        )
    )

    override suspend fun refresh(refreshToken: String): Result<SignInResponse> = request(
        RequestType.signIn,
        RefreshAuthentication(
            AuthFlow.RefreshTokenAuth,
            configuration.clientId,
            RefreshParameters(refreshToken)
        )
    )

    override suspend fun getUser(accessToken: String): Result<GetUserResponse> = request(
        RequestType.getUser,
        AccessToken(accessToken)
    )

    override suspend fun updateUserAttributes(
        accessToken: String,
        attributes: List<UserAttribute>
    ): Result<UpdateUserAttributesResponse> = request(
        RequestType.updateUserAttributes,
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
        RequestType.changePassword,
        ChangePassword(
            accessToken,
            currentPassword,
            newPassword
        )
    )

    override suspend fun forgotPassword(
        username: String
    ): Result<ForgotPasswordResponse> = request(
        RequestType.forgotPassword,
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
        RequestType.confirmForgotPassword,
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
        RequestType.getUserAttributeVerificationCode,
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
        RequestType.verifyUserAttribute,
        VerifyUserAttribute(
            accessToken,
            attributeName,
            code
        )
    )

    override suspend fun signOut(accessToken: String): Result<Unit> = request(
        RequestType.signOut,
        AccessToken(accessToken)
    )

    override suspend fun revokeToken(refreshToken: String): Result<Unit> = request(
        RequestType.revokeToken,
        RevokeToken(
            configuration.clientId,
            refreshToken
        )
    )

    override suspend fun deleteUser(accessToken: String): Result<Unit> = request(
        RequestType.deleteUser,
        AccessToken(accessToken)
    )

    private suspend inline fun <reified T> request(type: RequestType, payload: Any): Result<T> = try {
        val response = client.post<HttpResponse>(configuration.requestUrl) {
            header(Header.AmzTarget, type.identityProviderServiceValue)
            body = payload
        }
        if (T::class.simpleName == "Unit") {
            // otherwise kotlinx.serialization will fail
            Result.success(Unit as T)
        } else {
            Result.success(json.decodeFromString(response.readText()))
        }
    } catch (e: ResponseException) {
        try {
            val error = json.decodeFromString<RequestError>(e.response.readText())
            Result.failure(
                when (error.type) {
                    AWSException.CodeMismatch -> CodeMismatchException(e.response.status, error.message)
                    AWSException.ExpiredCode -> ExpiredCodeException(e.response.status, error.message)
                    AWSException.InternalError -> InternalErrorException(e.response.status, error.message)
                    AWSException.InvalidLambdaResponse -> InvalidLambdaResponseException(e.response.status, error.message)
                    AWSException.InvalidParameter -> InvalidParameterException(e.response.status, error.message)
                    AWSException.InvalidPassword -> InvalidPasswordException(e.response.status, error.message)
                    AWSException.LimitExceeded -> LimitExceededException(e.response.status, error.message)
                    AWSException.NotAuthorized -> NotAuthorizedException(e.response.status, error.message)
                    AWSException.ResourceNotFound -> ResourceNotFoundException(e.response.status, error.message)
                    AWSException.TooManyFailedAttempts -> TooManyFailedAttemptsException(e.response.status, error.message)
                    AWSException.TooManyRequests -> TooManyRequestsException(e.response.status, error.message)
                    AWSException.UnexpectedLambda -> UnexpectedLambdaException(e.response.status, error.message)
                    AWSException.UserLambdaValidation -> UserLambdaValidationException(e.response.status, error.message)
                    AWSException.UserNotConfirmed -> UserNotConfirmedException(e.response.status, error.message)
                    AWSException.UserNotFound -> UserNotFoundException(e.response.status, error.message)
                    else -> IdentityProviderException(e.response.status, error.message)
                }
            )
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    } catch (t: Throwable) {
        Result.failure(t)
    }
}

