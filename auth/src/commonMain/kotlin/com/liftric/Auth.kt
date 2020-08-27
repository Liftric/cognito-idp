package com.liftric

import com.liftric.base.*

interface Auth {
    /**
     * Signs up a new user
     * @param username The username
     * @param password The password
     * @param attributes Optional account attributes e.g. email, phone number, ...
     * @return Result object containing SignUpResponse on success or an error on failure
     */
    suspend fun signUp(username: String, password: String, attributes: List<UserAttribute>? = null): Result<SignUpResponse>

    /**
     * Confirms sign up of a new user
     * @param username The username
     * @param confirmationCode The confirmation code that was sent to the users' delivery medium
     * @return Result object containing Unit on success or an error on failure
     */
    suspend fun confirmSignUp(username: String, confirmationCode: String): Result<Unit>

    /**
     * Signs in the user with the given parameters
     * @param username The username
     * @param password The password
     * @return Result object containing SignInResponse on success or an error on failure
     */
    suspend fun signIn(username: String, password: String): Result<SignInResponse>

    /**
     * Fetches the user object
     * @param accessToken The access token from the sign in request
     * @return Result object containing GetUserResponse on success or an error on failure
     */
    suspend fun getUser(accessToken: String): Result<GetUserResponse>

    /**
     * Updates the users attributes
     * e.g. email, phone number
     * @param accessToken The access token from the sign in request
     * @param attributes List of attributes that should be updated
     * @return Result object containing UpdateUserAttributesResponse on success or an error on failure
     */
    suspend fun updateUserAttributes(accessToken: String, attributes: List<UserAttribute>): Result<UpdateUserAttributesResponse>

    /**
     * Changes the password of the current user
     * @param accessToken The access token from the sign in request
     * @param currentPassword The password to update
     * @param newPassword The new password
     * @return Result object containing Unit on success or an error on failure
     */
    suspend fun changePassword(accessToken: String, currentPassword: String, newPassword: String): Result<Unit>

    /**
     * Invokes password forgot and sends a confirmation code the the users' delivery medium
     * @param username The username
     * @return Result object containing CodeDeliveryDetails on success or an error on failure
     */
    suspend fun forgotPassword(username: String): Result<CodeDeliveryDetails>

    /**
     * Confirms forgot password
     * @param username The username
     * @param password The new password that was sent to the users' delivery medium
     * @param confirmationCode The confirmation code that was sent to the users' delivery medium
     * @return Result object containing Unit on success or an error on failure
     */
    suspend fun confirmForgotPassword(username: String, password: String, confirmationCode: String): Result<Unit>

    /**
     * Signs out the user globally
     * @param accessToken The access token from the sign in request
     * @return Result object containing Unit on success or an error on failure
     */
    suspend fun signOut(accessToken: String): Result<Unit>

    /**
     * Deletes the users account
     * @param accessToken The access token from the sign in request
     * @return Result object containing Unit on success or an error on failure
     */
    suspend fun deleteUser(accessToken: String): Result<Unit>
}
