object Apps {
    const val compileSdk = 28
    const val minSdk = 21
    const val targetSdk = 28
    const val versionCode = 1
    const val versionName = "1.0.0"
}

object Versions {
    const val gradle = "3.6.4"
    const val kotlin = "1.3.72"
    const val coroutines = "1.3.8"
    const val serialization = "0.20.0"
    const val ktor = "1.3.2"
    const val TestCore = "1.2.0"
}

object Libs {
    const val kotlnGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val serialization = "org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}"
    const val gradleAndroid = "com.android.tools.build:gradle:${Versions.gradle}"
    const val kotlinAndroid = "org.jetbrains.kotlin:kotlin-android-extensions:${Versions.kotlin}"

    /* Common */
    const val ktorCore = "io.ktor:ktor-client-core:${Versions.ktor}"
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val serializationCore = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:${Versions.serialization}"
    const val ktorSerializationCore = "io.ktor:ktor-client-serialization:${Versions.ktor}"

    /* Android */
    const val ktorAndroid = "io.ktor:ktor-client-android:${Versions.ktor}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val serializationAndroid = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.serialization}"
    const val ktorSerializationAndroid = "io.ktor:ktor-client-serialization-jvm:${Versions.ktor}"

    /* iOS */
    const val ktorNative = "io.ktor:ktor-client-ios:${Versions.ktor}"
    const val coroutinesNative = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.coroutines}"
    const val serializationNative = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:${Versions.serialization}"
    const val ktorSerializationNative = "io.ktor:ktor-client-serialization-native:${Versions.ktor}"
}

object TestLibs {
    const val TestCore = "androidx.test:core:${Versions.TestCore}"
}
