package com.liftric.cognito.idp.jwt

class MissingComponentsException(message:String): Exception(message)
class InvalidBase64Exception(message:String): Exception(message)

/**
 * Abstract base class that represents a JWT
 * Consists of convenience methods and an
 * abstract claims variable that has to be implemented
 * by the class that conforms to the object
 */
abstract class JWT<T>(private val tokenString: String) {
    /**
     * Access to the claims of the payload
     */
    abstract val claims: T

    /**
     * Provides access to the payload of the token
     * @return decoded Json String
     */
    fun getPayload(): String {
        val component = validateComponents()[1]
        return decodeBase64String(component)
    }

    /**
     * Validates if all three components a present
     * @throws MissingComponentsException when components count is not equal 3
     */
    private fun validateComponents(): List<String> {
        val components = tokenString.split(".")
        if (components.size != 3) {
            throw MissingComponentsException("Has ${components.size} components, should be 3 components")
        }
        return components
    }

    /**
     * Decodes Base64 encoded component
     * @throws InvalidBase64Exception when not valid Base64 encoded string
     */
    private fun decodeBase64String(component: String): String {
        return Base64.decode(component)?: throw InvalidBase64Exception("Not a valid Base64 encoded string")
    }
}