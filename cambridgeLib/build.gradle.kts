plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("com.vanniktech.maven.publish") version "0.28.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

/** ./gradlew :cambridgeLib:assembleRelease **/
android {
    namespace = "com.cambridge.lib"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
    testOptions {
        targetSdk = 35
    }
    lint {
        targetSdk = 35
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    publishing {
        singleVariant("release") {
//            withSourcesJar()
        }
    }
}

kotlin {
    jvm()
    jvmToolchain(11)

    androidTarget {
        publishLibraryVariants("release")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
//                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
    }
}

publishing {
    repositories {
        maven {
            name = "githubPackages"
            url = uri("https://maven.pkg.github.com/NikolayKuts/Web-Companion")
            credentials(PasswordCredentials::class)
        }
    }
}

mavenPublishing {
    // Define coordinates for the published artifact
    coordinates(
        groupId = "com.cambridge.dictionary",
        artifactId = "client",
        version = "1.0.0"
    )

    // Configure POM metadata for the published artifact
    pom {
        name.set("Canbradge Dictionary Client KMP Library")
        description.set("Alows to fetch words info in the Cambridge Dictionary")
        inceptionYear.set("2025")
        url.set("https://github.com/NikolayKuts/Web-Companion")

//        licenses {
//            license {
//                name.set("MIT")
//                url.set("https://opensource.org/licenses/MIT")
//            }
//        }

        // Specify developers information
//        developers {
//            developer {
//                id.set("<GITHUB_USER_NAME>")
//                name.set("<GITHUB_ACTUAL_NAME>")
//                email.set("<GITHUB_EMAIL_ADDRESS>")
//            }
//        }

        // Specify SCM information
        scm {
            url.set("https://github.com/NikolayKuts/Web-Companion")
        }
    }
}
