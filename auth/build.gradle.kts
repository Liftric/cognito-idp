import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.signing

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("maven-publish")
    id("kotlinx-serialization")
    id("net.nemerosa.versioning") version "2.14.0"
    id("signing")
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
        testInstrumentationRunner = Android.TestRunner
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

afterEvaluate {
    project.publishing.publications.withType(MavenPublication::class.java).forEach {
        it.groupId = artifactGroup
    }
}

tasks {
    val iosX64Test by existing(KotlinNativeSimulatorTest::class) {
        filter.excludeTestsMatching("com.liftric.auth.AuthHandlerIntegrationTests")
    }
}

val ossrhUsername: String? by project
val ossrhPassword: String? by project

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    repositories {
        maven {
            name = "sonatype"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }

    publications.withType<MavenPublication> {

        artifact(javadocJar.get())

        pom {
            name.set(artifactName)
            description.set("Lightweight AWS Cognito client for Kotlin Multiplatform projects.")
            url.set("https://github.com/Liftric/Auth")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://github.com/Liftric/Auth/blob/master/LICENSE")
                }
            }
            developers {
                developer {
                    id.set("gaebel")
                    name.set("Jan Gaebel")
                    email.set("gaebel@liftric.com")
                }
            }
            scm {
                url.set("https://github.com/Liftric/Auth")
            }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}