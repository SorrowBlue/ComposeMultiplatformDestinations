package com.sorrowblue.cmpdestinations

import androidx.navigation.NavType
import com.sorrowblue.cmpdestinations.animation.NavTransitions
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface NavGraph {
    val startDestination: KClass<*>
    val route: KClass<*>
    val typeMap: Map<KType, NavType<*>>
    val screenDestinations: List<ScreenDestination>
    val nestedNavGraphs: List<NavGraph>
    val transitions: NavTransitions
}
