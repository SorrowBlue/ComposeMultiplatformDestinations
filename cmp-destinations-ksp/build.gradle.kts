import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.report.ReportMergeTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.cmpdestinations.detekt)
    alias(libs.plugins.cmpdestinations.publish)
}

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

cmpDestinationsPublishing {
    group = "com.sorrowblue.cmpdestinations"
    artifactId = "cmp-destinations-ksp"
}
