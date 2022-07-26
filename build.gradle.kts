import com.liftric.vault.GetVaultSecretTask
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon

plugins {
    id("com.android.library") version libs.versions.android.tools.gradle
    kotlin("multiplatform") version libs.versions.kotlin
    alias(libs.plugins.definitions)
    alias(libs.plugins.npm.publishing)
    alias(libs.plugins.versioning)
    alias(libs.plugins.vault.client)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
    id("signing")
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

    iosSimulatorArm64()

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
                implementation(libs.ktor.client.core)
                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.serialization)
            }
        }
        val commonTest by getting {
            kotlin.srcDir("${buildDir}/gen")
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.android)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.client.jvm)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(libs.roboelectric)
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(libs.androidx.test.core)
                implementation(libs.opt.java)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(libs.opt.java)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
        val jsMain by getting {
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        all {
            languageSettings {
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
                optIn("kotlin.js.ExperimentalJsExport")
                optIn("kotlin.RequiresOptIn")
            }
        }
    }
}

android {
    compileSdk = 30

    defaultConfig {
        minSdk = 21
        targetSdk = 30
        testInstrumentationRunner = "androidx.test.runner"
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
    withType(KotlinNativeSimulatorTest::class) {
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
                "-Xinline-classes"
            )
        }
    }

    /**
     * Ugly atomicfu export hack: Coroutines core needs atomicfu-js, which generates broken d.ts entries. Excluding
     * atomicfu-js breaks the ktor client and filtering d.ts types in the IR compile is currently not possible... so let's
     * just rip it out after the fact
     *
     * The build/js/packages/cognito-idp/kotlin/cognito-idp.d.ts file should now be without errors and usable from typescript
     */
    val cleanTypescriptTypes by creating {
        fun MutableList<String>.removeFromTill(from: String, till: String) {
            val fromIndex = indexOfFirst { it == from }
            val tillIndex = indexOfFirst { it == till }
            if (fromIndex == -1 || tillIndex == -1) {
                println("looks like from='$from' till='$till' already scraped")
                return
            }
            println("removeFromTill from='$from' till='$till' fromIndex=$fromIndex")
            println("removeFromTill from='$from' till='$till' tillIndex=$tillIndex")
            (fromIndex..tillIndex).forEach {
                removeAt(fromIndex)
            }
        }

        val dTsFile = file("$buildDir/js/packages/cognito-idp/kotlin/cognito-idp.d.ts")
        inputs.file(dTsFile)
        outputs.file(dTsFile)

        doLast {
            val dTs = dTsFile.readLines().toMutableList()
            dTs.removeFromTill("export namespace kotlinx.atomicfu {", "}")
            dTs.removeFromTill("export namespace io.ktor.util {", "}")
            dTsFile.writeText(dTs.joinToString("\n"))
        }
    }
    val jsBrowserProductionLibraryDistribution by existing {
        finalizedBy(cleanTypescriptTypes)
    }
    val jsProductionLibraryCompileSync by existing {
        finalizedBy(cleanTypescriptTypes)
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

npmPublish {
    organization.set("liftric")
    access.set(PUBLIC)
    readme.set(rootProject.file("README.md"))

    packages {
        named("js") {
            packageName.set(project.name)
            packageJson {
                keywords.set(listOf(
                    "kotlin",
                    "cognito",
                    "identity-provider",
                    "liftric",
                    "aws"
                ))
                license.set("MIT")
                description.set("Lightweight AWS Cognito Identity Provider client.")
                homepage.set("https://github.com/liftric/cognito-idp")
            }
        }
    }

    registries {
        npmjs {
            uri.set(uri("https://registry.npmjs.org"))
            authToken.set(npmAccessKey)
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
