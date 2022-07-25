package com.liftric.cognito.idp.core

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
         * Wraps value on success or exception on [Failure].
         */
        fun <T> doTry(block: () -> T) : Result<T> = try {
             success(block())
         } catch (e: Throwable){
             failure(e)
         }
    }

    class Failure(val exception: Throwable) {
        override fun equals(other: Any?): Boolean = other is Failure && exception == other.exception
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = "Failure($exception)"
    }

    fun getOrNull(): T? = when (value) {
        is Failure -> null
        else -> value as T
    }

    @Throws(Throwable::class)
    fun getOrThrow(): T = when (value) {
        is Failure -> throw value.exception
        else -> value as T
    }

    fun exceptionOrNull(): Throwable? = when (value) {
        is Failure -> value.exception
        else -> null
    }

    /**
     * Applies the transformation on success, otherwise re-wraps the [Failure].
     */
    fun <R> map(transform: (value: T) -> R): Result<R> = when (value) {
        is Failure -> failure(value.exception)
        else -> success(transform(value as T))
    }

    /**
     * Applies the transformation on success, otherwise re-wraps the [Failure].
     * Catches exception and wraps it as [Failure].
     */
    fun <R> mapCatching(transform: (value: T) -> R): Result<R> = when (value) {
        is Failure -> failure(value.exception)
        else -> doTry { transform(value as T) }
    }

    /**
     * Applies the transformation on success, otherwise re-wraps the [Failure].
     * Catches exception and wraps it as [Failure].
     */
    @Deprecated("Use mapCatching.", ReplaceWith("mapCatching(onSuccess)"))
    fun <R> andThen(onSuccess: (value: T) -> R): Result<R> = when(value) {
        is Failure -> failure(value.exception)
        else -> doTry { onSuccess(value as T) }
    }

    /**
     * Provides value [onSuccess] or exception [onFailure].
     */
    fun fold(onSuccess: (value: T) -> Unit, onFailure: (exception: Throwable) -> Unit) = when(value) {
        is Failure -> exceptionOrNull()?.let { onFailure(it) }
        else -> onSuccess(value as T)
    }

    override fun toString(): String = when (value) {
        is Failure -> value.toString()
        else -> "Success($value)"
    }
}

inline fun <T, R> Result<T>.onResult(action: (value: T) -> Result<R>): Result<R> = when (value) {
    is Result.Failure -> Result.failure(value.exception)
    else -> action(value as T)
}

inline fun <T> Result<T>.onFailure(action: (exception: Throwable) -> Unit): Result<T> = apply {
    exceptionOrNull()?.let { action(it) }
}

@Deprecated("Use fold.", ReplaceWith("fold(onSuccess = action, onFailure = {})"))
inline fun <T> Result<T>.onSuccess(action: (value: T) -> Unit): Result<T> = apply {
    if (value !is Result.Failure) action(value as T)
}

@Deprecated("Use doTry.", ReplaceWith("doTry(block)"))
inline fun <R> runCatching(block: () -> R): Result<R> = try {
    Result.success(block())
} catch (e: Throwable) {
    Result.failure(e)
}

@Deprecated("Use doTry.", ReplaceWith("doTry(block)"))
inline fun <T, R> T.runCatching(block: T.() -> R): Result<R> = try {
    Result.success(block())
} catch (e: Throwable) {
    Result.failure(e)
}

