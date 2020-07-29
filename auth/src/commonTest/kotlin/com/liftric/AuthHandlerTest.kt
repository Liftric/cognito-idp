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
import kotlinx.coroutines.withTimeout
import kotlin.test.*

class TestAuthHandler(configuration: Configuration): AuthHandler(configuration) {
    /**
     * Override dispatch method, and launch request methods runBlocking.
     * Otherwise it wouln't be testable.
     */
    override fun dispatch(block: suspend () -> Unit) = runTest {
        block()
    }
}

expect fun runTest(block: suspend () -> Unit)

class AuthHandlerTest() {
    private val configuration = Configuration(
        Environment.variable("origin")?: "",
        Region.euCentral1,
        Environment.variable("clientid")?: ""
    )

    // Randomize temp user account name to not exceed aws boundaries
    private val random = (0..999).random()
    private val username = "auth-lib-test-user-$random"
    private val password = "auth-lib-test-user-$random"

    private var authHandler = TestAuthHandler(configuration)

    //-------------------
    // INTEGRATION TESTS
    //-------------------
    @Test
    fun testSomething() {
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
}
