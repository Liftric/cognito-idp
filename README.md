![CI](https://github.com/Liftric/auth/workflows/CI/badge.svg) ![Published](https://github.com/Liftric/auth/workflows/Publish%20to%20Bintray/badge.svg) ![Release](https://img.shields.io/github/v/release/liftric/auth?label=release)

# Auth

Auth is a lightweight AWS Cognito client for Kotlin Multiplatform projects

> In its current state it provides only the bare minimum that was needed for our project. Feel free to contribute if there is something missing for you.

## Import

Auth is published on Bintray.

```kotlin
repositories {
    maven { url = uri("https://dl.bintray.com/liftric/maven/") }
}
```

Then, simply add the dependencies to your sourceSets:

```kotlin
sourceSets {
    val commonMain by getting {
        dependencies {
            implementation("com.liftric:auth:<version>")
        }
    }
}
```

## How-to

### Instantiating

At first you have to instantiate the dependencies for the authentication handler class.

The handler needs a configuration object consisting of the origin url, the region code, and the client ID.

```kotlin
val configuration = Configuration(origin = "ORIGIN_URL",  
                                  region = Region.euCentral1,
                                  clientId = "CLIENT_ID") 
```

Now you have to pass the configuration object to the authentication handler via its constructor and you are good to go.

```kotlin
val authHandler = AuthHandler(configuration) 
```

### API

General usage of the request methods.

All methods are suspending and will return a `Result<T>` object which wraps the desired return object `T` and can contain an exception.

```kotlin
val signUpResponse = signUp(username = "user", password = "password")
if (signUpResponse.isSuccess) {
    println(signUpResponse.getOrNull())
    ...
} else {
    println(signUpResponse.exceptionOrNull())
}
```

#### Sign Up

You can  sign up users by providing a username, password, and optional attributes.

Returns a parsed object on success. 

```kotlin
val attribute = UserAttribute(Name = "email", Value = "email@my.tld")

suspend signUp(username = "user",
               password = "password",
               attributes = listOf(attribute)): Result<SignUpResponse>
...
```

#### Sign In

At the moment you can only sign in with username and password.

Returns a parsed object on success.

```kotlin
suspend signIn(username = "user", password = "password"): Result<SignInResponse>
```

#### Sign Out

Signs out the user and returns an error if something went wrong.

```kotlin
suspend signOut(accessToken = "TOKEN_FROM_SIGN_IN_REQUEST"): Result<SignOutResponse>
```

#### Get User

Returns the users attributes and metadata on success.

More info about this in the [official documentation](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/API_GetUser.html).

```kotlin
suspend getUser(accessToken = "TOKEN_FROM_SIGN_IN_REQUEST"): GetUserResponse
```

#### Update User Attributes

Updates the users attributes (e.g. email change).

Returns a parsed object on success.

```kotlin
suspend updateUserAttributes(accessToken = "TOKEN_FROM_SIGN_IN_REQUEST",
                             attributes = listOf(...)): Result<UpdateUserAttributesResponse>
```

#### Change Password

Updates the users password and returns an error if something went wrong.

```kotlin
suspend changePassword(accessToken = "TOKEN_FROM_SIGN_IN_REQUEST",
                       currentPassword = "OLD_PW",
                       newPassword = "NEW_PW"): Result<Unit>
```

#### Delete User

Deletes the user from the user pool and returns an error if something went wrong.

```kotlin
suspend deleteUser(accessToken = "TOKEN_FROM_SIGN_IN_REQUEST"): Result<Unit>
```

## License

Auth is available under the MIT license. See the LICENSE file for more info.
