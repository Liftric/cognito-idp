package com.liftric.auth.jwt

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializer(forClass = CognitoIdToken::class)
internal object CustomAttributesSerializer : KSerializer<CognitoIdTokenClaims> {
    private val stringToJsonElementSerializer = MapSerializer(String.serializer(), JsonElement.serializer())

    override val descriptor: SerialDescriptor = stringToJsonElementSerializer.descriptor

    override fun deserialize(decoder: Decoder): CognitoIdTokenClaims {
        require(decoder is JsonDecoder)
        val json = decoder.json
        val filtersMap = decoder.decodeSerializableValue(stringToJsonElementSerializer)

        val address: Address? = filtersMap["address"]?.let {
            json.decodeFromJsonElement(it)
        }
        val birthdate: String? = filtersMap["birthdate"]?.let {
            json.decodeFromJsonElement(it)
        }
        val email: String? = filtersMap["email"]?.let {
            json.decodeFromJsonElement(it)
        }
        val emailVerified: Boolean? = filtersMap["email_verified"]?.let {
            json.decodeFromJsonElement(it)
        }
        val familyName: String? = filtersMap["family_name"]?.let {
            json.decodeFromJsonElement(it)
        }
        val middleName: String? = filtersMap["middle_name"]?.let {
            json.decodeFromJsonElement(it)
        }
        val gender: String? = filtersMap["gender"]?.let {
            json.decodeFromJsonElement(it)
        }
        val givenName: String? = filtersMap["given_name"]?.let {
            json.decodeFromJsonElement(it)
        }
        val locale: String? = filtersMap["locale"]?.let {
            json.decodeFromJsonElement(it)
        }
        val name: String? = filtersMap["name"]?.let {
            json.decodeFromJsonElement(it)
        }
        val nickname: String? = filtersMap["nickname"]?.let {
            json.decodeFromJsonElement(it)
        }
        val phoneNumber: String? = filtersMap["phone_number"]?.let {
            json.decodeFromJsonElement(it)
        }
        val phoneNumberVerified: Boolean? = filtersMap["phone_number_verified"]?.let {
            json.decodeFromJsonElement(it)
        }
        val picture: String? = filtersMap["picture"]?.let {
            json.decodeFromJsonElement(it)
        }
        val profile: String? = filtersMap["profile"]?.let {
            json.decodeFromJsonElement(it)
        }
        val preferredUsername: String? = filtersMap["preferred_username"]?.let {
            json.decodeFromJsonElement(it)
        }
        val sub: String? = filtersMap["sub"]?.let {
            json.decodeFromJsonElement(it)
        }
        val aud: String = filtersMap["aud"]?.let {
            json.decodeFromJsonElement(it)
        }?: run {
            throw SerializationException(missingField("aud"))
        }
        val authTime: Long = filtersMap["auth_time"]?.let {
            json.decodeFromJsonElement(it)
        }?: run {
            throw SerializationException(missingField("auth_time"))
        }
        val cognitoGroups: List<String> = filtersMap["cognito:groups"]?.let {
            json.decodeFromJsonElement(it)
        }?: run {
            throw SerializationException(missingField("cognito:groups"))
        }
        val cognitoUsername: String = filtersMap["cognito:username"]?.let {
            json.decodeFromJsonElement(it)
        }?: run {
            throw SerializationException(missingField("cognito:username"))
        }
        val exp: Long = filtersMap["exp"]?.let {
            json.decodeFromJsonElement(it)
        }?: run {
            throw SerializationException(missingField("exp"))
        }
        val eventId: String = filtersMap["event_id"]?.let {
            json.decodeFromJsonElement(it)
        }?: run {
            throw SerializationException(missingField("event_id"))
        }
        val jti: String? = filtersMap["jti"]?.let {
            json.decodeFromJsonElement(it)
        }
        val originJti: String? = filtersMap["origin_jti"]?.let {
            json.decodeFromJsonElement(it)
        }
        val iss: String = filtersMap["iss"]?.let {
            json.decodeFromJsonElement(it)
        }?: run {
            throw SerializationException(missingField("iss"))
        }
        val zoneinfo: String? = filtersMap["zoneinfo"]?.let {
            json.decodeFromJsonElement(it)
        }
        val iat: Long = filtersMap["iat"]?.let {
            json.decodeFromJsonElement(it)
        }?: run {
            throw SerializationException(missingField("iat"))
        }
        val scope: String? = filtersMap["scope"]?.let {
            json.decodeFromJsonElement(it)
        }
        val tokenUse: String = filtersMap["token_use"]?.let {
            json.decodeFromJsonElement(it)
        }?: run {
            throw SerializationException(missingField("token_use"))
        }
        val website: String? = filtersMap["website"]?.let {
            json.decodeFromJsonElement(it)
        }
        val updatedAt: Long? = filtersMap["updated_at"]?.let {
            json.decodeFromJsonElement(it)
        }
        val customAttributes = filtersMap.filter { (key, _) ->
            key.contains("custom")
        }.map { (key, value) ->
            key to if (value.jsonPrimitive.isString) {
                json.decodeFromJsonElement(String.serializer(), value)
            } else {
                json.decodeFromJsonElement(Long.serializer(), value).toString()
            }
        }.toMap()

        return CognitoIdTokenClaims(
            sub,
            name,
            givenName,
            familyName,
            middleName,
            nickname,
            preferredUsername,
            profile,
            picture,
            website,
            email,
            emailVerified,
            gender,
            birthdate,
            zoneinfo,
            locale,
            phoneNumber,
            phoneNumberVerified,
            address,
            updatedAt,
            aud,
            authTime,
            cognitoGroups,
            cognitoUsername,
            exp,
            eventId,
            jti,
            originJti,
            iss,
            iat,
            scope,
            tokenUse,
            customAttributes
        )
    }
    override fun serialize(encoder: Encoder, value: CognitoIdTokenClaims) {
        require(encoder is JsonEncoder)
        val json = encoder.json

        val map: MutableMap<String, JsonElement> = mutableMapOf()
        value.sub?.let { map["name"] = json.encodeToJsonElement(it) }
        value.name?.let { map["name"] = json.encodeToJsonElement(it) }
        value.givenName?.let { map["given_name"] = json.encodeToJsonElement(it) }
        value.familyName?.let { map["family_name"] = json.encodeToJsonElement(it) }
        value.middleName?.let { map["middle_name"] = json.encodeToJsonElement(it) }
        value.nickname?.let { map["nickname"] = json.encodeToJsonElement(it) }
        value.preferredUsername?.let { map["preferred_username"] = json.encodeToJsonElement(it) }
        value.profile?.let { map["profile"] = json.encodeToJsonElement(it) }
        value.picture?.let { map["picture"] = json.encodeToJsonElement(it) }
        value.website?.let { map["website"] = json.encodeToJsonElement(it) }
        value.email?.let { map["email"] = json.encodeToJsonElement(it) }
        value.emailVerified?.let { map["email_verified"] = json.encodeToJsonElement(it) }
        value.gender?.let { map["gender"] = json.encodeToJsonElement(it) }
        value.birthdate?.let { map["birthdate"] = json.encodeToJsonElement(it) }
        value.zoneinfo?.let { map["zoneinfo"] = json.encodeToJsonElement(it) }
        value.locale?.let { map["locale"] = json.encodeToJsonElement(it) }
        value.phoneNumber?.let { map["phone_number"] = json.encodeToJsonElement(it) }
        value.phoneNumberVerified?.let { map["phone_number_verified"] = json.encodeToJsonElement(it) }
        value.address?.let { map["address"] = json.encodeToJsonElement(it) }
        value.updatedAt?.let { map["updated_at"] = json.encodeToJsonElement(it) }
        value.aud.let { map["aud"] = json.encodeToJsonElement(it) }
        value.authTime.let { map["auth_time"] = json.encodeToJsonElement(it) }
        value.cognitoGroups.let { map["cognito:groups"] = json.encodeToJsonElement(it) }
        value.cognitoUsername.let { map["cognito:username"] = json.encodeToJsonElement(it) }
        value.exp.let { map["exp"] = json.encodeToJsonElement(it) }
        value.eventId.let { map["event_id"] = json.encodeToJsonElement(it) }
        value.jti?.let { map["jti"] = json.encodeToJsonElement(it) }
        value.originJti?.let { map["origin_jti"] = json.encodeToJsonElement(it) }
        value.iss.let { map["iss"] = json.encodeToJsonElement(it) }
        value.iat.let { map["iat"] = json.encodeToJsonElement(it) }
        value.scope?.let { map["scope"] = json.encodeToJsonElement(it) }
        value.tokenUse.let { map["token_user"] = json.encodeToJsonElement(it) }
        value.customAttributes?.forEach { map[it.key] = json.encodeToJsonElement(it.value) }

        encoder.encodeSerializableValue(stringToJsonElementSerializer, map)
    }

    private fun missingField(name: String): String {
        return "Missing field $name"
    }
}