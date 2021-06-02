package com.liftric.auth.base

object Header {
    const val Authority         = "authority"
    const val CacheControl      = "cache-control"
    const val AmzUserAgent      = "x-amz-user-agent"
    const val Useragent         = "user-agent"
    const val Accept            = "accept"
    const val Origin            = "origin"
    const val SecFetchSite      = "sec-fetch-site"
    const val SecFetchMode      = "sec-fetch-mode"
    const val SecFetchDest      = "sec-fetch-dest"
    const val AcceptLanguage    = "accept-language"
    const val Dnt               = "dnt"
    const val AmzTarget         = "x-amz-target"
    const val AmzJson           = "application/x-amz-json-1.1"
}

object AuthFlow {
    const val RefreshTokenAuth  = "REFRESH_TOKEN_AUTH"
    const val UserSrpAuth       = "USER_SRP_AUTH"
    const val UserPasswordAuth  = "USER_PASSWORD_AUTH"
}

enum class Region(val code: String) {
    usEast1("us-east-1"),
    usEast2("us-east-2"),
    usWest1("us-west-1"),
    usWest2("us-west-2"),
    afSouth1("af-south-1"),
    apEast1("ap-east-1"),
    apSouth1("ap-south-1"),
    apNortheast3("ap-northeast-3"),
    apNortheast2("ap-northeast-2"),
    apSoutheast1("ap-southeast-1"),
    apSoutheast2("ap-southeast-2"),
    apNortheast1("ap-northeast-1"),
    caCentral1("ca-central-1"),
    cnNorth1("cn-north-1"),
    cnNorthwest1("cn-northwest-1"),
    euCentral1("eu-central-1"),
    euWest1("eu-west-1"),
    euWest2("eu-west-2"),
    euSouth1("eu-south-1"),
    euWest3("eu-west-3"),
    euNorth1("eu-north-1"),
    meSouth1("me-south-1"),
    saEast1("sa-east-1")
}

object CognitoException {
    const val UserNotFound  = "UserNotFoundException"
    const val NotAuthorized = "NotAuthorizedException"
}
