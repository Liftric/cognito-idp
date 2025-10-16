package com.liftric.cognito.idp

import com.bastiaanjansen.otp.HMACAlgorithm
import com.bastiaanjansen.otp.TOTPGenerator
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Duration

@RunWith(RobolectricTestRunner::class)
actual class IdentityProviderClientTests: AbstractIdentityProviderClientTests() {
    override fun generateTotpCode(secret: String): String? {
        val builder = TOTPGenerator.Builder(secret.toByteArray())
        builder
            .withHOTPGenerator { hotpGenerator ->
                hotpGenerator.withPasswordLength(6)
                hotpGenerator.withAlgorithm(HMACAlgorithm.SHA1) // SHA256 and SHA512 are also supported
            }
            .withPeriod(Duration.ofSeconds(30))

        val totp = builder.build()
        return totp.now()
    }
}

actual fun runTest(block: suspend () -> Unit) = runBlocking {
    block.invoke()
}