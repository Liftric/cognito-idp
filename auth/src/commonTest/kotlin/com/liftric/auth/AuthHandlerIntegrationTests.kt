package com.liftric.auth

import com.liftric.auth.base.*
import com.liftric.auth.base.Environment
import com.liftric.auth.base.Region
import kotlinx.coroutines.delay
import kotlin.test.*
import kotlinx.coroutines.*
import kotlin.js.JsName

expect class AuthHandlerIntegrationTests: AbstractAuthHandlerIntegrationTests
abstract class AbstractAuthHandlerIntegrationTests() {
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

    data class Credentials(val username: String, val password: String)

    private fun randomUser(): Credentials {
        val random = (0..999).random()
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
        val signUpResponse = authHandler.signUp(credential.username, credential.password, userAttributes)

        assertNull(signUpResponse.exceptionOrNull())
        assertNotNull(signUpResponse.getOrNull())

        val signInResponse = authHandler.signIn(credential.username, credential.password)
        assertNotNull(signInResponse.getOrNull())

        return Pair(signInResponse.getOrNull()!!.AuthenticationResult.AccessToken, credential)
    }

    private suspend fun deleteUser(token: String) {
        val deleteUserResponse = authHandler.deleteUser(token)
        assertNull(deleteUserResponse.exceptionOrNull())
    }

    @JsName("SignUpSignInDeleteUserTest")
    @Test
    fun `Sign up, sign in, delete user should succeed`() = runBlocking {
        val signUpResponse = authHandler.signUp(
            username, password,
            attributes = listOf(
                UserAttribute(Name = "custom:target_group", Value = "ROLE_USER")
            )
        )

        assertNull(signUpResponse.exceptionOrNull())
        assertNotNull(signUpResponse.getOrNull())

        val signInResponse = authHandler.signIn(username, password)
        assertNull(signInResponse.exceptionOrNull())
        assertNotNull(signInResponse.getOrNull())

        val deleteUserResponse = authHandler.deleteUser(signInResponse.getOrNull()!!.AuthenticationResult.AccessToken)
        assertNull(deleteUserResponse.exceptionOrNull())
    }

    @JsName("GetUserTest")
    @Test
    fun `Should get user`() = runBlocking {
        val (token, _) = createUser()

        val getUserAttribute = authHandler.getUser(token)
        assertNull(getUserAttribute.exceptionOrNull())
        assertNotNull(getUserAttribute.getOrNull())

        deleteUser(token)
    }

    @JsName("ChangeAttributeTest")
    @Test
    fun `Should change attribute`() = runBlocking {
        val (token, _) = createUser()

        val updateUserAttributesResponse = authHandler.updateUserAttributes(
            token,
            listOf(UserAttribute(Name = "custom:target_group", Value = "ROLE_USER"))
        )
        assertNull(updateUserAttributesResponse.exceptionOrNull())
        assertNotNull(updateUserAttributesResponse.getOrNull())

        val getUserResponse = authHandler.getUser(token)
        assertNull(getUserResponse.exceptionOrNull())
        assertNotNull(getUserResponse.getOrNull())

        getUserResponse.getOrNull()!!.UserAttributes.map { attribute ->
            if (attribute.Name == "email") {
                assertEquals("ROLE_USER", attribute.Value)
            }
        }
        deleteUser(token)
    }

    @JsName("ChangePasswordTest")
    @Test
    fun `Should change password`() = runBlocking {
        val (token, credentials) = createUser()

        val changePasswordResponse = authHandler.changePassword(token, credentials.password, credentials.password + "B")
        assertNull(changePasswordResponse.exceptionOrNull())

        val signOutResponse = authHandler.signOut(token)
        assertNull(signOutResponse.exceptionOrNull())

        // AWS is not revoking Tokens automatically so give it some time
        delay(1000)

        val signInResponse = authHandler.signIn(credentials.username, credentials.password + "B")
        assertNull(signInResponse.exceptionOrNull())
        assertNotNull(signInResponse.getOrNull())

        deleteUser(signInResponse.getOrNull()!!.AuthenticationResult.AccessToken)
    }

    @JsName("SignOutSignInTest")
    @Test
    fun `Sign out and sign in should succeed`() = runBlocking {
        val (token, credentials) = createUser()

        val signOutResponse = authHandler.signOut(token)
        assertNull(signOutResponse.exceptionOrNull())

        // AWS is not revoking Tokens instantly so give it some time
        delay(1000)

        val signInResponse = authHandler.signIn(credentials.username, credentials.password)
        assertNull(signInResponse.exceptionOrNull())
        assertNotNull(signInResponse.getOrNull())

        deleteUser(signInResponse.getOrNull()!!.AuthenticationResult.AccessToken)
    }

