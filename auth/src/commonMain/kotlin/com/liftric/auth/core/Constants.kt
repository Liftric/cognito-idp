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

internal enum class Request(val value: String) {
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

enum class AWSException(val description: String) {
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
