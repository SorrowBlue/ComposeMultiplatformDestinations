plugins {
    `kotlin-dsl`
    alias(libs.plugins.detekt)
}

group = "com.sorrowblue.cmpdestinations.buildlogic"

kotlin {
    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

dependencies {
    compileOnly(files(currentLibs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.detekt.plugin)
    implementation(libs.mavenPublish.plugin)
    detektPlugins(libs.detekt.compose)
    detektPlugins(libs.detekt.formatting)
}

gradlePlugin {
    plugins {
        register(libs.plugins.cmpdestinations.publish) {
            implementationClass = "PublishConversionPlugin"
        }
        register(libs.plugins.cmpdestinations.detekt) {
            implementationClass = "DetektConversionPlugin"
        }
    }
}
detekt {
    buildUponDefaultConfig = true
    autoCorrect = true
    config.setFrom(layout.projectDirectory.file("../config/detekt/detekt.yml"))
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(false)
        md.required.set(false)
        sarif.required.set(true)
        txt.required.set(false)
        xml.required.set(false)
    }
}


// Temporarily set to PushMode only
private val currentLibs get() = libs

private fun NamedDomainObjectContainer<PluginDeclaration>.register(
    provider: Provider<PluginDependency>,
    function: PluginDeclaration.() -> Unit,
) = register(provider.get().pluginId) {
    id = name
    function()
}
