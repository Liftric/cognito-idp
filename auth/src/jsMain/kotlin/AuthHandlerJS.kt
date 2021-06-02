@file:JsExport

import com.liftric.auth.AuthHandler
import com.liftric.auth.Configuration
import com.liftric.auth.base.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import kotlin.js.Promise

/**
 * Typescript compatible [AuthHandler] implementation.
 */
class AuthHandlerJS(origin: String, regionString: String, clientId: String) {
    private val region = Region.values().first { it.code == regionString }
    private val handler: AuthHandler = AuthHandler(Configuration(origin, region, clientId))

    fun signUp(username: String, password: String, attributes: Array<UserAttribute>? = null): Promise<SignUpResponse> =
        MainScope().promise {
            handler.signUp(username,password, attributes?.toList())
                .getOrThrow()
        }
}

// custom POJOs for unsupported
