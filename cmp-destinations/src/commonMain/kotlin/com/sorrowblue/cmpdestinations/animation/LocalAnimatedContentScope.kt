package com.sorrowblue.cmpdestinations.animation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.staticCompositionLocalOf

val LocalAnimatedContentScope = staticCompositionLocalOf<AnimatedContentScope> {
    throw IllegalStateException("CompositionLocal AnimatedContentScope not present")
}
