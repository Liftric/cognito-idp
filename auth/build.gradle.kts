import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import java.util.*

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("maven-publish")
    id("kotlinx-serialization")
    id("com.jfrog.bintray") version "1.8.5"
    id("net.nemerosa.versioning") version "2.14.0"
}

kotlin {
    ios()

    android {
        publishLibraryVariants("debug", "release")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Libs.ktorCore)
                implementation(Libs.coroutinesCore)
                implementation(Libs.serializationCore)
                implementation(Libs.ktorSerializationCore)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(Libs.coroutinesCore)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libs.ktorAndroid)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(TestLibs.RoboElectrics) {
                    exclude(Exclude.GoogleAutoService, Exclude.AutoService)
                }
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(TestLibs.TestCore)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(Libs.ktorIOS)
            }
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
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
        unitTests.apply {
            isReturnDefaultValues = true
        }
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
        description = "Lightweight AWS Cognito client for Kotlin Multiplatform projects"
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
    }
}

tasks.withType<BintrayUploadTask> {
    doFirst {
        // https://github.com/bintray/gradle-bintray-plugin/issues/229
        project.publishing.publications.withType(MavenPublication::class.java).forEach {
            val moduleFile = buildDir.resolve("publications/${it.name}/module.json")
            if (moduleFile.exists()) {
                it.artifact(object : org.gradle.api.publish.maven.internal.artifact.FileBasedMavenArtifact(moduleFile) {
                    override fun getDefaultExtension() = "module"
                })
            }
        }
        val pubs = project.publishing.publications.map { it.name }
        setPublications(*pubs.toTypedArray())
    }
}

tasks.withType<BintrayUploadTask> {
    dependsOn("publishToMavenLocal")
}
