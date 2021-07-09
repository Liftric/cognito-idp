package com.liftric.auth.core

enum class RequestType(val identityProviderServiceValue: String) {
    signIn("AWSCognitoIdentityProviderService.InitiateAuth"),
    signUp ("AWSCognitoIdentityProviderService.SignUp"),
    confirmSignUp( "AWSCognitoIdentityProviderService.ConfirmSignUp"),
    signOut("AWSCognitoIdentityProviderService.GlobalSignOut"),
    revokeToken("AWSCognitoIdentityProviderService.RevokeToken"),
    getUser("AWSCognitoIdentityProviderService.GetUser"),
    changePassword("AWSCognitoIdentityProviderService.ChangePassword"),
    deleteUser("AWSCognitoIdentityProviderService.DeleteUser"),
    updateUserAttributes("AWSCognitoIdentityProviderService.UpdateUserAttributes"),
    forgotPassword("AWSCognitoIdentityProviderService.ForgotPassword"),
    confirmForgotPassword("AWSCognitoIdentityProviderService.ConfirmForgotPassword"),
    getUserAttributeVerificationCode("AWSCognitoIdentityProviderService.GetUserAttributeVerificationCode"),
    verifyUserAttribute("AWSCognitoIdentityProviderService.VerifyUserAttribute")
}
