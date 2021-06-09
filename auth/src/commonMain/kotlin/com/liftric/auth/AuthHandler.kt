package com.liftric.auth

import com.liftric.auth.base.*
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

class CodeMismatchException(message: String) : Exception(message)
class ExpiredCodeException(message: String) : Exception(message)
class InternalErrorException(message: String) : Exception(message)
class InvalidLambdaResponseException(message: String) : Exception(message)
class InvalidParameterException(message: String) : Exception(message)
class InvalidPasswordException(message: String) : Exception(message)
class LimitExceededException(message: String) : Exception(message)
class NotAuthorizedException(message: String) : Exception(message)
class ResourceNotFoundException(message: String) : Exception(message)
class TooManyFailedAttemptsException(message: String) : Exception(message)
class TooManyRequestsException(message: String) : Exception(message)
class UnexpectedLambdaException(message: String) : Exception(message)
class UserLambdaValidationException(message: String) : Exception(message)
class UserNotConfirmedException(message: String) : Exception(message)
class UserNotFoundException(message: String) : Exception(message)

/**
 * AWS Cognito authentication client
 * Provides common request methods
 *
 * Don't forget to check [AuthHandlerJS] when doing changes here :)
 */
open class AuthHandler(private val configuration: Configuration) : Auth {
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

    //----------
    // INTERFACE
    //----------

    override suspend fun signUp(
        username: String,
        password: String,
        attributes: List<UserAttribute>?
    ): Result<SignUpResponse> = request(
        RequestType.signUp,
        SignUp(
            ClientId = configuration.clientId,
            Username = username,
            Password = password,
            UserAttributes = attributes ?: listOf()
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
        UpdateUserAttributes(accessToken, attributes)
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

    override suspend fun deleteUser(accessToken: String): Result<Unit> = request(
        RequestType.deleteUser,
        AccessToken(accessToken)
    )

    //----------
    // REQUEST
    //----------

    private suspend inline fun <reified T> request(type: RequestType, payload: Any): Result<T> = try {
        val response = client.post<HttpResponse>(configuration.requestUrl) {
            header(Header.AmzTarget, type.identityProviderServiceValue)
            body = payload
        }
        if (T::class.simpleName == "Unit") {
            // otherwise kotlinx.serialization will fail
            Result.success(Unit as T, response.status)
        } else {
            Result.success(json.decodeFromString(response.readText()), response.status)
        }
    } catch (e: ResponseException) {
        try {
            val error = json.decodeFromString<RequestError>(e.response.readText())
            Result.failure(
                when (error.type) {
                    CognitoException.CodeMismatch -> CodeMismatchException(error.message)
                    CognitoException.ExpiredCode -> ExpiredCodeException(error.message)
                    CognitoException.InternalError -> InternalErrorException(error.message)
                    CognitoException.InvalidLambdaResponse -> InvalidLambdaResponseException(error.message)
                    CognitoException.InvalidParameter -> InvalidParameterException(error.message)
                    CognitoException.InvalidPassword -> InvalidPasswordException(error.message)
                    CognitoException.LimitExceeded -> LimitExceededException(error.message)
                    CognitoException.NotAuthorized -> NotAuthorizedException(error.message)
                    CognitoException.ResourceNotFound -> ResourceNotFoundException(error.message)
                    CognitoException.TooManyFailedAttempts -> TooManyFailedAttemptsException(error.message)
                    CognitoException.TooManyRequests -> TooManyRequestsException(error.message)
                    CognitoException.UnexpectedLambda -> UnexpectedLambdaException(error.message)
                    CognitoException.UserLambdaValidation -> UserLambdaValidationException(error.message)
                    CognitoException.UserNotConfirmed -> UserNotConfirmedException(error.message)
                    CognitoException.UserNotFound -> UserNotFoundException(error.message)
                    else -> Error(error.message)
                },
                e.response.status
            )
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    } catch (t: IOException) {
        Result.failure(t)
    } catch (t: Throwable) {
        Result.failure(t)
    }
}

