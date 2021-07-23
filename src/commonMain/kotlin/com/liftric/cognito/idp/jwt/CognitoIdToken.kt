package com.liftric.cognito.idp.jwt

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

class InvalidCognitoIdTokenException(message:String, cause: Throwable): Exception(message, cause)

@Serializable(with = CognitoIdTokenSerializer::class)
data class CognitoIdTokenClaims(
    override val sub: String? = null,
    override val name: String? = null,
    override val givenName: String? = null,
    override val familyName: String? = null,
    override val middleName: String? = null,
    override val nickname: String? = null,
    override val preferredUsername: String? = null,
    override val profile: String? = null,
    override val picture: String? = null,
    override val website: String? = null,
    override val email: String? = null,
    override val emailVerified: Boolean? = null,
    override val gender: String? = null,
    override val birthdate: String? = null,
    override val zoneinfo: String? = null,
    override val locale: String? = null,
    override val phoneNumber: String? = null,
    override val phoneNumberVerified: Boolean? = null,
    override val address: Address? = null,
    override val updatedAt: Long? = null,
    override val aud: String,
    override val authTime: Long,
    override val cognitoGroups: List<String>,
    override val cognitoUsername: String,
    override val exp: Long,
    override val eventId: String,
    override val jti: String? = null,
    override val originJti: String? = null,
    override val iss: String,
    override val iat: Long,
    override val scope: String? = null,
    override val tokenUse: String,
    override val customAttributes: Map<String, String>? = null
): IdToken, IdTokenExtension

class CognitoIdToken(idTokenString: String): JWT<CognitoIdTokenClaims>(idTokenString) {
    override val claims: CognitoIdTokenClaims
        get() {
            try {
                return Json.decodeFromString(CognitoIdTokenClaims.serializer(), getPayload())
            } catch (e: SerializationException) {
                throw InvalidCognitoIdTokenException("This is not a valid cognito id token", e)
            }
        }
}
