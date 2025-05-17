import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

internal class DetektConversionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            with(pluginManager) {
                apply(libs.plugins.detekt.get().pluginId)
            }

            extensions.configure<DetektExtension> {
                buildUponDefaultConfig = true
                autoCorrect = true
                basePath = rootProject.projectDir.absolutePath
                config.setFrom("${rootProject.projectDir}/config/detekt/detekt.yml")
            }

            dependencies {
                add("detektPlugins", libs.detekt.compose)
                add("detektPlugins", libs.detekt.formatting)
            }

            val reportMerge = rootProject.tasks.withType(ReportMergeTask::class)
            tasks.withType<Detekt>().configureEach {
                reports {
                    sarif.required.set(true)
                    html.required.set(false)
                    md.required.set(false)
                    txt.required.set(false)
                    xml.required.set(false)
                }
                finalizedBy(reportMerge)
                exclude {
                    it.file.path.contains("generated")
                }
            }
            reportMerge.configureEach {
                input.from(tasks.withType<Detekt>().map(Detekt::sarifReportFile))
            }
            tasks.register("detektAll") {
                group = "verification"
                dependsOn(tasks.withType<Detekt>())
            }
        }
    }
}
