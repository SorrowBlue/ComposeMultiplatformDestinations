package com.sorrowblue.cmpdestinations

import androidx.navigation.NavType
import com.sorrowblue.cmpdestinations.animation.NavTransitions
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface GraphNavigation {
    val startDestination: KClass<*>
    val route: KClass<*>
    val typeMap: Map<KType, NavType<*>>
    val destinations: Array<Destination>
    val nestedGraphs: Array<GraphNavigation>
    val transitions: NavTransitions
}
