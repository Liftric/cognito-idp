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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

expect class AuthHandlerMockTest: AbstractAuthHandlerMockTest

abstract class AbstractAuthHandlerMockTest(
    settingsStore: SettingsStore,
    secretStore: SecretStore
) {
    private val configuration = Configuration(
        Environment.variable("origin")?: "",
        Region.euCentral1,
        Environment.variable("clientid")?: ""
    )

    private var authHandler = TestAuthHandler(configuration, settingsStore, secretStore)

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

        val payload = Authentication(AuthFlow.UserPasswordAuth, "1234567890",
            AuthParameters("username", "password")
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

        val payload = Authentication(AuthFlow.UserPasswordAuth, "1234567890",
            AuthParameters("username", "password")
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