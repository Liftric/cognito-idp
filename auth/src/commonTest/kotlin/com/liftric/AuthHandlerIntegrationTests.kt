package com.liftric

import com.liftric.base.*
import com.liftric.base.Environment
import kotlinx.coroutines.delay
import kotlin.test.*

class TestAuthHandler(configuration: Configuration): AuthHandler(configuration) {
    override fun dispatch(block: suspend () -> Unit) = runBlocking {
        block()
    }
}

class AuthHandlerIntegrationTests() {
    private val configuration = Configuration(
        Environment.variable("origin") ?: "",
        Region.euCentral1,
        Environment.variable("clientid") ?: ""
    )

    // Randomize temp user account name to not exceed aws try threshold
    private val random = (0..999).random()
    private val username = "auth-lib-test-user-$random"
    private val password = "auth-lib-test-user-${random}A1@"

    private var authHandler = TestAuthHandler(configuration)

    //-------------------
    // INTEGRATION TESTS
    //-------------------

    private fun randomUser(): String {
        return "auth-lib-test-user-${(0..999).random()}"
    }

    private fun createUser(block: (accessToken: String, credential: String) -> Unit) {
        val credential = randomUser()

        authHandler.signUp(credential, credential, null) { _,_ ->
            authHandler.signIn(credential, credential) { _, value ->
                assertNotNull(value)
                block(value.AuthenticationResult.AccessToken, credential)
            }
        }
    }

    private fun deleteUser(token: String) {
        authHandler.deleteUser(token) { error ->
            assertNull(error)
        }
    }

    @Test
    fun `Sign up, sign in, delete user should succeed`()  {
        authHandler.signUp(
            username, password,
            attributes = listOf(
                UserAttribute(Name = "email", Value = "test@test.test"),
                UserAttribute(Name = "custom:target_group", Value = "ROLE_USER")
            )) { error, value ->
            assertNull(error)
            assertNotNull(value)

            authHandler.signIn(username, password) { signInError, signInValue ->
                assertNull(signInError)
                assertNotNull(signInValue)

                try {
                    authHandler.deleteUser(signInValue.AuthenticationResult.AccessToken) { deleteError ->
                        assertNull(deleteError)
                    }
                } catch(error: Error) {
                    fail(error.message)
                }
            }
        }
    }

    @Test
    fun `Should get user`() {
        createUser { token, _ ->
            authHandler.getUser(token) { error, value ->
                assertNull(error)
                assertNotNull(value)
                deleteUser(token)
            }
        }
    }

    @Test
    fun `Should change email`() {
        createUser { token, _ ->
            authHandler.updateUserAttributes(
                token,
                listOf(UserAttribute(Name = "email", Value = "test2@test.test"))
            ) { error, value ->
                assertNull(error)
                assertNotNull(value)

                authHandler.getUser(token) { getUserError, getUserValue ->
                    assertNull(getUserError)
                    assertNotNull(getUserValue)

                    getUserValue.UserAttributes.map { attribute ->
                        if(attribute.Name == "email") {
                            assertEquals("test2@test.test", attribute.Value)
                        }
                    }

                    deleteUser(token)
                }
            }
        }
    }

    @Test
    fun `Should change password`() {
        createUser { token, credential ->
            authHandler.changePassword(token, credential, credential + "A") { error ->
                assertNull(error)

                authHandler.signOut(token) { signOutError ->
                    assertNull(signOutError)

                    runBlocking {
                        // AWS is not revoking Tokens automatically so give it some time
                        delay(1000)
                    }
                    authHandler.signIn(credential, credential + "A") { signInError, signInValue ->
                        assertNull(signInError)
                        assertNotNull(signInValue)

                        runBlocking {
                            // AWS is not revoking Tokens automatically so give it some time
                            delay(1000)
                        }

                        deleteUser(signInValue.AuthenticationResult.AccessToken)
                    }
                }
            }
        }
    }

    @Test
    fun `Sign out and sign in should succeed`() {
        createUser { token, credential ->
            authHandler.signOut(token) { error ->
                assertNull(error)

                runBlocking {
                    // AWS is not revoking Tokens automatically so give it some time
                    delay(1000)
                }
                authHandler.signIn(credential, credential) { signInError, signInValue ->
                    assertNull(signInError)
                    assertNotNull(signInValue)

                    runBlocking {
                        // AWS is not revoking Tokens automatically so give it some time
                        delay(1000)
                    }

                    deleteUser(signInValue.AuthenticationResult.AccessToken)
                }
            }
        }
    }

    @Test
    fun `Sign up should fail because password too short`() {
        authHandler.signUp(
            "Username", "Short",
            attributes = listOf(
                UserAttribute(Name = "email", Value = "test@test.test"),
                UserAttribute(Name = "custom:target_group", Value = "ROLE_USER")
            )) { error, value ->
            assertNotNull(error)
            assertNull(value)
            assertEquals("1 validation error detected: Value at 'password' failed to satisfy constraint: Member must have length greater than or equal to 6", error.message)
        }
    }

    @Test
    fun `Sign up should fail because password too long`() {
        authHandler.signUp(
            "Username", buildString { (1..260).forEach { _ -> append("A") } },
            attributes = listOf(
                UserAttribute(Name = "email", Value = "test@test.test"),
                UserAttribute(Name = "custom:target_group", Value = "ROLE_USER")
            )) { error, value ->
            assertNotNull(error)
            assertNull(value)
            assertEquals("1 validation error detected: Value at 'password' failed to satisfy constraint: Member must have length less than or equal to 256", error.message)
        }
    }

    @Test
    fun `Sign up should fail because username too long`() {
        authHandler.signUp(
            buildString { (1..130).forEach { _ -> append("A") } }, "Password",
            attributes = listOf(
                UserAttribute(Name = "email", Value = "test@test.test"),
                UserAttribute(Name = "custom:target_group", Value = "ROLE_USER")
            )) { error, value ->
            assertNotNull(error)
            assertNull(value)
            assertEquals("1 validation error detected: Value at 'username' failed to satisfy constraint: Member must have length less than or equal to 128", error.message)
        }
    }

    @Test
    fun `Sign in should fail because wrong credentials`() {
        authHandler.signIn(
            randomUser(), "WRONG_PASSWORD") { error, value ->
            assertNotNull(error)
            assertNull(value)
            assertEquals("Incorrect username or password.", error.message)
        }
    }

    @Test
    fun `Get user should fail since access token wrong`() {
        authHandler.deleteUser("WRONG_TOKEN") { error ->
            assertNotNull(error)
            assertEquals("Invalid Access Token", error.message)
        }
    }

    @Test
    fun `Delete user should fail since access token wrong`() {
        authHandler.deleteUser("WRONG_TOKEN") { error ->
            assertNotNull(error)
            assertEquals("Invalid Access Token", error.message)
        }
    }

    @Test
    fun `Sign out should fail since access token wrong`() {
        authHandler.signOut("WRONG_TOKEN") { error ->
            assertNotNull(error)
            assertEquals("Invalid Access Token", error.message)
        }
    }

    @Test
    fun `Update attributes should fail since access token wrong`() {
        authHandler.updateUserAttributes(
            "WRONG_TOKEN",
            attributes = listOf(
                UserAttribute(Name = "email", Value = "test@test.test"))
        ) { error, value ->
            assertNotNull(error)
            assertNull(value)
            assertEquals("Invalid Access Token", error.message)
        }
    }
}
