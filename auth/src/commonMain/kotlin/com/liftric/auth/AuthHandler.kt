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
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class UserNotFoundException(message: String): Exception(message)
class NotAuthorizedException(message: String): Exception(message)

/**
 * AWS Cognito authentication client
 * Provides common request methods
 *
 * Don't forget to check [AuthHandlerJS] when doing changes here :)
 */
open class AuthHandler(private val configuration: Configuration): Auth {
    enum class RequestType {
        signIn, signUp, confirmSignUp, signOut, getUser, changePassword,
        deleteUser, updateUserAttributes, forgotPassword, confirmForgotPassword,
        getUserAttributeVerificationCode, verifyUserAttribute
    }

    private val client = HttpClient {
        val configuration = configuration
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
    ): Result<SignUpResponse> {
        return request(
            RequestType.signUp,
            serialize(
                SignUp(
                    ClientId = configuration.clientId,
                    Username = username,
                    Password = password,
                    UserAttributes = attributes ?: listOf()
                )
            )
        ).onResult {
            try {
                Result.success(parse(it))
            } catch (e: SerializationException) {
                Result.failure(e)
            }
        }
    }

    override suspend fun confirmSignUp(
        username: String,
        confirmationCode: String
    ): Result<Unit> {
        return request(
            RequestType.confirmSignUp,
            serialize(
                ConfirmSignUp(
                    configuration.clientId,
                    username,
                    confirmationCode
                )
            )
        ).onResult {
            Result.success(Unit)
        }
    }

    override suspend fun signIn(
        username: String,
        password: String
    ): Result<SignInResponse> {
        return request(
            RequestType.signIn,
            serialize(
                Authentication(
                    AuthFlow.UserPasswordAuth,
                    configuration.clientId,
                    AuthParameters(username, password)
                )
            )
        ).onResult {
            try {
                Result.success(parse(it))
            } catch (e: SerializationException) {
                Result.failure(e)
            }
        }
    }

    override suspend fun refresh(refreshToken: String): Result<SignInResponse> {
        return request(
                RequestType.signIn,
                serialize(
                        RefreshAuthentication(
                                AuthFlow.RefreshTokenAuth,
                                configuration.clientId,
                                RefreshParameters(refreshToken)
                        )
                )
        ).onResult {
            try {
                Result.success(parse(it))
            } catch (e: SerializationException) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getUser(accessToken: String): Result<GetUserResponse> {
        return request(
            RequestType.getUser,
            serialize(AccessToken(accessToken))
        ).onResult {
            try {
                Result.success(parse(it))
            } catch (e: SerializationException) {
                Result.failure(e)
            }
        }
    }

    override suspend fun updateUserAttributes(
        accessToken: String,
        attributes: List<UserAttribute>
    ): Result<UpdateUserAttributesResponse> {
        return request(
            RequestType.updateUserAttributes,
            serialize(UpdateUserAttributes(accessToken, attributes))
        ).onResult {
            try {
                Result.success(parse(it))
            } catch (e: SerializationException) {
                Result.failure(e)
            }
        }
    }

    override suspend fun changePassword(
        accessToken: String,
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        return request(
            RequestType.changePassword,
            serialize(
                ChangePassword(
                    accessToken,
                    currentPassword,
                    newPassword
                )
            )
        ).onResult {
            Result.success(Unit)
        }
    }

    override suspend fun forgotPassword(
        username: String
    ): Result<ForgotPasswordResponse> {
        return request(
            RequestType.forgotPassword,
            serialize(
                ForgotPassword(
                    configuration.clientId,
                    username
                )
            )
        ).onResult {
            try {
                Result.success(parse(it))
            } catch (e: SerializationException) {
                Result.failure(e)
            }
        }
    }

    override suspend fun confirmForgotPassword(
        confirmationCode: String,
        username: String,
        password: String
    ): Result<Unit> {
        return request(
            RequestType.confirmForgotPassword,
            serialize(
                ConfirmForgotPassword(
                    configuration.clientId,
                    confirmationCode,
                    username,
                    password
                )
            )
        ).onResult {
            Result.success(Unit)
        }
    }

    override suspend fun getUserAttributeVerificationCode(
        accessToken: String,
        attributeName: String,
        clientMetadata: Map<String, String>?
    ): Result<GetAttributeVerificationCodeResponse> {
        return request(
            RequestType.getUserAttributeVerificationCode,
            serialize(
                GetUserAttributeVerificationCode(
                    accessToken,
                    attributeName,
                    clientMetadata
                )
            )
        ).onResult {
            try {
                Result.success(parse(it))
            } catch (e: SerializationException) {
                Result.failure(e)
            }
        }
    }

    override suspend fun verifyUserAttribute(
        accessToken: String,
        attributeName: String,
        code: String
    ): Result<Unit> {
        return request(
            RequestType.verifyUserAttribute,
            serialize(
                VerifyUserAttribute(
                    accessToken,
                    attributeName,
                    code
                )
            )
        ).onResult {
            Result.success(Unit)
        }
    }

    override suspend fun signOut(accessToken: String): Result<Unit> {
        return request(
            RequestType.signOut,
            serialize(AccessToken(accessToken))
        ).onResult {
            Result.success(Unit)
        }
    }

    override suspend fun deleteUser(accessToken: String): Result<Unit> {
        return request(
            RequestType.deleteUser,
            serialize(AccessToken(accessToken))
        ).onResult {
            Result.success(Unit)
        }
    }

    //----------
    // REQUEST
    //----------

    private suspend fun request(type: RequestType, payload: String): Result<String> {
        return try {
            val response = client.post<HttpResponse>(configuration.requestUrl) {
                header(
                    Header.AmzTarget,
                    when (type) {
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
                        RequestType.getUserAttributeVerificationCode -> IdentityProviderService.GetUserAttributeVerificationCode
                        RequestType.verifyUserAttribute -> IdentityProviderService.VerifyUserAttribute
                    }
                )
                body = payload
            }
            Result.success(response.readText(), response.status)
        } catch (e: ResponseException) {
            try {
                val error = parse<RequestError>(e.response.readText())
                Result.failure(
                    when (error.type) {
                        CognitoException.UserNotFound -> UserNotFoundException(error.message)
                        CognitoException.NotAuthorized -> NotAuthorizedException(error.message)
                        else -> Error(error.message)
                    },
                    e.response.status
                )
            } catch (e: SerializationException) {
                Result.failure(e)
            }
        } catch (e: SerializationException) {
            Result.failure(e)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}
