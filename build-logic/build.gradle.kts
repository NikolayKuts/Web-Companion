plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("custom-gitHub-pages-publishing") {
            id = "custom-gitHub-pages-publishing"
            implementationClass = "CustomGitHubPagesPublishingPlugin"
        }
    }
}

dependencies {
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.28.0")
}