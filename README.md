![CI](https://github.com/Liftric/auth/workflows/CI/badge.svg) ![Published](https://github.com/Liftric/auth/workflows/Publish%20to%20Bintray/badge.svg) ![Version](https://img.shields.io/github/v/release/liftric/auth?label=version)

# Auth

Auth is a simple AWS Cognito authentication client. 

> In its current state it provides only the bare minimum that was needed for our project. Feel free to contribute if there is something missing for you.

## Import

Auth is published on Bintray and mirrored to JCenter. Set at least one of the two repos.

```kotlin
repositories {
    maven { url = uri("https://dl.bintray.com/liftric/maven/") }
    jcenter()
}
```

Then, simply add the dependencies to your sourceSets:

```kotlin
sourceSets {
    val commonMain by getting {
        dependencies {
            implementation("com.liftric:auth-common:<version>")   
        }
    }
    val androidMain by getting {
        dependencies {
             implementation("com.liftric:auth-android:<version>")   
        }
    }
    val iosMain by getting {
        dependencies {
            implementation("com.liftric:auth-ios:<version>")   
        }
    }
}
```

## How-to

### Instantiating

At first you have to instantiate the dependencies for the authentication handler class.

The handler needs a configuration object consisting of the origin url, the region code, and the client ID.

```kotlin
val configuration = Configuration(origin: "ORIGIN_URL",  
                                  region: Region.euCentral1,
                                  clientId: "CLIENT_ID") 
```

Now you have to provide the settings- and secretstore (Needed to store the tokens and credentials).

#### Android

```kotlin
val secretStore = SecretStore(context = Context)
val settingsStore = SettingsStore(context = Context)
```

#### iOS

```kotlin
val secretStore = SecretStore()
val settingsStore = SettingsStore()
```

Since all dependencies are instantiated you can pass them to the authentication handler via its constructor and you are good to go.

```kotlin
val authHandler = AuthHandler(configuration, settingsStore, secretStore) 
```

### API

General usage of the request methods.

All methods will return an optional error and the response value (JSON string). In case of a failure you can access the errors message and the response value will be null.

```kotlin
signUp(username = "user", password = "password") { error, value ->
    error?.let {
        println(error.message)
        return
    }
    
    val jsonString = value
    ...
}
```

#### Sign Up

You can  sign up users by providing a username, password, and optional attributes. 

The credentials will be secured in the iOS keychain or with encrypted SharedPreferences.

```kotlin
val attribute = UserAttribute(Name: "email", Value: "email@my.tld")

signUp(username = "user", password = "password", attributes = listOf(attribute)) { error, value ->
    ...
}
```

#### Sign In

At the moment you can only sign in with username and password.

The response contains an access token and an expiration time which will be stored in the iOS UserDefaults or in the SharedPreferences.

```kotlin
signIn(username = "user", password = "password") { error, value ->
    ...
}
```

#### Sign Out

Signs out the user.

Removes the stored credentials and the token from the stores.

```kotlin
signOut() { error, value ->
    ...
}
```

#### Get User

Returns the users attributes and metadata.

More info about this in the [official documentation](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/API_GetUser.html).

```kotlin
getUser() { error, value ->
    ...
}
```

#### Update User Attributes

Updates the users attributes (e.g. email change).

```kotlin
updateUserAttributes(attributes = listOf(...)) { error, value ->
    ...
}
```

#### Change Password

Updates the users password and also updates the stored value in the secretstore. 

```kotlin
changePassword(toNewPassword = "SomethingSecure42") { error, value ->
    ...
}
```

#### Delete User

Deletes the user from the user pool and clears all data from the settings- and secretstore. 

```kotlin
deleteUser() { error, value ->
    ...
}
```

## License

Auth is available under the MIT license. See the LICENSE file for more info.
