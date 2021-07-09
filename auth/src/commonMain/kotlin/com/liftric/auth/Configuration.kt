package com.liftric.auth

import com.liftric.auth.core.Header
import com.liftric.auth.core.Region
import io.ktor.http.HeadersBuilder

/**
 * Configuration object for the auth handler
 * Holds all headers needed to make requests to AWS Cognito
 */
class Configuration(region: Region, val clientId: String) {

    /**
     * For compatibility reasons this constructor is needed, previous Auth versions set the Origin header
     */
    @Deprecated("origin isn't required, please adapt to the primary constructor")
    constructor(origin: String, region: Region, clientId: String) : this(region,clientId) {
        println("Configuration: origin isn't required, please adapt to the primary constructor")
    }

    internal val requestUrl = "https://cognito-idp.${region.code}.amazonaws.com"

    private val headers = mapOf(
        Header.Authority to requestUrl,
        Header.CacheControl to "max-age=0",
        Header.AmzUserAgent to "aws-amplify/0.1.x js",
        Header.Useragent to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36",
        Header.Accept to "*/*",
        Header.SecFetchSite to "cross-site",
        Header.SecFetchMode to "cors",
        Header.AcceptLanguage to "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7,cs;q=0.6,es;q=0.5,da;q=0.4,ru;q=0.3",
        Header.Dnt to "1"
    )

    /**
     * Appends configuration headers to HeadersBuilder
     */
    fun setupDefaultRequest(builder: HeadersBuilder) {
        headers.forEach {
            builder.append(it.key, it.value)
        }
    }
}
