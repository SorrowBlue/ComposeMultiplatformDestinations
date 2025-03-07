import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.cmpdestinations.detekt)
}

kotlin {

    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }

    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    jvm("desktop")
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(projects.cmpDestinations)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.androidx.navigation.composeMultiplatform)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

android {
    namespace = "com.sorrowblue.cmpdestinations"

    defaultConfig {
        applicationId = "com.sorrowblue.cmpdestinations"
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
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

dependencies {
    debugImplementation(compose.uiTooling)
    add("kspAndroid", projects.cmpDestinationsKsp)
    add("kspIosX64", projects.cmpDestinationsKsp)
    add("kspIosArm64", projects.cmpDestinationsKsp)
    add("kspIosSimulatorArm64", projects.cmpDestinationsKsp)
    add("kspDesktop", projects.cmpDestinationsKsp)
}

compose.desktop {
    application {
        mainClass = "com.sorrowblue.cmpdestinations.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.sorrowblue.cmpdestinations"
            packageVersion = "1.0.0"
        }
    }
}
