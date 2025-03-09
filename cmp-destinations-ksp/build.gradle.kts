plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.cmpdestinations.detekt)
    alias(libs.plugins.cmpdestinations.publish)
}

kotlin {
    jvm()
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

    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}

cmpDestinationsPublishing {
    group = "com.sorrowblue.cmpdestinations"
    artifactId = "cmp-destinations-ksp"
}
