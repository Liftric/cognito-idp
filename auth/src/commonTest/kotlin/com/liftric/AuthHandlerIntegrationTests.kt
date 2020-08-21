package com.liftric

import com.liftric.base.*
import com.liftric.base.Environment
import kotlinx.coroutines.delay
import kotlin.test.*
import kotlinx.coroutines.*
import kotlin.js.JsName

class AuthHandlerIntegrationTests() {
    private val configuration = Configuration(
        Environment.variable("origin") ?: "",
        Region.euCentral1,
        Environment.variable("clientid") ?: ""
    )

    // Randomize temp user account name to not exceed aws try threshold
    private val random = (0..999).random()
    private val username = "auth-lib-test-user-${random}"
    private val password = "auth-lib-test-user-${random}A1@"

    private var authHandler = AuthHandler(configuration)

    //-------------------
    // INTEGRATION TESTS
    //-------------------

    data class Credential(val username: String, val password: String)

    private fun randomUser(): Credential {
        val random = (0..999).random()
        return Credential(
            username = "auth-lib-test-user-${random}",
            password = "auth-lib-test-user-${random}A1@"
        )
    }

    private suspend fun createUser(block: (accessToken: String, credential: Credential) -> Unit) {
        val credential = randomUser()
        val userAttributes: List<UserAttribute> = listOf(
            UserAttribute(
                Name = "custom:target_group",
                Value = "ROLE_PATIENT"
            )
        )
        authHandler.signUp(credential.username, credential.password, userAttributes) { error, value ->
            assertNull(error)
            assertNotNull(value)
        }
        authHandler.signIn(credential.username, credential.password) { _, value ->
            assertNotNull(value)
            block(value.AuthenticationResult.AccessToken, credential)
        }
    }

    private suspend fun deleteUser(token: String) {
        authHandler.deleteUser(token) { error ->
            assertNull(error)
        }
    }

    @JsName("SignUpSignInDeleteUserTest")
    @Test
    fun `Sign up, sign in, delete user should succeed`() = runBlocking {
        authHandler.signUp(
            username, password,
            attributes = listOf(
                UserAttribute(Name = "email", Value = "test@test.test"),
                UserAttribute(Name = "custom:target_group", Value = "ROLE_USER")
            )
        ) { error, value ->
            assertNull(error)
            assertNotNull(value)
        }
        var accessToken: String = ""
        authHandler.signIn(username, password) { signInError, signInValue ->
            assertNull(signInError)
            assertNotNull(signInValue)
            accessToken = signInValue.AuthenticationResult.AccessToken
        }
        try {
            authHandler.deleteUser(accessToken) { deleteError ->
                assertNull(deleteError)
            }
        } catch (error: Error) {
            fail(error.message)
        }
    }

    @JsName("GetUserTest")
    @Test
    fun `Should get user`() = runBlocking {
        var token: String = ""
        createUser { t, _ ->
            token = t
        }
        authHandler.getUser(token) { error, value ->
            assertNull(error)
            assertNotNull(value)
        }
        deleteUser(token)
    }

    @JsName("ChangeEmailTest")
    @Test
    fun `Should change email`() = runBlocking {
        var token: String = ""
        createUser { t, _ ->
            token = t
        }
        authHandler.updateUserAttributes(
            token,
            listOf(UserAttribute(Name = "email", Value = "test2@test.test"))
        ) { error, value ->
            assertNull(error)
            assertNotNull(value)
        }
        authHandler.getUser(token) { getUserError, getUserValue ->
            assertNull(getUserError)
            assertNotNull(getUserValue)

            getUserValue.UserAttributes.map { attribute ->
                if (attribute.Name == "email") {
                    assertEquals("test2@test.test", attribute.Value)
                }
            }
        }
        deleteUser(token)
    }

    @JsName("ChangePasswordTest")
    @Test
    fun `Should change password`() = runBlocking {
        var token: String = ""
        var credential: Credential = Credential("", "")
        createUser { t, c ->
            token = t
            credential = c
        }
        authHandler.changePassword(token, credential.password, credential.password + "B") { error ->
            assertNull(error)
        }
        authHandler.signOut(token) { signOutError ->
            assertNull(signOutError)
        }
        // AWS is not revoking Tokens automatically so give it some time
        delay(1000)
        var accessToken: String = ""
        authHandler.signIn(credential.username, credential.password + "B") { signInError, signInValue ->
            assertNull(signInError)
            assertNotNull(signInValue)
            accessToken = signInValue.AuthenticationResult.AccessToken
        }
        deleteUser(accessToken)
    }

