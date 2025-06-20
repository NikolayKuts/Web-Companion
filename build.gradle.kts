// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false

    kotlin("multiplatform") version "1.9.0" apply false
}

tasks.register("publishAllCambridgeApiToGithub") {
    group = "publishing"
    description = "Publishes all modules to GitHub Packages"

    dependsOn(
        ":cambridgeClient:publish",
        ":cambridgeCore:publish"
    )
}

tasks.register("publishAllYandexDictinaryApiToGithub") {
    group = "publishing"
    description = "Publishes all modules to GitHub Packages"

    dependsOn(
        ":yandexDictionaryClient:publish",
        ":yandexDictionaryCore:publish"
    )
}