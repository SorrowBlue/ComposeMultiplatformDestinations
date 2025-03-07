package com.sorrowblue.cmpdestinations

import android.os.Build

actual fun getPlatform(): Platform = object : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}
