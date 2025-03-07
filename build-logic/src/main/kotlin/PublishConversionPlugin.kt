import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.RepositoryBuilder
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.the

interface PublishConversionPluginExtension {
    val group: Property<String>
    val artifactId: Property<String>
}

fun Project.cmpDestinationsPublishing(configure: Action<PublishConversionPluginExtension>): Unit =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure(
        "cmpDestinationsPublishing",
        configure
    )

internal class PublishConversionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.mavenPublish.get().pluginId)
            }

            // Set version from git tag
            val repository = RepositoryBuilder()
                .setGitDir(target.rootDir.resolve(".git"))
                .readEnvironment()
                .findGitDir()
                .build()
            val git = Git(repository)
            val tagsduty = git.describe().setTags(true).setAbbrev(1).call()
            val tags = git.describe().setTags(true).setAbbrev(0).call()
            target.version = when {
                tags.isNullOrEmpty() -> "0.0.0-SNAPSHOT"
                tags == tagsduty -> target.version = tags
                else -> target.version = "$tags-SNAPSHOT"
            }

            val extension =
                project.extensions.create<PublishConversionPluginExtension>("cmpDestinationsPublishing")

            extensions.configure<PublishingExtension> {
                repositories {
                    mavenLocal()
                }
            }

            extensions.configure<MavenPublishBaseExtension> {
                publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, true)

                afterEvaluate {
                    coordinates(
                        extension.group.get(),
                        extension.artifactId.get(),
                        version.toString()
                    )
                }

                pom {
                    afterEvaluate {
                        this@pom.name.set(extension.artifactId.get())
                    }
                    description.set(
                        "Annotation processing library for type-safe Jetpack Compose navigation with no boilerplate."
                    )
                    inceptionYear.set("2025")
                    url.set("https://github.com/SorrowBlue/ComposeMultiplatformDestinations")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            distribution.set("repo")
                        }
                    }
                    developers {
                        developer {
                            id.set("sorrowblue")
                            name.set("Sorrow Blue")
                            url.set("https://github.com/SorrowBlue")
                        }
                    }
                    scm {
                        url.set("https://github.com/SorrowBlue/ComposeMultiplatformDestinations")
                        connection.set("scm:git:git://github.com/SorrowBlue/ComposeMultiplatformDestinations.git")
                        developerConnection.set(
                            "scm:git:ssh://git@github.com/SorrowBlue/ComposeMultiplatformDestinations.git"
                        )
                    }
                }
            }
        }
    }
}
