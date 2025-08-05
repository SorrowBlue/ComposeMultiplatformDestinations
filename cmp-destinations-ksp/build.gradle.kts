plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.cmpdestinations.detekt)
    alias(libs.plugins.cmpdestinations.publish)
    alias(libs.plugins.versions)
}

kotlin {
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.ksp.symbolProcessingApi)
                implementation(libs.squareup.kotlinpoetKsp)
                implementation(libs.kotlinx.serialization)

                implementation(projects.cmpDestinations) {
                    exclude(group = "org.jetbrains.skiko")
                }
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.androidx.navigationCompose)
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
