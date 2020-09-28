package com.liftric.auth.jwt

import kotlinx.serialization.Serializable

/**
 * Id Token containing OIDC standard claims
 * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#StandardClaims">http://openid.net</a>
 */
interface IdToken {
    /**
     * Subject identifier
     */
    val sub: String?

    /**
     * Full name in displayable form including all name parts
     */
    val name: String?

    /**
     * Given name(s) or first name(s)
     */
    val givenName: String?

    /**
     * Surname(s) or last name(s)
     */
    val familyName: String?

    /**
     * Middle name(s)
     */
    val middleName: String?

    /**
     * Casual name that may or may not be the same as the givenName
     */
    val nickname: String?

    /**
     * Shorthand name
     */
    val preferredUsername: String?

    /**
     * URL of the profile page
     */
    val profile: String?

    /**
     * URL of the profile picture
     */
    val picture: String?

    /**
     * Web page or blog
     */
    val website: String?

    /**
     * Preferred email address
     */
    val email: String?

    /**
     * True if email has been verified
     */
    val emailVerified: Boolean?

    /**
     * Gender, either 'female' or 'male'
     */
    val gender: String?

    /**
     * Birthdate represented as an ISO 8601:2004 YYYY-MM-DD format
     */
    val birthdate: String?

    /**
     * String from zoneinfo time zone database representing the time zone
     */
    val zoneinfo: String?

    /**
     * Locale, represented as a BCP47 language tag
     */
    val locale: String?

    /**
     * Preferred telephone number
     */
    val phoneNumber: String?

    /**
     * True if phone number has been verified
     */
    val phoneNumberVerified: Boolean?

    /**
     * Preferred postal address
     */
    val address: Address?

    /**
     * Time the information was last updated
     */
    val updatedAt: Long?
}

/**
 * Address claim as specified by OIDC
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#AddressClaim">http://openid.net</a>
 */
@Serializable
data class Address (
    /**
     * Full mailing address, formatted for display or use on a mailing label
     */
    val formatted: String? = null,
    /**
     * Full street address
     */
    val streetAddress: String? = null,
    /**
     * City or locality
     */
    val locality: String? = null,
    /**
     * State, province, prefecture, or region
     */
    val region: String? = null,
    /**
     * Zip code or postal code
     */
    val postalCode: String? = null,
    /**
     * Country name
     */
    val country: String? = null
)

interface IdTokenExtension {
    /**
     * Audience
     */
    val aud: String

    /**
     * Time when the authentication occurred. JSON number that represents the number of seconds from 1970-01-01T0:0:0Z as measured in UTC format
     */
    val authTime: Long

    /**
     * List of groups the user belongs to
     */
    val cognitoGroups: List<String>

    /**
     * Username
     */
    val cognitoUsername: String

    /**
     * Expiration time
     */
    val exp: Long

    /**
     * Event id
     */
    val eventId: String

    /**
     * Issuer
     */
    val iss: String

    /**
     * Issued at
     */
    val iat: Long

    /**
     * List of Oauth 2.0 scopes that define what access the token provides
     */
    val scope: String?

    /**
     * Intended purpose of this token. Its value is always id
     */
    val tokenUse: String

    /**
     * Custom cognito attributes
     */
    val customAttributes: Map<String, String>?
}