    @JsName("SignOutSignInTest")
    @Test
    fun `Sign out and sign in should succeed`() = runBlocking {
        var token: String = ""
        var credential: Credential = Credential("", "")
        createUser { t, c ->
            token = t
            credential = c
        }
        authHandler.signOut(token) { error ->
            assertNull(error)
        }
        // AWS is not revoking Tokens automatically so give it some time
        delay(1000)
        var accessToken: String = ""
        authHandler.signIn(credential.username, credential.password) { signInError, signInValue ->
            assertNull(signInError)
            assertNotNull(signInValue)
            accessToken = signInValue.AuthenticationResult.AccessToken
        }
        deleteUser(accessToken)
    }

    @JsName("SignUpFailPasswordTooShortTest")
    @Test
    fun `Sign up should fail because password too short`() = runBlocking {
        authHandler.signUp(
            "Username", "Short",
            attributes = listOf(
                UserAttribute(Name = "email", Value = "test@test.test"),
                UserAttribute(Name = "custom:target_group", Value = "ROLE_USER")
            )
        ) { error, value ->
            assertNotNull(error)
            assertNull(value)
            assertEquals(
                "1 validation error detected: Value at 'password' failed to satisfy constraint: Member must have length greater than or equal to 6",
                error.message
            )
        }
    }

    @JsName("SignUpFailPasswordTooLongTest")
    @Test
    fun `Sign up should fail because password too long`() = runBlocking {
        authHandler.signUp(
            "Username", buildString { (1..260).forEach { _ -> append("A") } },
            attributes = listOf(
                UserAttribute(Name = "email", Value = "test@test.test"),
                UserAttribute(Name = "custom:target_group", Value = "ROLE_USER")
            )
        ) { error, value ->
            assertNotNull(error)
            assertNull(value)
            assertEquals(
                "1 validation error detected: Value at 'password' failed to satisfy constraint: Member must have length less than or equal to 256",
                error.message
            )
        }
    }

    @JsName("SignUpFailUsernameTooLongTest")
    @Test
    fun `Sign up should fail because username too long`() = runBlocking {
        authHandler.signUp(
            buildString { (1..130).forEach { _ -> append("A") } }, "Password",
            attributes = listOf(
                UserAttribute(Name = "email", Value = "test@test.test"),
                UserAttribute(Name = "custom:target_group", Value = "ROLE_USER")
            )
        ) { error, value ->
            assertNotNull(error)
            assertNull(value)
            assertEquals(
                "1 validation error detected: Value at 'username' failed to satisfy constraint: Member must have length less than or equal to 128",
                error.message
            )
        }
    }

    @JsName("SignInTest")
    @Test
    fun `Sign in should fail because wrong credentials`() = runBlocking {
        authHandler.signIn(
            randomUser().username, "WRONG_PASSWORD"
        ) { error, value ->
            assertNotNull(error)
            assertNull(value)
            assertEquals("Incorrect username or password.", error.message)
        }
    }

    @JsName("DeleteUserFailTest")
    @Test
    fun `Get user should fail since access token wrong`() = runBlocking {
        authHandler.deleteUser("WRONG_TOKEN") { error ->
            assertNotNull(error)
            assertEquals("Invalid Access Token", error.message)
        }
    }

    @JsName("DeleteUserTest")
    @Test
    fun `Delete user should fail since access token wrong`() = runBlocking {
        authHandler.deleteUser("WRONG_TOKEN") { error ->
            assertNotNull(error)
            assertEquals("Invalid Access Token", error.message)
        }
    }

    @JsName("SignOutTest")
    @Test
    fun `Sign out should fail since access token wrong`() = runBlocking {
        authHandler.signOut("WRONG_TOKEN") { error ->
            assertNotNull(error)
            assertEquals("Invalid Access Token", error.message)
        }
    }

    @JsName("UpdateUserAttributesTest")
    @Test
    fun `Update attributes should fail since access token wrong`() = runBlocking {
        authHandler.updateUserAttributes(
            "WRONG_TOKEN",
            attributes = listOf(
                UserAttribute(Name = "email", Value = "test@test.test")
            )
        ) { error, value ->
            assertNotNull(error)
            assertNull(value)
            assertEquals("Invalid Access Token", error.message)
        }
    }
}
