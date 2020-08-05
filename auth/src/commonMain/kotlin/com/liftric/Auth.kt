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
    fun signUp(username: String, password: String, attributes: List<UserAttribute>? = null, response: (error: Error?, value: SignUpResponse?) -> Unit)

    /**
     * Signs in the user with the given parameters
     * @param username The username
     * @param password The password
     * @param response Callback with error if something went wrong or an object on success
     */
    fun signIn(username: String, password: String, response: (error: Error?, value: SignInResponse?) -> Unit)

    /**
     * Signs out the user globally
     * @param accessToken The access token from the sign in request
     * @param response Callback with error if something went wrong
     */
    fun signOut(accessToken: String, response: (error: Error?) -> Unit)

    /**
     * Fetches the user object
     * @param accessToken The access token from the sign in request
     * @param response Callback with error if something went wrong or an object on success
     */
    fun getUser(accessToken: String, response: (error: Error?, value: GetUserResponse?) -> Unit)

    /**
     * Updates the users attributes
     * e.g. email, phone number
     * @param accessToken The access token from the sign in request
     * @param attributes List of attributes that should be updated
     * @param response Callback with error if something went wrong or an object on success
     */
    fun updateUserAttributes(accessToken: String, attributes: List<UserAttribute>, response: (error: Error?, value: UpdateUserAttributesResponse?) -> Unit)

    /**
     * Changes the password of the current user
     * @param accessToken The access token from the sign in request
     * @param currentPassword The password to update
     * @param newPassword The new password
     * @param response Callback with request response error
     */
    fun changePassword(accessToken: String, currentPassword: String, newPassword: String, response: (error: Error?) -> Unit)

    /**
     * Deletes the users account
     * @param accessToken The access token from the sign in request
     * @param response Callback with error if something went wrong
     */
    fun deleteUser(accessToken: String, response: (error: Error?) -> Unit)
}