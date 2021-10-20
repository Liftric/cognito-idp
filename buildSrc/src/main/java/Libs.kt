object Apps {
    const val compileSdk = 30
    const val minSdk = 21
    const val targetSdk = 30
}

object Android {
    const val TestRunner = "org.robolectric.RobolectricTestRunner"
}

object Versions {
    const val gradle = "7.0.0"
    const val kotlin = "1.5.31"
    const val npmPublish = "2.0.3"
    const val definitions = "4.40.0"
    const val coroutines = "1.5.1-native-mt"
    const val serialization = "1.2.2"
    const val ktor = "1.6.3"
    const val TestCore = "1.2.0"
    const val RoboElectric = "4.5.1"
}

object Libs {
    const val serialization = "org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}"

    /* Common */
    const val ktorCore = "io.ktor:ktor-client-core:${Versions.ktor}"
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val serializationCore = "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.serialization}"
    const val ktorSerializationCore = "io.ktor:ktor-client-serialization:${Versions.ktor}"

    /* Android */
    const val ktorAndroid = "io.ktor:ktor-client-android:${Versions.ktor}"

    /* JVM */
    const val ktorJvm = "io.ktor:ktor-client-cio:${Versions.ktor}"

    /* iOS */
    const val ktorIOS = "io.ktor:ktor-client-ios:${Versions.ktor}"

    /* JS */
    const val ktorJS = "io.ktor:ktor-client-js:${Versions.ktor}"
}

object TestLibs {
    const val TestCore = "androidx.test:core:${Versions.TestCore}"
    const val RoboElectrics = "org.robolectric:robolectric:${Versions.RoboElectric}"
}

object Exclude {
    const val GoogleAutoService = "com.google.auto.service"
    const val AutoService = "auto-service"
}
