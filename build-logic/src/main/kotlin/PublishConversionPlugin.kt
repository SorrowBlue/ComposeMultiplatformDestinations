import com.vanniktech.maven.publish.MavenPublishBaseExtension
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.of
import org.gradle.process.ExecOperations

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

            val gitTagProvider: Provider<String> = providers.of(GitTagValueSource::class) {}
            runCatching {
                val tag = checkNotNull(gitTagProvider.orNull) { "No git tag found." }
                val version = checkNotNull(releaseVersionOrSnapshot(tag.removePrefix("v"))) { "git tag is not valid." }
                target.version = version
            }.onFailure {
                logger.warn("Failed to get git tag. Using version 'UNKNOWN'.")
                target.version = "UNKNOWN"
            }

            val extension =
                project.extensions.create<PublishConversionPluginExtension>("cmpDestinationsPublishing")

            extensions.configure<PublishingExtension> {
                repositories {
                    mavenLocal()
                }
            }

            extensions.configure<MavenPublishBaseExtension> {
                afterEvaluate {
                    coordinates(
                        extension.group.get(),
                        extension.artifactId.get(),
                        version.toString()
                    )
                    logger.lifecycle("publish ${extension.group.get()}:${extension.artifactId.get()}:$version")
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
                        connection.set("scm:git:https://github.com/SorrowBlue/ComposeMultiplatformDestinations.git")
                        developerConnection.set(
                            "scm:git:ssh://git@github.com/SorrowBlue/ComposeMultiplatformDestinations.git"
                        )
                    }
                }
            }
        }
    }

    private fun releaseVersionOrSnapshot(tag: String): String? {
        val regex = Regex("""(^\d+\.\d+\.)(\d+)([\w-]*)$""")
        val groups = regex.find(tag)?.groups ?: return null
        return if (groups.size == 4) {
            if (groups[3]?.value?.isEmpty() == true) {
                groups.first()!!.value
            } else {
                "${groups[1]!!.value}${groups[2]!!.value.toInt().plus(1)}-SNAPSHOT"
            }
        } else {
            null
        }
    }
}

// パラメータは不要だが、インターフェースとして定義が必要
private interface GitTagParameters : ValueSourceParameters

// Gitコマンドを実行して最新タグを取得するValueSource
private abstract class GitTagValueSource @Inject constructor(
    private val execOperations: ExecOperations,
) : ValueSource<String, GitTagParameters> {

    override fun obtain(): String {
        return try {
            // 標準出力をキャプチャするためのByteArrayOutputStream
            val stdout = ByteArrayOutputStream()
            // git describe コマンドを実行
            val result = execOperations.exec {
                // commandLine("git", "tag", "--sort=-creatordate") // もし作成日時順の最新タグが良い場合
                commandLine("git", "describe", "--tags", "--abbrev=1")
                standardOutput = stdout
                // エラーが発生してもGradleビルドを止めないようにし、戻り値で判断
                isIgnoreExitValue = true
                // エラー出力は捨てる (必要ならキャプチャも可能)
                errorOutput = ByteArrayOutputStream()
            }

            if (result.exitValue == 0) {
                // 成功したら標準出力をトリムして返す
                stdout.toString().trim().removePrefix("v")
            } else {
                // gitコマンド失敗時 (タグがない、gitリポジトリでない等)
                println("Warning: Could not get git tag. (Exit code: ${result.exitValue})")
                "UNKNOWN" // または適切なデフォルト値
            }
        } catch (e: Exception) {
            // その他の予期せぬエラー
            println("Warning: Failed to execute git command: ${e.message}")
            "UNKNOWN" // または適切なデフォルト値
        }
    }
}
