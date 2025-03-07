package com.sorrowblue.cmpdestinations

actual fun getPlatform(): Platform = object : Platform {
    override val name: String = "Web with Kotlin/Wasm"
}
