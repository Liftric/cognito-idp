package com.liftric.auth

import com.liftric.auth.base.*
import com.liftric.auth.base.Environment
import com.liftric.auth.base.Region
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
}
