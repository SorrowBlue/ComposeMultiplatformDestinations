package com.sorrowblue.cmpdestinations

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
