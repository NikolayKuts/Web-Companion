plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("custom-gitHub-pages-publishing")
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
                implementation(project(":yandexDictionaryCore"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
    }
}

customGitHubPagesPublishing {
    groupId = "com.yandex.dictionary"
    artifactId = "client"
    version = "1.0.0"

    pon(
        name = "Yandex Dictionary Client KMP Library",
        description = "Alows to fetch words info from the Yandex Dictionary",
        inceptionYear = "2025"
    )
}
