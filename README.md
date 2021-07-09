![CI](https://github.com/Liftric/auth/workflows/CI/badge.svg) 
![maven-central](https://img.shields.io/maven-central/v/com.liftric/auth?label=Maven%20Central) 
![OSS Sonatype (Releases)](https://img.shields.io/nexus/r/com.liftric/auth?label=Sonatype%20OSSRH%20%28Releases%29&server=https%3A%2F%2Fs01.oss.sonatype.org)
![npm (scoped)](https://img.shields.io/npm/v/@liftric/auth)

# Auth

Auth is a lightweight AWS Cognito Identity Provider for Kotlin Multiplatform projects.

> In its current state it provides only the bare minimum that was needed for our project. Feel free to contribute if there is something missing for you.

## Import

### Kotlin

```kotlin
sourceSets {
    val commonMain by getting {
        dependencies {
            implementation("com.liftric:auth:<version>")
        }
    }
}
```

### Typescript

#### Yarn
```bash
yarn add @liftric/auth@<version>
```
#### npm
```sh
npm i @liftric/auth@<version>
```

## How-to

### Instantiating

The provider needs a configuration object consisting of the region code and the client ID.

```kotlin
val configuration = Configuration(region = Region.euCentral1, clientId = "CLIENT_ID") 
```

#### Kotlin

```kotlin
val provider = IdentityProvider(configuration) 
```

#### Typescript

```typescript
import {IdentityProviderJS} from '@liftric/auth';

const provider = new IdentityProviderJS('<regionString>', '<clientId>');
```

### API

General usage of the request methods.

#### Kotlin

All methods are suspending and will return a `Result<T>` object which wraps the desired return object `T` and can contain an exception.

Request related exceptions are of type `IdentityProviderException` and do also contain the `HttpStatusCode`.

```kotlin
val response = provider.signUp(username = "user", password = "password")
if (response.isSuccess) {
    println(signUpResponse.getOrNull())
} else {
    println(signUpResponse.exceptionOrNull())
}
```

#### Typescript

All methods return a `Promise` that returns the desired object `T` on success.

#### Sign Up

Signs up the user.

Attributes are optional.

```kotlin
val attribute = UserAttribute(Name = "email", Value = "name@url.tld")

signUp(username = "USERNAME", password = "PASSWORD",
       attributes = listOf(attribute)): Result<SignUpResponse>
```

#### Confirm Sign Up

Confirms the sign up (also the delivery medium).

```kotlin
confirmSignUp(username = "USERNAME", confirmationCode = "CODE_FROM_DELIVERY_MEDIUM"): Result<Unit>
```

#### Sign In

Signs in the users.

```kotlin
signIn(username = "USERNAME", password = "PASSWORD"): Result<SignInResponse>
```

#### Refresh access token

Refreshes access token based on refresh token that's retrieved from an earlier sign in.

```kotlin
val signInResponse: SignInResponse = ... // from earlier login or refresh
val refreshToken = signInResponse.AuthenticationResult.RefreshToken
refresh(refreshToken = refreshToken): Result<SignInResponse>
```

#### Get Claims

You can retrieve the claims of both the IdTokens' and AccessTokens' payload by converting them to either a `CognitoIdToken` or `CognitoAccessToken`

```kotlin
val idToken = CognitoIdToken(idTokenString)
// or
val accessToken = CognitoAccessToken(accessTokenString)

val phoneNumber = idToken.claims.phoneNumber
val sub = idToken.claims.sub
```

Custom attributes of the IdToken get mapped into `customAttributes`

```kotlin
val twitter = idToken.claims.customAttributes["custom:twitter"]
```

#### Get User

Returns the users attributes and metadata on success.

More info about this in the [official documentation](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/API_GetUser.html).

```kotlin
getUser(accessToken = "TOKEN_FROM_SIGN_IN_REQUEST"): Result<GetUserResponse>
```

#### Update User Attributes

Updates the users attributes (e.g. email, phone number, ...).

```kotlin
updateUserAttributes(accessToken = "TOKEN_FROM_SIGN_IN_REQUEST",
                     attributes = listOf(...)): Result<UpdateUserAttributesResponse>
```

#### Change Password

Updates the users password 

```kotlin
changePassword(accessToken = "TOKEN_FROM_SIGN_IN_REQUEST",
               currentPassword = "OLD_PW",
               newPassword = "NEW_PW"): Result<Unit>
```

#### Forgot Password

Invokes password forgot and sends a confirmation code the the users' delivery medium.

More info about the ForgotPasswordResponse in the [official documentation](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/API_CodeDeliveryDetailsType.html).

```kotlin
forgotPassword(username = "USERNAME"): Result<ForgotPasswordResponse>
```

#### Confirm Forgot Password

Confirms forgot password.

```kotlin
confirmForgotPassword(confirmationCode = "CODE_FROM_DELIVERY_MEDIUM", username = "USERNAME", 
                      password = "NEW_PASSWORD_FROM_DELIVERY_MEDIUM"): Result<Unit>
```

#### Get user Attribute Verification Code

Gets the user attribute verification code for the specified attribute name

```kotlin
getUserAttributeVerificationCode(accessToken = "TOKEN_FROM_SIGN_IN_REQUEST", attributeName = "EMAIL", clientMetadata = null): Result<GetAttributeVerificationCodeResponse>
```

#### Verify User Attribute

Verifies the specified user attribute.

```kotlin
verifyUserAttribute(accessToken = "TOKEN_FROM_SIGN_IN_REQUEST", attributeName = "EMAIL", code = "CODE_FROM_DELIVERY_MEDIUM"): Result<Unit>
```

#### Sign Out

Signs out the user globally.

```kotlin
signOut(accessToken = "TOKEN_FROM_SIGN_IN_REQUEST"): Result<SignOutResponse>
```

#### Revoke Token

Revokes all access tokens generated by the refresh token.

```kotlin
revokeToken(refreshToken = "TOKEN_FROM_SIGN_IN_REQUEST"): Result<Unit>
```

#### Delete User

Deletes the user from the user pool.

```kotlin
deleteUser(accessToken = "TOKEN_FROM_SIGN_IN_REQUEST"): Result<Unit>
```

## Contributing

Auth is a simple kotlin project with one caveat: We're using a live Cogntio Userpool for integration tests and 
the values are provided using code generation at compile time. 

The build needs both `region` and `clientid` configured, either using our hashicorp vault cluster (obviously not accessible from the outside),
or via env var (github actions approach). `region` expects the AWS Region Code for the target region, like "us-east-1".

So if you only want to build the project, provide `region` and `clientid` env var with garbage values...

... and if you want to execute to tests yourself, you can use your own congito user pool client values.

## License

Auth is available under the MIT license. See the LICENSE file for more info.
