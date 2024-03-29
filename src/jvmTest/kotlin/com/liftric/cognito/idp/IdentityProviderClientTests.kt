package com.liftric.cognito.idp

import com.bastiaanjansen.otp.HMACAlgorithm
import com.bastiaanjansen.otp.TOTP
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import kotlin.text.toByteArray
import java.time.Duration


actual class IdentityProviderClientTests: AbstractIdentityProviderClientTests() {
    override fun generateTotpCode(secret: String): String? {
        val builder = TOTP.Builder(secret.toByteArray())
        builder
            .withPasswordLength(6)
            .withAlgorithm(HMACAlgorithm.SHA1) // SHA256 and SHA512 are also supported
            .withPeriod(Duration.ofSeconds(30))

        val totp = builder.build()
        return totp.now()
    }
}

actual fun runTest(block: suspend () -> Unit) = runBlocking {
    block.invoke()
}
