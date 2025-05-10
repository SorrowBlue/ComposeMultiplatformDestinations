![Maven Central Version](https://img.shields.io/maven-central/v/com.sorrowblue.cmpdestinations/cmp-destinations-core)
![GitHub top language](https://img.shields.io/github/languages/top/SorrowBlue/ComposeMultiplatformDestinations)
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/SorrowBlue/ComposeMultiplatformDestinations)


## Overview
ComposeMultiplatformDestinations is a navigation library for Compose Multiplatform applications that
enables type-safe navigation across Android, iOS, desktop, and web platforms. The library simplifies
the implementation of navigation in multiplatform projects by providing a code generation system
that creates type-safe navigation routes from annotated functions.

### Purpose and Scope
This document provides an overview of ComposeMultiplatformDestinations, explaining its architecture,
key components, and how they interact to provide a unified navigation experience across platforms.
For detailed installation and usage instructions, see Getting Started.

## Getting Started

This guide provides instructions for installing and setting up ComposeMultiplatformDestinations in
your project, along with basic usage examples to get you started with type-safe navigation in your
Compose Multiplatform applications. For more detailed information about navigation concepts, see
Core Navigation Concepts.

### Installation

ComposeMultiplatformDestinations consists of two main modules that need to be added to your project:
the core library and the KSP processor for code generation.

### Add Dependencies

Add the following dependencies to your module's build.gradle.kts file:
```kotlin
plugins {
    // Required plugins
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization") // Required for route arguments
    id("com.google.devtools.ksp") // Required for code generation
    // Other plugins for your project...
}
kotlin {
    // Your platform targets (Android, iOS, Desktop, Web)
    sourceSets {
        commonMain {
            dependencies {
                // Core library
                implementation("com.sorrowblue.cmpdestinations:cmp-destinations-core:VERSION")
                
                // Other dependencies...
            }
        }
    }

}

// KSP dependency for code generation
dependencies {
    add("kspAndroid", "com.sorrowblue.cmpdestinations:cmp-destinations-ksp:VERSION")
}
```