    @JsName("SignUpFailPasswordTooShortTest")
    @Test
    fun `Sign up should fail because password too short`() = runBlocking {
        val signUpResponse = authHandler.signUp(
            "Username", "Short",
            attributes = listOf(
                UserAttribute(Name = "custom:target_group", Value = "ROLE_USER")
            )
        )
        assertNotNull(signUpResponse.exceptionOrNull())
        assertNull(signUpResponse.getOrNull())
        assertEquals(
            "1 validation error detected: Value at 'password' failed to satisfy constraint: Member must have length greater than or equal to 6",
            signUpResponse.exceptionOrNull()!!.message
        )
    }

    @JsName("SignUpFailPasswordTooLongTest")
    @Test
    fun `Sign up should fail because password too long`() = runBlocking {
        val signUpResponse = authHandler.signUp(
            "Username", buildString { (1..260).forEach { _ -> append("A") } },
            attributes = listOf(
                UserAttribute(Name = "custom:target_group", Value = "ROLE_USER")
            )
        )
        assertNotNull(signUpResponse.exceptionOrNull())
        assertNull(signUpResponse.getOrNull())
        assertEquals(
            "1 validation error detected: Value at 'password' failed to satisfy constraint: Member must have length less than or equal to 256",
            signUpResponse.exceptionOrNull()!!.message
        )
    }

    @JsName("SignUpFailUsernameTooLongTest")
    @Test
    fun `Sign up should fail because username too long`() = runBlocking {
        var signUpResponse = authHandler.signUp(
            buildString { (1..130).forEach { _ -> append("A") } }, "Password",
            attributes = listOf(
                UserAttribute(Name = "custom:target_group", Value = "ROLE_USER")
            )
        )
        assertNotNull(signUpResponse.exceptionOrNull())
        assertNull(signUpResponse.getOrNull())
        assertEquals(
            "1 validation error detected: Value at 'username' failed to satisfy constraint: Member must have length less than or equal to 128",
            signUpResponse.exceptionOrNull()!!.message
        )
    }

    @JsName("SignInTest")
    @Test
    fun `Sign in should fail because wrong credentials`() = runBlocking {
        val signInResponse = authHandler.signIn(
            randomUser().username, "WRONG_PASSWORD"
        )
        assertNotNull(signInResponse.exceptionOrNull())
        assertNull(signInResponse.getOrNull())
        assertEquals("Incorrect username or password.", signInResponse.exceptionOrNull()!!.message)
    }

    @JsName("DeleteUserFailTest")
    @Test
    fun `Get user should fail since access token wrong`() = runBlocking {
        val deleteUserResponse = authHandler.deleteUser("WRONG_TOKEN")
        assertNotNull(deleteUserResponse.exceptionOrNull())
        assertEquals("Invalid Access Token", deleteUserResponse.exceptionOrNull()!!.message)
    }

    @JsName("DeleteUserTest")
    @Test
    fun `Delete user should fail since access token wrong`() = runBlocking {
        val deleteUserResponse = authHandler.deleteUser("WRONG_TOKEN")
        assertNotNull(deleteUserResponse.exceptionOrNull())
        assertEquals("Invalid Access Token", deleteUserResponse.exceptionOrNull()!!.message)
    }

    @JsName("SignOutTest")
    @Test
    fun `Sign out should fail since access token wrong`() = runBlocking {
        val signOutResponse = authHandler.signOut("WRONG_TOKEN")
        assertNotNull(signOutResponse.exceptionOrNull())
        assertEquals("Invalid Access Token", signOutResponse.exceptionOrNull()!!.message)
    }

    @JsName("UpdateUserAttributesTest")
    @Test
    fun `Update attributes should fail since access token wrong`() = runBlocking {
        val updateUserAttributesResponse = authHandler.updateUserAttributes(
            "WRONG_TOKEN",
            attributes = listOf(
                UserAttribute(Name = "email", Value = "test@test.test")
            )
        )
        assertNotNull(updateUserAttributesResponse.exceptionOrNull())
        assertNull(updateUserAttributesResponse.getOrNull())
        assertEquals("Invalid Access Token", updateUserAttributesResponse.exceptionOrNull()!!.message)
    }

