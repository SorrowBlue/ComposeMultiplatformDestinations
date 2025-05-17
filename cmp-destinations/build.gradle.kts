import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.cmpdestinations.detekt)
    alias(libs.plugins.cmpdestinations.publish)
}

kotlin {

    // JVM
    jvm()

    // JS
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
        d8()
    }

    // Android
    androidTarget {
        publishLibraryVariants("release")
    }

    // iOS
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlinx.serialization.json.internal.JsonFriendModuleApi")
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(libs.androidx.navigationCompose.multiplatform)
                implementation(libs.kotlinx.serializationJson)
                implementation(libs.kotlinx.io)
                implementation(libs.androidx.coreBundle)
                implementation(libs.androidx.lifecycleRuntimeCompose)
            }
        }
    }

    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}

android {
    namespace = "com.sorrowblue.cmpdestinations"

    lint {
        val isCI = System.getenv("CI").toBoolean()
        checkAllWarnings = true
        checkDependencies = true
        disable += listOf("InvalidPackage", "NewerVersionAvailable", "GradleDependency")
        baseline = project.file("lint-baseline.xml")
        htmlReport = !isCI
        htmlOutput =
            if (htmlReport) project.file("${project.rootDir}/build/reports/lint/lint-result.html") else null
        sarifReport = isCI
        sarifOutput =
            if (sarifReport) project.file("${project.rootDir}/build/reports/lint/lint-result.sarif") else null
        textReport = false
        xmlReport = false
    }
}

cmpDestinationsPublishing {
    group = "com.sorrowblue.cmpdestinations"
    artifactId = "cmp-destinations-core"
}
