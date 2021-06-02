package com.liftric.auth

import AuthHandlerJS
import com.liftric.auth.base.Region
import com.liftric.auth.base.UserAttribute
import env
import kotlinx.coroutines.await
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.fail

class AuthHandlerJSTest {
    private val authHandler = AuthHandlerJS(
        env["origin"] ?: error("missing origin"),
        Region.euCentral1.code,
        env["clientid"] ?: error("missing origin")
    )

    // Randomize temp user account name to not exceed aws try threshold
    private val random = (0..999).random()
    private val username = "auth-lib-test-user-${random}"
    private val password = "auth-lib-test-user-${random}A1@"


    data class Credentials(val username: String, val password: String)

    private fun randomUser(): Credentials {
        val random = (0..9999).random()
        return Credentials(
            username = "auth-lib-test-user-${random}",
            password = "auth-lib-test-user-${random}A1@"
        )
    }

    private suspend fun createUser(): Pair<String, Credentials> {
        val credential = randomUser()
        val userAttributes: List<UserAttribute> = listOf(
            UserAttribute(
                Name = "custom:target_group",
                Value = "ROLE_PATIENT"
            )
        )
        val signUpResponse = authHandler.signUp(credential.username, credential.password, userAttributes.toTypedArray())

        assertNull(signUpResponse.await())
        assertNotNull(signUpResponse.await())

        val signInResponse = authHandler.signIn(credential.username, credential.password)
        assertNotNull(signInResponse.await())

        return Pair(signInResponse.await().AuthenticationResult.AccessToken, credential)
    }

    private suspend fun deleteUser(token: String) {
        val deleteUserResponse = authHandler.deleteUser(token)
        assertNull(deleteUserResponse.await())
    }

    @JsName("SignUpSignInDeleteUserTest")
    @Test
    fun `Sign up, sign in, delete user should succeed`() = runTest {
        println("Sign up, sign in, delete user should succeed")
        authHandler.signUp(
            username, password,
            attributes = arrayOf(
                UserAttribute(Name = "custom:target_group", Value = "ROLE_USER")
            )
        ).await().also {
            println("signUpResponse=$it")
            assertNotNull(it)
        }

        authHandler.signIn(username, password).await().also {
            println("signInResponse=$it")
            assertNotNull(it)

            authHandler.deleteUser(it.AuthenticationResult.AccessToken).await().also {
                println("deleteUser=$it")
                assertNotNull(it)
            }
        }
    }

    @JsName("SignUpFailPasswordTooLongTest")
    @Test
    fun `Sign up should fail because password too long`() = runTest {
        authHandler.signUp(
            "Username", buildString { (1..260).forEach { _ -> append("A") } },
            attributes = arrayOf(
                UserAttribute(Name = "custom:target_group", Value = "ROLE_USER")
            )
        ).then {
            fail("signUp must fail")
        }.catch {
            println(it.message)
        }
    }
}
