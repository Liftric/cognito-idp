package com.liftric.auth.core

internal object Header {
    const val Authority = "authority"
    const val CacheControl = "cache-control"
    const val AmzUserAgent = "x-amz-user-agent"
    const val Useragent = "user-agent"
    const val Accept = "accept"
    const val SecFetchSite = "sec-fetch-site"
    const val SecFetchMode = "sec-fetch-mode"
    const val AcceptLanguage = "accept-language"
    const val Dnt = "dnt"
    const val AmzTarget = "x-amz-target"
    const val AmzJson = "application/x-amz-json-1.1"
}

internal enum class Authentication(val flow: String) {
    RefreshTokenAuth("REFRESH_TOKEN_AUTH"),
    UserPasswordAuth("USER_PASSWORD_AUTH")
}

/**
 * @see https://docs.aws.amazon.com/general/latest/gr/rande.html#regional-endpoints
 */
enum class Region(val code: String) {
    USEast1("us-east-1"),
    USEast2("us-east-2"),
    USWest1("us-west-1"),
    USWest2("us-west-2"),
    AFSouth1("af-south-1"),
    APEast1("ap-east-1"),
    APSouth1("ap-south-1"),
    APNortheast3("ap-northeast-3"),
    APNortheast2("ap-northeast-2"),
    APSoutheast1("ap-southeast-1"),
    APSoutheast2("ap-southeast-2"),
    APNortheast1("ap-northeast-1"),
    CACentral1("ca-central-1"),
    CNNorth1("cn-north-1"),
    CNNorthwest1("cn-northwest-1"),
    EUCentral1("eu-central-1"),
    EUWest1("eu-west-1"),
    EUWest2("eu-west-2"),
    EUSouth1("eu-south-1"),
    EUWest3("eu-west-3"),
    EUNorth1("eu-north-1"),
    MESouth1("me-south-1"),
    SAEast1("sa-east-1")
}

internal enum class Request(val identityProviderServiceValue: String) {
    SignIn("AWSCognitoIdentityProviderService.InitiateAuth"),
    SignUp ("AWSCognitoIdentityProviderService.SignUp"),
    ConfirmSignUp( "AWSCognitoIdentityProviderService.ConfirmSignUp"),
    SignOut("AWSCognitoIdentityProviderService.GlobalSignOut"),
    RevokeToken("AWSCognitoIdentityProviderService.RevokeToken"),
    GetUser("AWSCognitoIdentityProviderService.GetUser"),
    ChangePassword("AWSCognitoIdentityProviderService.ChangePassword"),
    DeleteUser("AWSCognitoIdentityProviderService.DeleteUser"),
    UpdateUserAttributes("AWSCognitoIdentityProviderService.UpdateUserAttributes"),
    ForgotPassword("AWSCognitoIdentityProviderService.ForgotPassword"),
    ConfirmForgotPassword("AWSCognitoIdentityProviderService.ConfirmForgotPassword"),
    GetUserAttributeVerificationCode("AWSCognitoIdentityProviderService.GetUserAttributeVerificationCode"),
    VerifyUserAttribute("AWSCognitoIdentityProviderService.VerifyUserAttribute")
}

enum class AWSException(val identifier: String) {
     CodeMismatch("CodeMismatchException"),
     ExpiredCode("ExpiredCodeException"),
     InternalError("InternalErrorException"),
     InvalidLambdaResponse("InvalidLambdaResponseException"),
     InvalidParameter("InvalidParameterException"),
     InvalidPassword("InvalidPasswordException"),
     LimitExceeded("LimitExceededException"),
     NotAuthorized("NotAuthorizedException"),
     ResourceNotFound("ResourceNotFoundException"),
     TooManyFailedAttempts("TooManyFailedAttemptsException"),
     TooManyRequests("TooManyRequestsException"),
     UnexpectedLambda("UnexpectedLambdaException"),
     UserLambdaValidation("UserLambdaValidationException"),
     UserNotConfirmed("UserNotConfirmedException"),
     UserNotFound("UserNotFoundException")
}
