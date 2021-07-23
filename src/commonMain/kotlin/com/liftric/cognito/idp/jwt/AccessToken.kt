package com.liftric.cognito.idp.jwt

/**
 * Access Token containing claims specified by IETF:
 * https://tools.ietf.org/html/rfc7519#section-4
 */
interface AccessToken {
    /**
     * Audience
     */
    val aud: String?

    /**
     * Expiration Time
     */
    val exp: Long

    /**
     * Issued at
     */
    val iat: Long

    /**
     * Issuer
     */
    val iss: String

    /**
     * JWT ID
     */
    val jti: String?

    /**
     * JWT origin ID
     */
    val originJti: String?

    /**
     * Not Before
     */
    val nbf: Long?

    /**
     * Subject
     */
    val sub: String
}

/**
 * Access Token extension for Cognito
 */
interface AccessTokenExtension {
    /**
     * Time when the authentication occurred. JSON number that represents the number of seconds from 1970-01-01T0:0:0Z as measured in UTC format
     */
    val authTime: Long

    /**
     * Client id
     */
    val clientId: String

    /**
     * List of groups the user belongs to
     */
    val cognitoGroups: List<String>

    /**
     * Device key
     */
    val deviceKey: String?

    /**
     * Event id
     */
    val eventId: String?

    /**
     * List of Oauth 2.0 scopes that define what access the token provides
     */
    val scope: String?

    /**
     * Intended purpose of this token. Its value is always access
     */
    val tokenUse: String

    /**
     * Username
     */
    val username: String
}