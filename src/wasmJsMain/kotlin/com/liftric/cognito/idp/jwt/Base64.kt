package com.liftric.cognito.idp.jwt

import kotlinx.browser.window

internal actual class Base64 {
	actual companion object {
		actual fun decode(input: String): String? {
			// Validate the input to ensure it is proper Base64
			if (!input.matches(Regex("^[A-Za-z0-9+/]*={0,2}\$"))) {
				return null
			}

			return try {
				// Decode using the browser's atob function
				val decoded = window.atob(input)

				// Re-encode the result to compare with input (remove padding)
				val reencoded = window.btoa(decoded)
					.replace(Regex("=+\$"), "")

				if (input.replace(Regex("=+\$"), "") != reencoded) null else decoded
			} catch (e: Throwable) {
				// Return null if decoding fails
				null
			}
		}
	}
}