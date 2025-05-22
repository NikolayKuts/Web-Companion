import org.gradle.jvm.tasks.Jar

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("maven-publish")
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
//    jvm()
    jvmToolchain(11)

    androidTarget {
        publishLibraryVariants("release")
    }

    sourceSets {
        val ktor_version = "2.3.13"

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
//                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

                implementation("io.ktor:ktor-client-core:$ktor_version")
                implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
    }
}

group = "com.cambridgeDictionary"

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
//                groupId = "com.cambridge"
                artifactId = "lib"
                version = "1.0.0"

                from(components["release"])
                artifact(tasks.named("sourcesJar"))
            }
        }

        repositories {
            mavenLocal()
        }
    }
}


tasks.named<Jar>("sourcesJar") {
    from(kotlin.sourceSets["commonMain"].kotlin)
}

