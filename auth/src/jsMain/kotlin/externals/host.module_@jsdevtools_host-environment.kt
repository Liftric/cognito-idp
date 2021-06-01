@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*
import tsstdlib.Record

external interface Host {
    var global: Global
    var path: String
    var url: URL
    var cwd: String
    var cwdURL: URL
    var os: OSInfo
    var env: EnvironmentVariables
    var ci: dynamic /* Boolean | CIInfo */
        get() = definedExternally
        set(value) = definedExternally
    var node: dynamic /* Boolean | NodeInfo */
        get() = definedExternally
        set(value) = definedExternally
    var browser: dynamic /* Boolean | Browsers */
        get() = definedExternally
        set(value) = definedExternally
    fun <T> merge(props: T): Host /* Host & T */
    fun toJSON(): HostJSON
}

external interface HostJSON {
    var global: String
    var os: OSInfo
    var env: EnvironmentVariables
    var ci: dynamic /* Boolean | CIInfo */
        get() = definedExternally
        set(value) = definedExternally
    var node: dynamic /* Boolean | NodeInfo */
        get() = definedExternally
        set(value) = definedExternally
    var browser: dynamic /* Boolean | Browsers */
        get() = definedExternally
        set(value) = definedExternally
}

typealias Global = Record<String, Any>

external interface OSInfo {
    var windows: Boolean
    var mac: Boolean
    var linux: Boolean
}

external interface EnvironmentVariables {
    @nativeGetter
    operator fun get(key: String): String?
    @nativeSetter
    operator fun set(key: String, value: String?)
}

external interface CIInfo {
    var name: String
    var pr: Boolean
    var CODEBUILD: Boolean?
    var APPVEYOR: Boolean?
    var AZURE_PIPELINES: Boolean?
    var BAMBOO: Boolean?
    var BITBUCKET: Boolean?
    var BITRISE: Boolean?
    var BUDDY: Boolean?
    var BUILDKITE: Boolean?
    var CIRCLE: Boolean?
    var CIRRUS: Boolean?
    var CODESHIP: Boolean?
    var DRONE: Boolean?
    var DSARI: Boolean?
    var GITHUB_ACTIONS: Boolean?
    var GITLAB: Boolean?
    var GOCD: Boolean?
    var HUDSON: Boolean?
    var JENKINS: Boolean?
    var MAGNUM: Boolean?
    var NETLIFY: Boolean?
    var NEVERCODE: Boolean?
    var SAIL: Boolean?
    var SEMAPHORE: Boolean?
    var SHIPPABLE: Boolean?
    var SOLANO: Boolean?
    var STRIDER: Boolean?
    var TASKCLUSTER: Boolean?
    var TEAMCITY: Boolean?
    var TRAVIS: Boolean?
}

typealias NodeInfo = VersionInfo

external interface Browsers {
    var IE: dynamic /* Boolean | BrowserInfo */
        get() = definedExternally
        set(value) = definedExternally
    var edge: dynamic /* Boolean | BrowserInfo */
        get() = definedExternally
        set(value) = definedExternally
    var chrome: dynamic /* Boolean | BrowserInfo */
        get() = definedExternally
        set(value) = definedExternally
    var firefox: dynamic /* Boolean | BrowserInfo */
        get() = definedExternally
        set(value) = definedExternally
    var safari: dynamic /* Boolean | BrowserInfo */
        get() = definedExternally
        set(value) = definedExternally
    var mobile: Boolean
}

external interface BrowserInfo : VersionInfo {
    var mobile: Boolean
}

external interface VersionInfo {
    var version: Number
    var majorVersion: Number
    var minorVersion: Number
    var patchVersion: Number
    @nativeGetter
    operator fun get(vMajor: String): dynamic /* Boolean? | Any? */
    @nativeSetter
    operator fun set(vMajor: String, value: Boolean)
    @nativeSetter
    operator fun set(vMajor: String, value: Any)
}
