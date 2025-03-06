package com.sorrowblue.cmpdestinations.annotation

import com.sorrowblue.cmpdestinations.animation.NavTransitions
import kotlin.reflect.KClass

annotation class NavGraph(
    val startDestination: KClass<*>,
    val root: KClass<*> = Nothing::class,
    val transitions: KClass<out NavTransitions> = NavTransitions.Default::class,
)
