import java.util.Date
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    id("kotlinx-serialization")
    id("com.jfrog.bintray") version "1.8.5"
    id("net.nemerosa.versioning") version "2.14.0"
}

kotlin {
    val buildForDevice = project.findProperty("device") as? Boolean ?: false
    val iosTarget = if (buildForDevice) iosArm64("ios") else iosX64("ios")
    iosTarget.binaries {
        framework {
            if (!buildForDevice) {
                embedBitcode("disable")
            }
        }
    }

    android {
        publishLibraryVariants("release")

    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(Libs.ktorCore)
                implementation(Libs.coroutinesCore)
                implementation(Libs.serializationCore)
                implementation(Libs.ktorSerializationCore)
                implementation(Libs.kVault)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libs.ktorAndroid)
                implementation(Libs.coroutinesAndroid)
                implementation(Libs.serializationAndroid)
                implementation(Libs.ktorSerializationAndroid)
                implementation(Libs.kVaultAndroid)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(TestLibs.RoboElectric) {
                    exclude("com.google.auto.service", "auto-service")
                }
                implementation(TestLibs.TestCore)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(Libs.ktorNative)
                implementation(Libs.coroutinesNative)
                implementation(Libs.serializationNative)
                implementation(Libs.ktorSerializationNative)
                implementation(Libs.kVaultNative)
            }
        }
        val iosTest by getting {
            dependencies { }
        }
    }
}

android {
    compileSdkVersion(Apps.compileSdk)

    defaultConfig {
        minSdkVersion(Apps.minSdk)
        targetSdkVersion(Apps.targetSdk)
        versionCode = Apps.versionCode
        versionName = Apps.versionName
        testInstrumentationRunner = "org.robolectric.RobolectricTestRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

val artifactName = "Auth"
val artifactGroup = "com.liftric"
val artifactVersion: String = with(versioning.info) {
    if (branch == "HEAD" && dirty.not()) {
        tag
    } else {
        full
    }
}

group = artifactGroup
version = artifactVersion

bintray {
    user = System.getenv("bintrayUser")
    key = System.getenv("bintrayApiKey")
    publish = true
    override = true

    pkg.apply {
        repo = "maven"
        name = artifactName
        userOrg = "liftric"
        vcsUrl = "https://github.com/Liftric/Auth"
        description = "Lightweight AWS cognito authentication client"
        setLabels("kotlin-multiplatform", "liftric", "kotlin-native", "aws-cognito", "cognito", "aws")
        setLicenses("MIT")
        desc = description
        websiteUrl = "https://github.com/Liftric/Auth"
        issueTrackerUrl = "https://github.com/Liftric/Auth/issues"

        version.apply {
            name = artifactVersion
            vcsTag = artifactVersion
            released = Date().toString()
        }
    }
}

afterEvaluate {
    project.publishing.publications.withType(MavenPublication::class.java).forEach {
        it.groupId = artifactGroup
        if (it.name.contains("metadata")) {
            it.artifactId = "${artifactName.toLowerCase()}-common"
        } else if (it.name.contains("android")) {
            it.artifactId = "${artifactName.toLowerCase()}-android"
        } else {
            it.artifactId = "${artifactName.toLowerCase()}-${it.name}"
        }
    }
}

tasks.withType<BintrayUploadTask> {
    doFirst {
        val pubs = project.publishing.publications.map { it.name }.filter { it != "kotlinMultiplatform" }
        setPublications(*pubs.toTypedArray())
    }
}

tasks.withType<BintrayUploadTask> {
    dependsOn("publishToMavenLocal")
}