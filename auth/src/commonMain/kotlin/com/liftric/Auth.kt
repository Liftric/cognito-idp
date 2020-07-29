package com.liftric

import com.liftric.base.UserAttribute

interface Auth {
    /**
     * Signs up a new user
     */
    fun signUp(username: String, password: String, attributes: List<UserAttribute>? = null, response: (error: Error?, value: String?) -> Unit)

    /**
     * Signs in the user with the given parameters
     * @param username The username
     * @param password The password
     * @param response Callback with error and request response
     */
    fun signIn(username: String, password: String, response: (error: Error?, value: String?) -> Unit)

    /**
     * Signs the user out globally
     * @param response Callback with error and request response
     */
    fun signOut(accessToken: String, response: (error: Error?, value: String?) -> Unit)

    /**
     * Fetches the user object
     * @param response Callback with error and request response
     */
    fun getUser(accessToken: String, response: (error: Error?, value: String?) -> Unit)

    /**
     * Updates the users attributes
     * e.g. email, phone number
     * @param response Callback with error and request response
     */
    fun updateUserAttributes(accessToken: String, attributes: List<UserAttribute>, response: (error: Error?, value: String?) -> Unit)

    /**
     * Changes the password of the current user
     * @param response Callback with error and request response
     */
    fun changePassword(accessToken: String, currentPassword: String, newPassword: String, response: (error: Error?, value: String?) -> Unit)

    /**
     * Deletes the users account
     * @param response Callback with error and request response
     */
    fun deleteUser(accessToken: String, response: (error: Error?, value: String?) -> Unit)
}