package com.liftric

import com.liftric.base.*
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.serializer

/**
 * Authentifaction handler for AWS Cognito
 * Provides common request methods
 */
open class AuthHandler(private val configuration: Configuration) : Auth {
    enum class RequestType {
        signIn, signUp, confirmSignUp, signOut, getUser, changePassword,
        deleteUser, updateUserAttributes, forgotPassword, confirmForgotPassword
    }

    private val client = HttpClient() {
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
        val response = request(
            RequestType.signUp,
            serialize(
                SignUp.serializer(),
                SignUp(
                    ClientId = configuration.clientId,
                    Username = username,
                    Password = password,
                    UserAttributes = attributes ?: listOf()
                )
            )
        )
        return if (response.isSuccess) {
            try {
                Result.success(parse(SignUpResponse.serializer(), response.getOrNull()!!))
            } catch (e: SerializationException) {
                Result.failure(e)
            }
        } else {
            Result.failure(response.exceptionOrNull()!!)
        }
    }

    override suspend fun signIn(
        username: String,
        password: String
    ): Result<SignInResponse> {
        val response = request(
            RequestType.signIn,
            serialize(
                Authentication.serializer(),
                Authentication(
                    AuthFlow.UserPasswordAuth,
                    configuration.clientId,
                    AuthParameters(username, password)
                )
            )
        )
        return if (response.isSuccess) {
            try {
                Result.success(parse(SignInResponse.serializer(), response.getOrNull()!!))
            } catch (e: SerializationException) {
                Result.failure(e)
            }
        } else {
            Result.failure(response.exceptionOrNull()!!)
        }
    }

    override suspend fun signOut(accessToken: String): Result<Unit> {
        val response = request(
            RequestType.signOut,
            serialize(
                AccessToken.serializer(),
                AccessToken(accessToken)
            )
        )
        return if (response.isSuccess) {
            Result.success(Unit)
        } else {
            Result.failure(response.exceptionOrNull()!!)
        }
    }

    override suspend fun getUser(accessToken: String): Result<GetUserResponse> {
        val response = request(
            RequestType.getUser,
            serialize(
                AccessToken.serializer(),
                AccessToken(accessToken)
            )
        )
        return if (response.isSuccess) {
            try {
                Result.success(parse(GetUserResponse.serializer(), response.getOrNull()!!))
            } catch (e: SerializationException) {
                Result.failure(e)
            }
        } else {
            Result.failure(response.exceptionOrNull()!!)
        }
    }

    override suspend fun updateUserAttributes(
        accessToken: String,
        attributes: List<UserAttribute>
    ): Result<UpdateUserAttributesResponse> {
        val response = request(
            RequestType.updateUserAttributes,
            serialize(
                UpdateUserAttributes.serializer(),
                UpdateUserAttributes(accessToken, attributes)
            )
        )
        return if (response.isSuccess) {
            try {
                Result.success(parse(UpdateUserAttributesResponse.serializer(), response.getOrNull()!!))
            } catch (e: SerializationException) {
                Result.failure(e)
            }
        } else {
            Result.failure(response.exceptionOrNull()!!)
        }
    }

    override suspend fun changePassword(
        accessToken: String,
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        val response = request(
            RequestType.changePassword,
            serialize(
                ChangePassword.serializer(),
                ChangePassword
                    (
                    accessToken,
                    currentPassword,
                    newPassword
                )
            )
        )
        return if (response.isSuccess) {
            Result.success(Unit)
        } else {
            Result.failure(response.exceptionOrNull()!!)
        }
    }

    override suspend fun deleteUser(accessToken: String): Result<Unit> {
        val response = request(
            RequestType.deleteUser,
            serialize(
                AccessToken.serializer(),
                AccessToken(accessToken)
            )
        )
        return if (response.isSuccess) {
            Result.success(Unit)
        } else {
            Result.failure(response.exceptionOrNull()!!)
        }
    }

    //----------
    // REQUEST
    //----------

    private suspend fun request(type: RequestType, payload: String): Result<String> {
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
                }
            )
            body = payload
        }
        return if (response.status.value == 200) {
            Result.success(String(response.readBytes()))
        } else {
            val error = parse(RequestError.serializer(), String(response.readBytes()))
            Result.failure(Error(error.message))
        }
    }
}
