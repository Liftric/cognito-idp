package com.liftric.auth.jwt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface AccessTokenExtenstion {
    val authTime: Long
    val clientId: String
    val cognitoGroups: List<String>
    val deviceKey: String
    val email: String
    val emailVerified: Boolean
    val eventId: String
    val scope: String
    val tokenUse: String
    val username: String
}

@Serializable
data class CognitoAccessToken(
    override val aud: String,
    override val exp: Long,
    override val iat: Long,
    override val iss: String,
    override val jti: String,
    override val nbf: Long,
    override val sub: String,
    @SerialName("auth_time")
    override val authTime: Long,
    @SerialName("client_id")
    override val clientId: String,
    @SerialName("cognito:groups")
    override val cognitoGroups: List<String>,
    @SerialName("device_key")
    override val deviceKey: String,
    override val email: String,
    @SerialName("email_verified")
    override val emailVerified: Boolean,
    @SerialName("event_id")
    override val eventId: String,
    override val scope: String,
    @SerialName("token_use")
    override val tokenUse: String,
    override val username: String
): AccessToken, AccessTokenExtenstion {
    fun toJsonString(): String {
        return Json.encodeToString(serializer(), this)
    }
}