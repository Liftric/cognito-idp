package com.liftric

import com.liftric.base.*

interface Auth {
    /**
     * Signs up a new user
     * @param username The username
     * @param password The password
     * @param attributes
     * @param response Callback with error if something went wrong or an object on success
     */
    suspend fun signUp(username: String, password: String, attributes: List<UserAttribute>? = null): Result<SignUpResponse>

    /**
     * Signs in the user with the given parameters
     * @param username The username
     * @param password The password
     * @param response Callback with error if something went wrong or an object on success
     */
    suspend fun signIn(username: String, password: String): Result<SignInResponse>

    /**
     * Signs out the user globally
     * @param accessToken The access token from the sign in request
     * @param response Callback with error if something went wrong
     */
    suspend fun signOut(accessToken: String): Result<Unit>

    /**
     * Fetches the user object
     * @param accessToken The access token from the sign in request
     * @param response Callback with error if something went wrong or an object on success
     */
    suspend fun getUser(accessToken: String): Result<GetUserResponse>

    /**
     * Updates the users attributes
     * e.g. email, phone number
     * @param accessToken The access token from the sign in request
     * @param attributes List of attributes that should be updated
     * @param response Callback with error if something went wrong or an object on success
     */
    suspend fun updateUserAttributes(accessToken: String, attributes: List<UserAttribute>): Result<UpdateUserAttributesResponse>

    /**
     * Changes the password of the current user
     * @param accessToken The access token from the sign in request
     * @param currentPassword The password to update
     * @param newPassword The new password
     * @param response Callback with request response error
     */
    suspend fun changePassword(accessToken: String, currentPassword: String, newPassword: String): Result<Unit>

    /**
     * Deletes the users account
     * @param accessToken The access token from the sign in request
     * @param response Callback with error if something went wrong
     */
    suspend fun deleteUser(accessToken: String): Result<Unit>
}
