buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("https://dl.bintray.com/jetbrains/kotlin-native-dependencies") }
    }
    dependencies {
        classpath(Libs.kotlnGradle)
        classpath(Libs.serialization)
        classpath(Libs.gradleAndroid)
        classpath(Libs.kotlinAndroid)
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        jcenter()
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
    }
}
