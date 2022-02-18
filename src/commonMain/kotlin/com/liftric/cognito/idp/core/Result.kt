package com.liftric.cognito.idp.core

import kotlinx.serialization.SerializationException

/**
 * Since it is not possible to export [kotlin.Result] to iOS
 * we recreated the class with some of its methods and properties.
 */
class Result<out T> constructor(val value: Any?) {
    val isSuccess: Boolean get() = value !is Failure
    val isFailure: Boolean get() = value is Failure

    companion object {
        fun <T> success(value: T): Result<T> = Result(value)
        fun <T> failure(exception: Throwable): Result<T> = Result(Failure(exception))

        /**
         * [Result] builder function. Returns Success if [mightFail] doesn't, otherwise returns
         * [mightFail] exception as [Failure]
         */
        fun <T> doTry(mightFail: () -> T) : Result<T> = try {
             success(mightFail())
         } catch (e: Throwable){
             failure(e)
         }
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

    fun fold(onSuccess: (value: T) -> Unit, onFailure: (exception: Throwable) -> Unit) {
        when(value) {
            is Failure -> exceptionOrNull()?.let { onFailure(it) }
            else -> onSuccess(value as T)
        }
    }

    /**
     * Executes the [onSuccess] mapping if Success, or re-wraps the Failure doing nothing
     *
     * This can be used to pipe transform the result only if it is successful.
     * A happy path of results can be modelled with this.
     *
     * Would be called foldRight if [Result] would be called Either<Throwable,T> ;)
     */
    fun <R> andThen(onSuccess: (value: T) -> R): Result<R> = when(value) {
        is Failure -> failure(value.exception)
        else -> doTry { onSuccess(value as T) }
    }

    override fun toString(): String =
        when (value) {
            is Failure -> value.toString()
            else -> "Success($value)"
        }
}

inline fun <T, R> Result<T>.onResult(action: (value: T) -> Result<R>): Result<R> {
    return when (value) {
        is Result.Failure -> Result.failure(value.exception)
        else -> action(value as T)
    }
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