    @JsName("GetClaimsWithoutEmailTest")
    @Test
    fun `Test if get claims works without email address`() {
        val token = "eyJraWQiOiJwREgwTUpqeWdoRk4wT2J1cFpUNzl1QytLZkpZQ3BtNnZTamVXb3NpZUlFPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiIxZWEyMTg1Yi1iYTk5LTRlYjUtYmZkMS0yMDY0MjQ2Yzc0NWQiLCJhdWQiOiIzdjRzNm9lMmRobjZua2hydTU3OWc2bTZnMSIsImNvZ25pdG86Z3JvdXBzIjpbIlJPTEVfUEFUSUVOVCJdLCJldmVudF9pZCI6IjA3ODE5NGUzLTM5YzQtNDBhYS04MzkwLTkzMmI2MjY2MjE3YSIsInRva2VuX3VzZSI6ImlkIiwiYXV0aF90aW1lIjoxNTk5NTY1OTU4LCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAuZXUtY2VudHJhbC0xLmFtYXpvbmF3cy5jb21cL2V1LWNlbnRyYWwtMV9DMUduN0hiWU4iLCJjb2duaXRvOnVzZXJuYW1lIjoiNDMzMGI4ZjctYmRjMy00MTI2LTllYWUtZWVkZTc0ZTI0MTEyIiwiZXhwIjoxNTk5NTY5NTU4LCJpYXQiOjE1OTk1NjU5NTh9.hLzDOItbHkUWYyI9hTFIKkoC50_UrRnPFoIcyrsiCzP5zQFlhTboe9TZ6BE0o21IEk8tcdBkFRHbuM8zPru-qopB9tC7pkhvY1FoPMlNlRSmqj8YZjJ8InnHForkdJ4n9keM8PcdwW6KWlAjLViwSmOl3k-ptQq1DnmnmGmcmfzssDzON0R__jsyEQs_EZWQ3c86qodbIU4peN9Dm26TMSQCzJhZwvCuGRmRgplsqOD4UfDVh5ya-bXUogJuirlE8KFUH1my13AJOxAJLBgyOjBZceFMnC4ZqZBbSND-iiMvQcpn4O6gd5p5Je367LO56w_ypo6eMffEs39fikIP4g"
        authHandler.getClaims(token)
            .onSuccess { claims ->
                assertNotNull(claims)
                assertEquals(claims.sub, "1ea2185b-ba99-4eb5-bfd1-2064246c745d")
                assertEquals(claims.email, null)
                assertEquals(claims.emailVerified, null)
            }
            .onFailure {
                fail(it.message)
            }
    }

    @JsName("GetClaimsWithEmailTest")
    @Test
    fun `Test if get claims works with email address`() {
        val token = "eyJraWQiOiJwREgwTUpqeWdoRk4wT2J1cFpUNzl1QytLZkpZQ3BtNnZTamVXb3NpZUlFPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiI3NTUzZGRmOC1hMTAzLTRjYjItOWVkZi0yNDcwMTBmNGNjNGQiLCJhdWQiOiIzdjRzNm9lMmRobjZua2hydTU3OWc2bTZnMSIsImNvZ25pdG86Z3JvdXBzIjpbIlJPTEVfUEFUSUVOVCJdLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImV2ZW50X2lkIjoiZmMxNTM3NTQtNDY5ZS00YzZiLTlhMzktODVhM2M3MDAxZTMwIiwidG9rZW5fdXNlIjoiaWQiLCJhdXRoX3RpbWUiOjE1OTk1NjY5MjMsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC5ldS1jZW50cmFsLTEuYW1hem9uYXdzLmNvbVwvZXUtY2VudHJhbC0xX0MxR243SGJZTiIsImNvZ25pdG86dXNlcm5hbWUiOiI2YTg0MzYzNS1kZWM2LTQxMmYtYjI0MS1iNGRmYmI2NTVkM2YiLCJleHAiOjE1OTk1NzA1MjMsImlhdCI6MTU5OTU2NjkyMywiZW1haWwiOiJnYWViZWxAbGlmdHJpYy5jb20ifQ.ka1nCmT-ACwbvQ3uy3qsuZII6PQzdfJHA7UY3Wkt_7GU2fxBxcDdRjzdDdCmh4IE0e0uwfoddMXTXWaijo6yKvrv0VHtfsIkfFJb09TNtCNrxTy1PX-bJNeVT752N85pdNpkms6GefylP2iAZec520ISI1ZrHz0jlKfUq6iGpq3GKxIXJZ_dQGVPa2oTQDqG_CmOsr9sTRl8EoMoEIjxJdOAFeYltlPDcuhWZVUWsfwUq290UdOTBJhGruIre-cdfe03FEo9NG67mewldRYdsjNBgGQU_Jyp68hg1UQHrhKC-eUDmrWiyYGzKwbkUCCm1puwcy_wpu5HRQfjAjVW4A"
        authHandler.getClaims(token)
            .onSuccess { claims ->
                assertNotNull(claims)
                assertEquals(claims.sub, "7553ddf8-a103-4cb2-9edf-247010f4cc4d")
                assertNotEquals(claims.email, null)
                assertEquals(claims.emailVerified, false)
            }
            .onFailure {
                fail(it.message)
            }
    }
}
