package com.liftric.cognito.idp

import io.ktor.utils.io.errors.*
import com.liftric.cognito.idp.core.Result
import com.liftric.cognito.idp.core.onFailure
import com.liftric.cognito.idp.core.onResult
import com.liftric.cognito.idp.core.onSuccess
import kotlin.test.*

class ResultTests {
    @Test
    fun testGetSuccess() {
        val expected = "Test"
        val result: Result<String> = Result.success(expected)
        assertEquals(expected, result.getOrNull())
    }

    @Test
    fun testGetFailure() {
        val expected = IOException("No connectivity")
        val result: Result<String> = Result.failure(expected)
        assertEquals(expected, result.exceptionOrNull())
    }

    @Test
    fun testGetFailureShouldThrow() {
        val expected = IOException("No connectivity")
        assertFailsWith(IOException::class) {
            Result.failure<String>(expected).getOrThrow()
        }
    }

    @Test
    fun testOnSuccess() {
        val expected = "Test"
        var result: String? = null
        Result.success(expected).onSuccess {
            result = it
        }
        assertEquals(expected, result)
    }

    @Test
    fun testOnFailure() {
        val expected = IOException("No connectivity")
        var result: Throwable? = null
        Result.failure<String>(expected).onFailure {
            result = it
        }
        assertEquals(expected, result)
    }

    @Test
    fun testFoldOnSuccess() {
        val expected = "Test"
        var result: String? = null
        Result.success(expected).fold(
            onSuccess = {
                result = it
            },
            onFailure = {
                fail("onFailure() should not get called")
            }
        )
        assertEquals(expected, result)
    }

    @Test
    fun testDoTryAndFold() {
        Result.doTry { "1" }
            .andThen { "${it}2" }
            .andThen { it.toInt() }
            .andThen { it * 2 }
            .fold(onFailure = {fail("shouldn't fail!")}, onSuccess = { assertEquals(24, it) })
    }

    @Test
    fun testAndThenOnSuccess() {
        var result: String? = null
        Result.success("Hans")
            .andThen { "$it Wurst" }
            .fold(
                onSuccess = {
                    result = it
                },
                onFailure = {
                    fail("onFailure() should not get called")
                }
            )
        assertEquals("Hans Wurst", result)
    }

    @Test
    fun testAndThenOnFailure() {
        val exception = Exception("firstException")
        val failedAndThen = Result.failure<String>(exception)
            .andThen {
                Result.success("won't happen")
            }

        assertEquals(true, failedAndThen.isFailure)
        assertEquals(exception, failedAndThen.exceptionOrNull())

        Result.success("Test")
            .andThen {
                error("sad")
            }.fold(onSuccess = {
                fail("onSuccess() should not get called")
            }, onFailure = {
                assertEquals("sad" ,it.message)
            })
    }

    @Test
    fun testFoldOnFailure() {
        val expected = IOException("No connectivity")
        var result: Throwable? = null
        Result.failure<String>(expected).fold(
            onSuccess = {
                fail("onSuccess() should not get called")
            },
            onFailure = {
                result = it
            }
        )
        assertEquals(expected, result)
    }

    @Test
    fun testOnResultChainingSuccess() {
        val expected = "Test"
        val result = Result.success(expected)
            .onResult {
                Result.success(expected.repeat(2))
            }.onResult {
                Result.success(expected.repeat(3))
            }
        assertEquals(expected.repeat(3), result.getOrNull())
        assertNull(result.exceptionOrNull())
    }

    @Test
    fun testOnResultChainingSuccessAfterFailure() {
        val result = Result.failure<String>(IOException("No connectivity"))
            .onResult {
                Result.failure<String>(IOException("No connectivity"))
            }.onResult {
                Result.success("Test")
            }
        assertEquals(null, result.getOrNull())
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun testOnResultChainingFailure() {
        val expected = IOException("No connectivity")
        val result = Result.success("Test")
            .onResult {
                Result.failure<String>(expected)
            }
        assertEquals(expected, result.exceptionOrNull())
        assertNull(result.getOrNull())
    }
}
