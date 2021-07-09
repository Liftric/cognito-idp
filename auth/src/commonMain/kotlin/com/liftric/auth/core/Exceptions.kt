package com.liftric.auth.core

import io.ktor.http.HttpStatusCode

open class IdentityProviderException(val status: HttpStatusCode?, message: String) : Exception(message)

class CodeMismatchException(status: HttpStatusCode, message: String) : IdentityProviderException(status, message)
class ExpiredCodeException(status: HttpStatusCode, message: String) : IdentityProviderException(status, message)
class InternalErrorException(status: HttpStatusCode, message: String) : IdentityProviderException(status, message)
class InvalidLambdaResponseException(status: HttpStatusCode, message: String) : IdentityProviderException(status, message)
class InvalidParameterException(status: HttpStatusCode, message: String) : IdentityProviderException(status, message)
class InvalidPasswordException(status: HttpStatusCode, message: String) : IdentityProviderException(status, message)
class LimitExceededException(status: HttpStatusCode, message: String) : IdentityProviderException(status, message)
class NotAuthorizedException(status: HttpStatusCode, message: String) : IdentityProviderException(status, message)
class ResourceNotFoundException(status: HttpStatusCode, message: String) : IdentityProviderException(status, message)
class TooManyFailedAttemptsException(status: HttpStatusCode, message: String) : IdentityProviderException(status, message)
class TooManyRequestsException(status: HttpStatusCode, message: String) : IdentityProviderException(status, message)
class UnexpectedLambdaException(status: HttpStatusCode, message: String) : IdentityProviderException(status, message)
class UserLambdaValidationException(status: HttpStatusCode, message: String) : IdentityProviderException(status, message)
class UserNotConfirmedException(status: HttpStatusCode, message: String) : IdentityProviderException(status, message)
class UserNotFoundException(status: HttpStatusCode, message: String) : IdentityProviderException(status, message)
