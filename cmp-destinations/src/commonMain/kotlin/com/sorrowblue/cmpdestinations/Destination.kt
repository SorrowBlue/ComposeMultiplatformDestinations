package com.sorrowblue.cmpdestinations

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import com.sorrowblue.cmpdestinations.animation.LocalAnimatedContentScope
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface Destination {
    val route: KClass<*>
    val typeMap: Map<KType, NavType<*>>
    val style: DestinationStyle
    val deepLinks: Array<NavDeepLink>
    val wrappers: Array<out DestinationWrapper>

    @Composable
    fun Content(backStackEntry: NavBackStackEntry, navController: NavController)
}

interface ScreenDestination : Destination {

    @Composable
    fun AnimatedContentScope.Content(backStackEntry: NavBackStackEntry, navController: NavController) {
        CompositionLocalProvider(LocalAnimatedContentScope provides this) {
            Content(backStackEntry, navController)
        }
    }
}
