rootProject.name = "cognito-idp"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            version("android-tools-gradle", "8.1.0")
            version("kotlin", "1.9.10")
            version("ktor", "2.3.3")

            library("ktor-client-core", "io.ktor", "ktor-client-core").versionRef("ktor")
            library("ktor-client-android", "io.ktor", "ktor-client-android").versionRef("ktor")
            library("ktor-client-darwin", "io.ktor", "ktor-client-darwin-legacy").versionRef("ktor")
            library("ktor-client-jvm", "io.ktor", "ktor-client-java").versionRef("ktor")
            library("ktor-client-js", "io.ktor", "ktor-client-js").versionRef("ktor")
            library("kotlinx-coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").version("1.7.3")
            library("kotlinx-serialization", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.6.0")
            library("androidx-test-core", "androidx.test", "core").version("1.2.0")
            library("roboelectric", "org.robolectric", "robolectric").version("4.9")
            library("opt-java", "com.github.bastiaanjansen", "otp-java").version("1.3.2")

            plugin("vault-client", "com.liftric.vault-client-plugin").version("2.0.0")
            plugin("versioning", "net.nemerosa.versioning").version("3.0.0")
            plugin("npm-publishing", "dev.petuska.npm.publish").version("3.0.0")
            plugin("definitions", "io.github.turansky.kfc.definitions").version("5.50.0")
            plugin("kotlin.serialization", "org.jetbrains.kotlin.plugin.serialization").versionRef("kotlin")
        }
    }
}
