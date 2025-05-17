package com.sorrowblue.cmpdestinations

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface ScreenDestination {
    val route: KClass<*>
    val typeMap: Map<KType, NavType<*>>
    val style: DestinationStyle
    val deepLinks: Array<NavDeepLink>
    val wrappers: Array<out DestinationWrapper>

    @Composable
    fun NavBackStackEntry.Content(navController: NavController)
}
