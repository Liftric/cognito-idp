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

expect class AuthHandlerTest: AbstractAuthHandlerTest

abstract class AbstractAuthHandlerTest(
    private val settingsStore: SettingsStore,
    private val secretStore: SecretStore
) {
    private val configuration = Configuration(
        Environment.variable("origin")?: "",
        Region.euCentral1,
        Environment.variable("clientid")?: ""
    )

    private val username = "multiplatform-auth-lib"
    private val password = "multiplatform-auth-lib"

    private lateinit var authHandler: AuthHandler

    @BeforeTest
    fun setUp() {
        authHandler = AuthHandler(configuration, settingsStore, secretStore)
    }

    //-------------------
    // INTEGRATION TESTS
    //-------------------

    @Test
    fun `Test sign up, sign in, delete user`() = runTest {
        signUp(
            username,
            password
        ) { error, value ->
            assertNull(error)
            assertNotNull(value)

            runTest {
                signIn(
                    username,
                    password
                ) { error, value ->
                    assertNull(error)
                    assertNotNull(value)

                    runTest {
                        deleteUser { error, value ->
                            assertNull(error)
                            assertNotNull(value)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Sign in will fail because the credentals are wrong`() = runTest {
        signIn(
            username= "WRONG_ACCOUNT",
            password = "WRONG_PASSWORD"
        ) { error, value ->
            assertNull(value)
            assertNotNull(error)
            assertEquals("Incorrect username or password.", error.message)
        }
    }

    @Test
    fun `Sign up will fail because the password is too short`() = runTest {
        signUp(
            username= "SOMEUSER",
            password = "SHORT"
        ) { error, value ->
            assertNull(value)
            assertNotNull(error)
            assertEquals("1 validation error detected: Value at 'password' failed to satisfy constraint: Member must have length greater than or equal to 6", error.message)
        }
    }

    @Test
    fun `Sign up will fail because the password is too long`() = runTest {
        signUp(
            username = "SOMEUSER",
            password = buildString { (1..260).forEach { _ -> append("A") } }
        ) { error, value ->
            assertNull(value)
            assertNotNull(error)
            assertEquals("1 validation error detected: Value at 'password' failed to satisfy constraint: Member must have length less than or equal to 256", error.message)
        }
    }

    @Test
    fun `Sign up will fail because the username is too long`() = runTest {
        signUp(
            username = buildString { (1..130).forEach { _ -> append("A") } },
            password = "SOMEPASSWORD"
        ) { error, value ->
            assertNull(value)
            assertNotNull(error)
            assertEquals("1 validation error detected: Value at 'username' failed to satisfy constraint: Member must have length less than or equal to 128", error.message)
        }
    }

    // Helper methods

    private suspend fun signUp(username: String, password: String, response: ((error: Error?, value: String?) -> Unit)? = null) {
        authHandler.signUpRequest(
            username = username,
            password = password,
            attributes = listOf(UserAttribute("custom:target_group", "ROLE_PATIENT"))
        ) { error, value ->
            if (response != null) {
                response(error, value)
            }
        }
    }

    private suspend fun signIn(username: String, password: String, response: ((error: Error?, value: String?) -> Unit)? = null) {
        authHandler.signInRequest(
            username,
            password
        ) { error, value ->
            if (response != null) {
                response(error, value)
            }
        }
    }

    private suspend fun deleteUser(response: (error: Error?, value: String?) -> Unit) {
        authHandler.deleteUserRequest() { error, value ->
            response(error, value)
        }
    }

    //---------------------
    // UNIT TESTS (MOCKED)
    //---------------------

    private fun setUpMockClient(statusCode: HttpStatusCode, response: String, headers: Headers) {
        authHandler.setClient(HttpClient(MockEngine) {
            engine {
                addHandler {
                    val builder = HeadersBuilder()
                    configuration.setupDefaultRequest(builder)
                    builder.append("Content-Type", Header.AmzJson)
                    builder.apply {
                        headers.forEach { key, value ->
                            append(key, value.toString())
                        }
                    }
                    respond(response, statusCode, builder.build())
                }
            }
        })
    }

    @Test
    fun testRequest() = runTest {
        setUpMockClient(
            HttpStatusCode(200, "Success"),
            signInResponse, Headers.Empty
        )

        val payload = Authentication(
            AuthFlow.UserPasswordAuth,
            "1234567890",
            AuthParameters(username, password)
        )

        authHandler.request(
            AuthHandler.RequestType.signIn,
            serialize(Authentication.serializer(), payload)
        ) { error, value ->
            assertNull(error)
            assertNotNull(value)

            val obj = parse(AuthResponse.serializer(), value)
            assertEquals("ABCDEDFG", obj.AuthenticationResult.AccessToken)
            assertEquals(3600, obj.AuthenticationResult.ExpiresIn)
        }
    }

    @Test
    fun testRequestError() = runTest {
        setUpMockClient(
            HttpStatusCode(400, "Error"),
            signInErrorResponse, Headers.Empty
        )

        val payload = Authentication(
            AuthFlow.UserPasswordAuth,
            "1234567890",
            AuthParameters(username, password)
        )

        authHandler.request(
            AuthHandler.RequestType.signIn,
            serialize(Authentication.serializer(), payload)
        ) { error, value ->
            assertNotNull(error)
            assertNull(value)
            assertEquals("Encountered an error", error.message)
        }
    }
}