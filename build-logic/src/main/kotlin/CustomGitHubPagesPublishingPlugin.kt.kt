import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.provider.Property
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.credentials
import kotlin.reflect.KProperty

class CustomGitHubPagesPublishingPlugin : Plugin<Project> {

    private lateinit var extension: GitHubPagesPublishingExtensions

    override fun apply(project: Project) = with(project) {
        applyPlugins()
        createExtensions()

        afterEvaluate {
            extensions.configure<PublishingExtension> {
                repositories {
                    maven {
                        name = "githubPackages"
                        url = uri("https://maven.pkg.github.com/NikolayKuts/Web-Companion")
                        credentials(PasswordCredentials::class)
                    }
                }
            }

            extensions.configure<MavenPublishBaseExtension> {
                coordinates(
                    groupId = extension.groupId,
                    artifactId = extension.artifactId,
                    version = extension.version,
                )

                // Configure POM metadata for the published artifact
                pom {
                    name.set(extension.name)
                    description.set(extension.description)
                    inceptionYear.set(extension.inceptionYear)
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
        }
    }

    private fun Project.applyPlugins() {
        pluginManager.apply("com.vanniktech.maven.publish")
    }

    private fun Project.createExtensions() = extensions.create(
        "customGitHubPagesPublishing",
        GitHubPagesPublishingExtensions::class.java,
        this
    ).also { extension = it }
}

open class GitHubPagesPublishingExtensions(project: Project) {

    var groupId by StringProperty(project, "empty")

    var artifactId by StringProperty(project, "empty")

    var version by StringProperty(project, "empty")

    var name: String = "empty"
        private set
    var description: String = "empty"
        private set
    var inceptionYear: String = "empty"
        private set

    fun pon(
        name: String,
        description: String,
        inceptionYear: String
    ) {
        this.name = name
        this.description = description
        this.inceptionYear = inceptionYear
    }
}

internal class StringProperty<T>(
    project: Project,
    default: String
) {
    private val property: Property<String> = project.objects.property(String::class.java).apply {
        set(default)
    }

    operator fun getValue(thisRef: T, property: KProperty<*>): String =
        this.property.get()

    operator fun setValue(thisRef: T, property: KProperty<*>, value: String) =
        this.property.set(value)
}