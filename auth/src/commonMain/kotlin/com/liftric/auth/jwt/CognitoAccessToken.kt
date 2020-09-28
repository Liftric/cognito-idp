package com.liftric.auth.jwt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class InvalidCognitoAccessTokenException(message:String): Exception(message)

@Serializable
data class CognitoAccessTokenClaims(
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
): AccessToken, AccessTokenExtenstion

class CognitoAccessToken(accessTokenString: String): JWT<CognitoAccessTokenClaims>(accessTokenString) {
    override val claims: CognitoAccessTokenClaims
        get() {
            try {
                return Json.decodeFromString(CognitoAccessTokenClaims.serializer(), getPayload())
            } catch (e: Exception) {
                throw InvalidCognitoAccessTokenException("This is not a valid access token")
            }
        }
}