package com.liftric.cognito.idp.core

import kotlinx.serialization.SerializationException

class Result<out T> constructor(val value: Any?) {
    val isSuccess: Boolean get() = value !is Failure
    val isFailure: Boolean get() = value is Failure

    companion object {
        fun <T> success(value: T): Result<T> = Result(value)
        fun <T> failure(exception: Throwable): Result<T> = Result(Failure(exception))
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

    @Throws(
        IdentityProviderException::class,
        SerializationException::class,
        Throwable::class
    )
    fun getOrThrow(): T {
        throwOnFailure()
        return value as T
    }

    fun exceptionOrNull(): Throwable? =
        when (value) {
            is Failure -> value.exception
            else -> null
        }

    private fun throwOnFailure() {
        if (value is Failure) throw value.exception
    }

    fun onFailure(action: (exception: Throwable) -> Unit): Result<T> {
        exceptionOrNull()?.let { action(it) }
        return this
    }

    fun onSuccess(action: (value: T) -> Unit): Result<T> {
        if (isSuccess) action(value as T)
        return this
    }

    fun fold(onSuccess: (value: T) -> Unit, onFailure: (exception: Throwable) -> Unit) {
        when(value) {
            is Failure -> exceptionOrNull()?.let { onFailure(it) }
            else -> onSuccess(value as T)
        }
    }

    override fun toString(): String =
        when (value) {
            is Failure -> value.toString()
            else -> "Success($value)"
        }
}

fun <T, R> Result<T>.onResult(action: (value: T) -> Result<R>): Result<R> {
    return when (value) {
        is Result.Failure -> Result.failure(value.exception)
        else -> action(value as T)
    }
}