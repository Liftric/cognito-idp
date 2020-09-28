package com.liftric.auth.jwt

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
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

        val address = filtersMap["address"]?.let {
            json.decodeFromJsonElement(Address.serializer(), it)
        }
        val birthdate = filtersMap["birthdate"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }
        val email = filtersMap["email"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }
        val emailVerified = filtersMap["email_verified"]?.let {
            json.decodeFromJsonElement(Boolean.serializer(), it)
        }
        val familyName = filtersMap["family_name"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }
        val middleName = filtersMap["middle_name"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }
        val gender = filtersMap["gender"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }
        val givenName = filtersMap["given_name"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }
        val locale = filtersMap["locale"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }
        val name = filtersMap["name"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }
        val nickname = filtersMap["nickname"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }
        val phoneNumber = filtersMap["phone_number"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }
        val phoneNumberVerified = filtersMap["phone_number_verified"]?.let {
            json.decodeFromJsonElement(Boolean.serializer(), it)
        }
        val picture = filtersMap["picture"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }
        val profile = filtersMap["profile"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }
        val preferredUsername = filtersMap["preferred_username"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }
        val sub = filtersMap["sub"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }
        val aud = filtersMap["aud"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }?: run {
            throw SerializationException("Missing field aud")
        }
        val authTime = filtersMap["auth_time"]?.let {
            json.decodeFromJsonElement(Long.serializer(), it)
        }?: run {
            throw SerializationException("Missing field auth_time")
        }
        val cognitoGroups = filtersMap["cognito:groups"]?.let {
            json.decodeFromJsonElement(ListSerializer(String.serializer()), it)
        }?: run {
            throw SerializationException("Missing field cognito:groups")
        }
        val cognitoUsername = filtersMap["cognito:username"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }?: run {
            throw SerializationException("Missing field cognito:username")
        }
        val exp = filtersMap["exp"]?.let {
            json.decodeFromJsonElement(Long.serializer(), it)
        }?: run {
            throw SerializationException("Missing field exp")
        }
        val eventId = filtersMap["event_id"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }?: run {
            throw SerializationException("Missing field event_id")
        }
        val iss = filtersMap["iss"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }?: run {
            throw SerializationException("Missing field iss")
        }
        val zoneinfo = filtersMap["zoneinfo"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }
        val iat = filtersMap["iat"]?.let {
            json.decodeFromJsonElement(Long.serializer(), it)
        }?: run {
            throw SerializationException("Missing field iat")
        }
        val scope = filtersMap["scope"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }
        val tokenUse = filtersMap["token_use"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }?: run {
            throw SerializationException("Missing field token_use")
        }
        val website = filtersMap["website"]?.let {
            json.decodeFromJsonElement(String.serializer(), it)
        }
        val updatedAt = filtersMap["updated_at"]?.let {
            json.decodeFromJsonElement(Long.serializer(), it)
        }
        val unknownFilters = filtersMap.filter { (key, _) -> key.contains("custom") }
        val customAttributes = mutableMapOf<String, String>()
        unknownFilters.forEach {
            val value = if (it.value.jsonPrimitive.isString) {
                json.decodeFromJsonElement(String.serializer(), it.value)
            } else {
                json.decodeFromJsonElement(Long.serializer(), it.value).toString()
            }
            customAttributes[it.key] = value
        }

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

        value.sub?.let { map["name"] = json.encodeToJsonElement(String.serializer(), it) }
        value.name?.let { map["name"] = json.encodeToJsonElement(String.serializer(), it) }
        value.givenName?.let { map["given_name"] = json.encodeToJsonElement(String.serializer(), it) }
        value.familyName?.let { map["family_name"] = json.encodeToJsonElement(String.serializer(), it) }
        value.middleName?.let { map["middle_name"] = json.encodeToJsonElement(String.serializer(), it) }
        value.nickname?.let { map["nickname"] = json.encodeToJsonElement(String.serializer(), it) }
        value.preferredUsername?.let { map["preferred_username"] = json.encodeToJsonElement(String.serializer(), it) }
        value.profile?.let { map["profile"] = json.encodeToJsonElement(String.serializer(), it) }
        value.picture?.let { map["picture"] = json.encodeToJsonElement(String.serializer(), it) }
        value.website?.let { map["website"] = json.encodeToJsonElement(String.serializer(), it) }
        value.email?.let { map["email"] = json.encodeToJsonElement(String.serializer(), it) }
        value.emailVerified?.let { map["email_verified"] = json.encodeToJsonElement(Boolean.serializer(), it) }
        value.gender?.let { map["gender"] = json.encodeToJsonElement(String.serializer(), it) }
        value.birthdate?.let { map["birthdate"] = json.encodeToJsonElement(String.serializer(), it) }
        value.zoneinfo?.let { map["zoneinfo"] = json.encodeToJsonElement(String.serializer(), it) }
        value.locale?.let { map["locale"] = json.encodeToJsonElement(String.serializer(), it) }
        value.phoneNumber?.let { map["phone_number"] = json.encodeToJsonElement(String.serializer(), it) }
        value.phoneNumberVerified?.let { map["phone_number_verified"] = json.encodeToJsonElement(Boolean.serializer(), it) }
        value.address?.let { map["address"] = json.encodeToJsonElement(Address.serializer(), it) }
        value.updatedAt?.let { map["updated_at"] = json.encodeToJsonElement(Long.serializer(), it) }
        value.aud.let { map["aud"] = json.encodeToJsonElement(String.serializer(), it) }
        value.authTime.let { map["auth_time"] = json.encodeToJsonElement(Long.serializer(), it) }
        value.cognitoGroups.let { map["cognito:groups"] = json.encodeToJsonElement(ListSerializer(String.serializer()), it) }
        value.cognitoUsername.let { map["cognito:username"] = json.encodeToJsonElement(String.serializer(), it) }
        value.exp.let { map["exp"] = json.encodeToJsonElement(Long.serializer(), it) }
        value.eventId.let { map["event_id"] = json.encodeToJsonElement(String.serializer(), it) }
        value.iss.let { map["iss"] = json.encodeToJsonElement(String.serializer(), it) }
        value.iat.let { map["iat"] = json.encodeToJsonElement(Long.serializer(), it) }
        value.scope?.let { map["scope"] = json.encodeToJsonElement(String.serializer(), it) }
        value.tokenUse.let { map["token_user"] = json.encodeToJsonElement(String.serializer(), it) }
        value.customAttributes?.forEach {
            map[it.key] = json.encodeToJsonElement(String.serializer(), it.value)
        }

        encoder.encodeSerializableValue(stringToJsonElementSerializer, map)
    }
}