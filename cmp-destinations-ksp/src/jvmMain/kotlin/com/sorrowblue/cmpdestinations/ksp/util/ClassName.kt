package com.sorrowblue.cmpdestinations.ksp.util

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

/**
 * @see [com.sorrowblue.cmpdestinations.annotation.Destination]
 */
internal val Destination = ClassName("com.sorrowblue.cmpdestinations.annotation", "Destination")

/**
 * @see [com.sorrowblue.cmpdestinations.annotation.NavGraph]
 */
internal val NavGraph = ClassName("com.sorrowblue.cmpdestinations.annotation", "NavGraph")

/**
 * @see [com.sorrowblue.cmpdestinations.NavGraph]
 */
internal val INavGraph = ClassName("com.sorrowblue.cmpdestinations", "NavGraph")

/**
 * @see [com.sorrowblue.cmpdestinations.annotation.DestinationInGraph]
 */
internal val DestinationInGraph =
    ClassName("com.sorrowblue.cmpdestinations.annotation", "DestinationInGraph")

/**
 * @see [com.sorrowblue.cmpdestinations.annotation.NestedNavGraph]
 */
internal val NestedNavGraph =
    ClassName("com.sorrowblue.cmpdestinations.annotation", "NestedNavGraph")

/**
 * @see [com.sorrowblue.cmpdestinations.animation.NavTransitions]
 */
internal val NavTransitions =
    ClassName("com.sorrowblue.cmpdestinations.animation", "NavTransitions")

/**
 * @see [kotlinx.serialization.Serializable]
 */
internal val Serializable = ClassName("kotlinx.serialization", "Serializable")

/**
 * @see [androidx.navigation.NavType]
 */
internal val NavType = ClassName("androidx.navigation", "NavType")

/**
 * @see [com.sorrowblue.cmpdestinations.ScreenDestination]
 */
internal val ScreenDestination = ClassName("com.sorrowblue.cmpdestinations", "ScreenDestination")

/**
 * @see [com.sorrowblue.cmpdestinations.DestinationStyle]
 */
internal val DestinationStyle =
    ClassName("com.sorrowblue.cmpdestinations", "DestinationStyle")

/**
 * @see [androidx.compose.runtime.Composable]
 */
internal val Composable = ClassName("androidx.compose.runtime", "Composable")

/**
 * @see [androidx.navigation.NavBackStackEntry]
 */
internal val NavBackStackEntry = ClassName("androidx.navigation", "NavBackStackEntry")

/**
 * @see [androidx.navigation.NavController]
 */
internal val NavController = ClassName("androidx.navigation", "NavController")

/**
 * @see [com.sorrowblue.cmpdestinations.result.navResultSender]
 */
internal val navResultSender =
    MemberName("com.sorrowblue.cmpdestinations.result", "navResultSender")

/**
 * @see [com.sorrowblue.cmpdestinations.result.NavResultSender]
 */
internal val NavResultSender =
    ClassName("com.sorrowblue.cmpdestinations.result", "NavResultSender")

/**
 * @see [com.sorrowblue.cmpdestinations.result.NavResultReceiver]
 */
internal val NavResultReceiver =
    ClassName("com.sorrowblue.cmpdestinations.result", "NavResultReceiver")

/**
 * @see [com.sorrowblue.cmpdestinations.result.navResultReceiver]
 */
internal val navResultReceiver =
    MemberName("com.sorrowblue.cmpdestinations.result", "navResultReceiver")

/**
 * @see [androidx.navigation.toRoute]
 */
internal val toRoute = MemberName("androidx.navigation", "toRoute")

/**
 * @see [com.sorrowblue.cmpdestinations.DestinationStyle.Composable]
 */
internal val DestinationStyle_Composable =
    ClassName("com.sorrowblue.cmpdestinations", "DestinationStyle", "Composable")
