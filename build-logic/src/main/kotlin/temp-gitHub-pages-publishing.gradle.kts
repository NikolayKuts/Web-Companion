//import org.gradle.api.artifacts.repositories.PasswordCredentials
//import org.gradle.kotlin.dsl.maven
//import org.gradle.kotlin.dsl.version
//
//plugins {
//    id("com.vanniktech.maven.publish")
//}
//
//publishing {
//    repositories {
//        maven {
//            name = "githubPackages"
//            url = uri("https://maven.pkg.github.com/NikolayKuts/Web-Companion")
//            credentials(PasswordCredentials::class)
//        }
//    }
//}
//
//mavenPublishing {
//    // Define coordinates for the published artifact
//    coordinates(
//        groupId = "com.yandex.dictionary",
//        artifactId = "core",
//        version = "1.0.0"
//    )
//
//    // Configure POM metadata for the published artifact
//    pom {
//        name.set("Ynadex Dictionary Core KMP Library")
//        description.set("Provides Yandex Dictionary entities")
//        inceptionYear.set("2025")
//        url.set("https://github.com/NikolayKuts/Web-Companion")
//
////        licenses {
////            license {
////                name.set("MIT")
////                url.set("https://opensource.org/licenses/MIT")
////            }
////        }
//
//        // Specify developers information
////        developers {
////            developer {
////                id.set("<GITHUB_USER_NAME>")
////                name.set("<GITHUB_ACTUAL_NAME>")
////                email.set("<GITHUB_EMAIL_ADDRESS>")
////            }
////        }
//
//        // Specify SCM information
//        scm {
//            url.set("https://github.com/NikolayKuts/Web-Companion")
//        }
//    }
//}