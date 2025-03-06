import com.vanniktech.maven.publish.SonatypeHost
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.report.ReportMergeTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.detekt)
    alias(libs.plugins.maven.publish)
}

group = "com.sorrowblue.cmpdestinations"
version = "0.0.1-SNAPSHOT"

kotlin {
    jvm {
        withJava()
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.ksp.symbolProcessingApi)
                implementation(libs.squareup.kotlinpoetKsp)
            }
        }
    }

    compilerOptions {
        allWarningsAsErrors.set(true)
    }
}

dependencies {
    detektPlugins(libs.compose.rules.detekt)
    detektPlugins(libs.arturbosch.detektFormatting)
}

detekt {
    buildUponDefaultConfig = true
    autoCorrect = true
    basePath = rootProject.projectDir.absolutePath
    config.setFrom("${rootProject.projectDir}/config/detekt/detekt.yml")
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
}
reportMerge.configureEach {
    input.from(tasks.withType<Detekt>().map(Detekt::sarifReportFile))
}
tasks.register("detektAll") {
    group = "verification"
    dependsOn(tasks.withType<Detekt>())
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    coordinates(group.toString(), "cmp-destinations-ksp", version.toString())

    pom {
        name.set("Compose Multiplatform Navigation Destinations Library")
        description.set("Library description")
        inceptionYear.set("2025")
        url.set("https://github.com/SorrowBlue/ComposeMultiplatformDestinations")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
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
            developerConnection.set("scm:git:ssh://git@github.com/SorrowBlue/ComposeMultiplatformDestinations.git")
        }
    }
}
