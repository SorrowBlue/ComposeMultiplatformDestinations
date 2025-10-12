package com.sorrowblue.cmpdestinations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.ComposeNavigatorDestinationBuilder
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.compose.DialogNavigatorDestinationBuilder
import androidx.navigation.compose.navigation
import androidx.navigation.get
import com.sorrowblue.cmpdestinations.animation.LocalAnimatedContentScope
import com.sorrowblue.cmpdestinations.animation.NavTransitions
import kotlin.reflect.KClass

@Composable
fun NavGraphNavHost(
    graphNavigation: GraphNavigation,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: KClass<*> = graphNavigation.startDestination,
    isCompact: Boolean = false,
    contentAlignment: Alignment = Alignment.TopStart,
) {
    val navTransition =
        if (graphNavigation.transitions != NavTransitions.ApplyParent) graphNavigation.transitions else NavTransitions.Default
    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        contentAlignment = contentAlignment,
        route = graphNavigation.route,
        enterTransition = { with(navTransition) { enterTransition() } },
        exitTransition = { with(navTransition) { exitTransition() } },
        popEnterTransition = { with(navTransition) { popEnterTransition() } },
        popExitTransition = { with(navTransition) { popExitTransition() } },
        sizeTransform = { with(navTransition) { sizeTransform() } },
    ) {
        graphNavigation.nestedGraphs.forEach {
            navGraphNavigation(
                graphNavigation = it,
                navController = navController,
                isCompact = isCompact,
                parentNavTransitions = navTransition
            )
        }
        graphNavigation.destinations.forEach {
            screenDestination(
                screenDestination = it,
                navController = navController,
                isCompact = isCompact,
                navTransitions = navTransition
            )
        }
    }
}

@Composable
fun NavGraphNavHost(
    graphNavigation: GraphNavigation,
    navController: NavHostController,
    startDestination: Any,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    contentAlignment: Alignment = Alignment.TopStart,
) {
    val navTransition =
        if (graphNavigation.transitions != NavTransitions.ApplyParent) graphNavigation.transitions else NavTransitions.Default
    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        contentAlignment = contentAlignment,
        route = graphNavigation.route,
        enterTransition = { with(navTransition) { enterTransition() } },
        exitTransition = { with(navTransition) { exitTransition() } },
        popEnterTransition = { with(navTransition) { popEnterTransition() } },
        popExitTransition = { with(navTransition) { popExitTransition() } },
        sizeTransform = { with(navTransition) { sizeTransform() } },
    ) {
        graphNavigation.nestedGraphs.forEach {
            navGraphNavigation(
                graphNavigation = it,
                navController = navController,
                isCompact = isCompact,
                parentNavTransitions = navTransition
            )
        }
        graphNavigation.destinations.forEach {
            screenDestination(
                screenDestination = it,
                navController = navController,
                isCompact = isCompact,
                navTransitions = navTransition
            )
        }
    }
}

fun NavGraphBuilder.navGraphNavigation(
    graphNavigation: GraphNavigation,
    navController: NavController,
    isCompact: Boolean,
    parentNavTransitions: NavTransitions = NavTransitions.Default,
) {
    val navTransitions =
        if (graphNavigation.transitions != NavTransitions.ApplyParent) graphNavigation.transitions else parentNavTransitions
    navigation(
        startDestination = graphNavigation.startDestination,
        route = graphNavigation.route,
        typeMap = graphNavigation.typeMap,
        enterTransition = { with(navTransitions) { enterTransition() } },
        exitTransition = { with(navTransitions) { exitTransition() } },
        popEnterTransition = { with(navTransitions) { popEnterTransition() } },
        popExitTransition = { with(navTransitions) { popExitTransition() } },
        sizeTransform = { with(navTransitions) { sizeTransform() } },
    ) {
        graphNavigation.nestedGraphs.forEach {
            navGraphNavigation(
                graphNavigation = it,
                navController = navController,
                isCompact = isCompact,
                parentNavTransitions = navTransitions
            )
        }
        graphNavigation.destinations.forEach {
            screenDestination(
                screenDestination = it,
                navController = navController,
                isCompact = isCompact,
                navTransitions = navTransitions
            )
        }
    }
}

private fun NavGraphBuilder.screenDestination(
    screenDestination: Destination,
    navController: NavController,
    isCompact: Boolean,
    navTransitions: NavTransitions,
) {
    @Suppress("DEPRECATION")
    when (val style = screenDestination.style) {
        is DestinationStyle.Auto -> {
            if (isCompact) {
                addComposable(
                    screenDestination = screenDestination as ScreenDestination,
                    navController = navController,
                    navTransitions = navTransitions
                )
            } else {
                addDialog(
                    screenDestination = screenDestination,
                    navController = navController,
                    dialogProperties = style.dialogProperties
                )
            }
        }

        is DestinationComposableStyle ->
            addComposable(
                screenDestination = screenDestination as ScreenDestination,
                navController = navController,
                navTransitions = navTransitions
            )

        is DestinationDialogStyle ->
            addDialog(
                screenDestination = screenDestination,
                navController = navController,
                dialogProperties = style.dialogProperties
            )
    }
}

private fun NavGraphBuilder.addComposable(
    screenDestination: ScreenDestination,
    navController: NavController,
    navTransitions: NavTransitions,
) {
    destination(
        ComposeNavigatorDestinationBuilder(
            provider[ComposeNavigator::class],
            screenDestination.route,
            screenDestination.typeMap,
        ) {
            CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                with(screenDestination) {
                    Content(navController = navController, backStackEntry = it)
                }
            }
        }.apply {
            screenDestination.deepLinks.forEach {
                deepLink(it)
            }
            this.enterTransition = { with(navTransitions) { enterTransition() } }
            this.exitTransition = { with(navTransitions) { exitTransition() } }
            this.popEnterTransition = { with(navTransitions) { popEnterTransition() } }
            this.popExitTransition = { with(navTransitions) { popExitTransition() } }
            this.sizeTransform = { with(navTransitions) { sizeTransform() } }
        }
    )
}

private fun NavGraphBuilder.addDialog(
    screenDestination: Destination,
    navController: NavController,
    dialogProperties: DialogProperties,
) {
    destination(
        DialogNavigatorDestinationBuilder(
            navigator = provider[DialogNavigator::class],
            route = screenDestination.route,
            typeMap = screenDestination.typeMap,
            dialogProperties = dialogProperties,
        ) {
            with(screenDestination) {
                Content(backStackEntry = it, navController = navController)
            }
        }
    )
}
