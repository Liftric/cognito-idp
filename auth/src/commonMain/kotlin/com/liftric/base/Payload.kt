package com.liftric.base

import kotlinx.serialization.Serializable

@Serializable
internal data class Authentication(
    val AuthFlow: String,
    val ClientId: String,
    val AuthParameters: AuthParameters
)

@Serializable
internal data class AuthParameters(
    val USERNAME: String,
    val PASSWORD: String
)

@Serializable
internal data class AccessToken(
    val AccessToken: String
)

@Serializable
internal data class ChangePassword(
    val AccessToken: String,
    val PreviousPassword: String,
    val ProposedPassword: String
)

@Serializable
internal data class SignUp(
    val ClientId: String,
    val Password: String,
    val Username: String,
    val UserAttributes: List<UserAttribute> = listOf()
)

@Serializable
data class UserAttribute(
    val Name: String = "",
    val Value: String = ""
)

@Serializable
internal data class UpdateUserAttributes(
    val AccessToken: String,
    val UserAttributes: List<UserAttribute>
)