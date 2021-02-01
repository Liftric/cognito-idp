package com.liftric.auth.base

import com.liftric.auth.NotAuthorizedException
import com.liftric.auth.UserNotFoundException
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.cio.*

class Result<out T> constructor(val value: Any?, statusCode: HttpStatusCode? = null) {
    val isSuccess: Boolean get() = value !is Failure
    val isFailure: Boolean get() = value is Failure
    var statusCode: HttpStatusCode? = statusCode
        private set

    companion object {
        fun <T> success(value: T, statusCode: HttpStatusCode? = null): Result<T> = Result(value, statusCode)
        fun <T> failure(exception: Throwable, statusCode: HttpStatusCode? = null): Result<T> = Result(Failure(exception), statusCode)
    }

    class Failure(val exception: Throwable) {
        override fun equals(other: Any?): Boolean = other is Failure && exception == other.exception
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = "Failure($exception)"
    }

    fun getOrNull(): T? =
        when {
            isFailure -> null
            else -> value as T
        }

    fun exceptionOrNull(): Throwable? =
        when (value) {
            is Failure -> value.exception
            else -> null
        }

    override fun toString(): String =
        when (value) {
            is Failure -> value.toString() // "Failure($exception)"
            else -> "Success($value)"
        }
}

inline fun <T, R> Result<T>.onResult(action: (value: T) -> Result<R>): Result<R> {
    return when (value) {
        is Result.Failure -> Result.failure(value.exception, statusCode)
        else -> {
            val action = action(value as T)
            val value = action.value
            when(value) {
                is Result.Failure -> Result.failure(value.exception, statusCode)
                else -> Result.success(value as R, statusCode)
            }
        }
    }
}

fun Result<*>.throwOnFailure() {
    if (value is Result.Failure) throw value.exception
}

inline fun <T> Result<T>.getOrThrow(): T {
    throwOnFailure()
    return value as T
}

inline fun <T> Result<T>.onFailure(action: (exception: Throwable) -> Unit): Result<T> {
    exceptionOrNull()?.let { action(it) }
    return this
}

inline fun <T> Result<T>.onSuccess(action: (value: T) -> Unit): Result<T> {
    if (isSuccess) action(value as T)
    return this
}

inline fun <R> runCatching(block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        Result.failure(e)
    }
}

inline fun <T, R> T.runCatching(block: T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
