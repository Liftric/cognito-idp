package com.liftric

import com.liftric.base.*
import com.liftric.resources.Environment
import com.liftric.resources.signInErrorResponse
import com.liftric.resources.signInResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpStatusCode
import kotlin.test.*

expect fun runTest(block: suspend () -> Unit)

class TestAuthHandler(configuration: Configuration, settingsStore: SettingsStore,
                      secretStore: SecretStore): AuthHandler(configuration, settingsStore, secretStore
) {
    /**
     * Override dispatch method, and launch request methods runBlocking.
     * Otherwise it wouln't be testable.
     */
    override fun dispatch(block: suspend () -> Unit) = runTest { block() }
}

expect class AuthHandlerTest: AbstractAuthHandlerTest

abstract class AbstractAuthHandlerTest(
    settingsStore: SettingsStore,
    secretStore: SecretStore
) {
    private val configuration = Configuration(
        Environment.variable("origin")?: "",
        Region.euCentral1,
        Environment.variable("clientid")?: ""
    )

    // Randomize temp user account name to not exceed aws boundaries
    private val random = (0..999).random()
    private val username = "auth-lib-test-user-$random"
    private val password = "auth-lib-test-user-$random"

    private var authHandler = TestAuthHandler(configuration, settingsStore, secretStore)

    //-------------------
    // INTEGRATION TESTS
    //-------------------

    @Test
    fun `test sign up, sign in, delete user`()  {
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

                authHandler.deleteUser { deleteError, deleteValue ->
                    assertNull(deleteError)
                    assertNotNull(deleteValue)
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
            "Wrong_User", "Wrong_Password") { error, value ->
            assertNotNull(error)
            assertNull(value)
            assertEquals("Incorrect username or password.", error.message)
        }
    }

    @Test
    fun `Delete user should fail since no user is signed in`() {
        authHandler.deleteUser { error, value ->
            assertNotNull(error)
            assertNull(value)
            assertEquals("The user is not signed in.", error.message)
        }
    }
}