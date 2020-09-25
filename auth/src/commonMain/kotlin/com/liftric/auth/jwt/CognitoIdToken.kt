package com.liftric.auth.jwt

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

interface IdTokenExtenstion {
    val aud: String
    val authTime: Long
    val cognitoGroups: List<String>
    val cognitoUsername: String
    val exp: Long
    val eventId: String
    val iss: String
    val iat: Long
    val scope: String?
    val tokenUse: String
    val custom: Map<String, String>?
}

@Serializable
data class CognitoIdToken(
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
    override val iss: String,
    override val iat: Long,
    override val scope: String? = null,
    override val tokenUse: String,
    override val custom: Map<String, String>? = null
): IdToken, IdTokenExtenstion {
    fun toJsonString(): String {
        return Json.encodeToString(serializer(), this)
    }
}