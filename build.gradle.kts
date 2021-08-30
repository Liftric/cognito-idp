import com.liftric.vault.GetVaultSecretTask
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon

plugins {
    id("com.android.library") version Versions.gradle
    kotlin("multiplatform") version Versions.kotlin
    id("com.github.turansky.kfc.definitions") version Versions.definitions // fixes Promise in generated typescript files
    id("maven-publish")
    id("dev.petuska.npm.publish") version Versions.npmPublish
    id("org.jetbrains.kotlin.plugin.serialization") version Versions.kotlin
    id("net.nemerosa.versioning") version "2.14.0"
    id("signing")
    id("com.liftric.vault-client-plugin") version "2.0.0"
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    ios {
        binaries.framework()
    }
    android {
        publishLibraryVariants("debug", "release")
    }
    jvm()
    js(IR) {
        browser {
            testTask {
                useMocha {
                    timeout = "10s"
                }
            }
        }
        binaries.library()
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
            kotlin.srcDir("${buildDir}/gen")
            dependencies {
                implementation(kotlin("test"))
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
        val jvmMain by getting {
            dependencies {
                implementation(Libs.ktorJvm)
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
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(Libs.ktorIOS)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(Libs.ktorJS)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

android {
    compileSdk = Apps.compileSdk

    defaultConfig {
        minSdk = Apps.minSdk
        targetSdk = Apps.targetSdk
        testInstrumentationRunner = Android.TestRunner
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    testOptions {
        unitTests.apply {
            isReturnDefaultValues = true
        }
    }
}

group = "com.liftric"
version = with(versioning.info) {
    if (branch == "HEAD" && dirty.not()) tag else full
}

afterEvaluate {
    project.publishing.publications.withType(MavenPublication::class.java).forEach {
        it.groupId = group.toString()
    }
}

tasks {
    val iosX64Test by existing(KotlinNativeSimulatorTest::class) {
        filter.excludeTestsMatching("com.liftric.cognito.idp.IdentityProviderClientTests")
    }

    val testSecrets by creating(GetVaultSecretTask::class) {
        secretPath.set("secret/apps/smartest/shared/cognito")
    }

    val createJsEnvHack by creating {
        outputs.dir("$buildDir/gen")

        if (System.getenv("region") == null || System.getenv("clientId") == null) {
            // github ci provides region and clientId envs, locally we'll use vault directly
            dependsOn(testSecrets)
        }

        doFirst {
            val (clientId, region) = with(testSecrets.secret.get()) {
                ((System.getenv("clientId") ?: this["client_id_dev"].toString()) to
                        (System.getenv("region") ?: this["client_region_dev"].toString()))
            }

            mkdir("$buildDir/gen")
            with(File("$buildDir/gen/env.kt")) {
                createNewFile()
                writeText(
                    """
                    val env = mapOf(
                        "region" to "$region",
                        "clientId" to "$clientId",
                    )
                    """.trimIndent()
                )
            }
        }
    }

    withType<KotlinCompileCommon> {
        dependsOn(createJsEnvHack)
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            languageVersion = "1.5"
            freeCompilerArgs = listOf(
                "-Xinline-classes",
                "-Xuse-experimental=kotlin.js.ExperimentalJsExport"
            )
        }
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
            name.set(project.name)
            description.set("Lightweight AWS Cognito Identity Provider client for Kotlin Multiplatform projects.")
            url.set("https://github.com/liftric/cognito-idp")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://github.com/liftric/cognito-idp/blob/master/LICENSE")
                }
            }
            developers {
                developer {
                    id.set("gaebel")
                    name.set("Jan Gaebel")
                    email.set("gaebel@liftric.com")
                }
                developer {
                    id.set("benjohnde")
                    name.set("Ben John")
                    email.set("john@liftric.com")
                }
                developer {
                    id.set("ingwersaft")
                    name.set("Marcel Kesselring")
                    email.set("kesselring@liftric.com")
                }
            }
            scm {
                url.set("https://github.com/liftric/cognito-idp")
            }
        }
    }
}

val npmAccessKey: String? by project

npmPublishing {
    organization = "liftric"
    access = PUBLIC
    readme = rootProject.file("README.md")

    publications {
        val js by getting {
            moduleName = project.name
            packageJson {
                keywords = jsonArray(
                    "kotlin",
                    "cognito",
                    "identity-provider",
                    "liftric",
                    "aws"
                )
                license = "MIT"
                description = "Lightweight AWS Cognito Identity Provider client."
                homepage = "https://github.com/liftric/cognito-idp"
                bugs = mutableMapOf<String, Any?>().apply {
                    put("url", "https://github.com/liftric/cognito-idp/issues")
                }
            }
        }
    }

    repositories {
        repository("npmjs") {
            registry = uri("https://registry.npmjs.org")
            authToken = npmAccessKey
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}

vault {
    vaultAddress.set("https://dark-lord.liftric.io")
    if (System.getenv("CI") == null) {
        vaultTokenFilePath.set("${System.getProperty("user.home")}/.vault-token")
    } else {
        vaultToken.set(System.getenv("VAULT_TOKEN"))
    }
}
