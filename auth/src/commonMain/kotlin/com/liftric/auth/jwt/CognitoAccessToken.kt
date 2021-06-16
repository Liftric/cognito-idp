package com.liftric.auth.jwt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class InvalidCognitoAccessTokenException(message: String, cause: Throwable): Exception(message, cause)

@Serializable
data class CognitoAccessTokenClaims(
    override val aud: String? = null,
    override val exp: Long,
    override val iat: Long,
    override val iss: String,
    override val jti: String? = null,
    @SerialName("origin_jti")
    override val originJti: String? = null,
    override val nbf: Long? = null,
    override val sub: String,
    @SerialName("auth_time")
    override val authTime: Long,
    @SerialName("client_id")
    override val clientId: String,
    @SerialName("cognito:groups")
    override val cognitoGroups: List<String>,
    @SerialName("device_key")
    override val deviceKey: String? = null,
    @SerialName("event_id")
    override val eventId: String? = null,
    override val scope: String? = null,
    @SerialName("token_use")
    override val tokenUse: String,
    override val username: String
): AccessToken, AccessTokenExtension

class CognitoAccessToken(accessTokenString: String): JWT<CognitoAccessTokenClaims>(accessTokenString) {
    override val claims: CognitoAccessTokenClaims
        get() {
            try {
                return json.decodeFromString(CognitoAccessTokenClaims.serializer(), getPayload())
            } catch (e: Exception) {
                throw InvalidCognitoAccessTokenException("This is not a valid access token", e)
            }
        }
    companion object {
        private val json by lazy {
            Json { ignoreUnknownKeys = true }
        }
    }
}