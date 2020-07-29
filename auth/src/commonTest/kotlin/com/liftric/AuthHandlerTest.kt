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
    fun signUp()  {
        authHandler.signUp(
            username, password,
            attributes = listOf(
                UserAttribute(Name = "email", Value = "test@test.test"),
                UserAttribute(Name = "custom:target_group", Value = "ROLE_USER")
            )) { error, value ->
            assertNull(error)
            assertNotNull(value)
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