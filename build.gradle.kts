buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Libs.kotlinGradle)
        classpath(Libs.serialization)
        classpath(Libs.gradleAndroid)
        classpath(Libs.kotlinAndroid)
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
