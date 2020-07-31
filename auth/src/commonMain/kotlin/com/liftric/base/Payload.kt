package com.liftric.base

import kotlinx.serialization.Serializable

@Serializable
data class Authentication(
    val AuthFlow: String,
    val ClientId: String,
    val AuthParameters: AuthParameters
)

@Serializable
data class AuthParameters(
    val USERNAME: String,
    val PASSWORD: String
)

@Serializable
data class AccessToken(
    val AccessToken: String
)

@Serializable
data class ChangePassword(
    val AccessToken: String,
    val PreviousPassword: String,
    val ProposedPassword: String
)

@Serializable
data class SignUp(
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
data class UpdateUserAttributes(
    val AccessToken: String,
    val UserAttributes: List<UserAttribute>
)