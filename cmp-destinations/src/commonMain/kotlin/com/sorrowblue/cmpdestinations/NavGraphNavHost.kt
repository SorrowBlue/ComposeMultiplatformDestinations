package com.sorrowblue.cmpdestinations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
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
    startDestination: KClass<*>? = null,
    isCompact: Boolean = false,
    contentAlignment: Alignment = Alignment.TopStart,
) {
    val navTransition = remember { graphNavigation.transitions }
    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = startDestination ?: graphNavigation.startDestination,
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
                isCompact = isCompact
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
) {
    val navTransition = graphNavigation.transitions
    navigation(
        startDestination = graphNavigation.startDestination,
        route = graphNavigation.route,
        typeMap = graphNavigation.typeMap,
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
                isCompact = isCompact
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

private fun NavGraphBuilder.screenDestination(
    screenDestination: Destination,
    navController: NavController,
    isCompact: Boolean,
    navTransitions: NavTransitions,
) {
    when (screenDestination.style) {
        DestinationStyle.Composable ->
            addComposable(
                screenDestination = screenDestination as ScreenDestination,
                navController = navController,
                navTransitions = navTransitions
            )

        DestinationStyle.Dialog ->
            addDialog(screenDestination = screenDestination, navController = navController)

        DestinationStyle.Auto -> {
            if (isCompact) {
                addComposable(
                    screenDestination = screenDestination as ScreenDestination,
                    navController = navController,
                    navTransitions = navTransitions
                )
            } else {
                addDialog(screenDestination = screenDestination, navController = navController)
            }
        }
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
            // TODO("たぶんViewModelとかでrouteを取得するときに必要？")
            //  rememberKoinModules { listOf(module { single { screenDestination.typeMap } }) }
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
) {
    destination(
        DialogNavigatorDestinationBuilder(
            navigator = provider[DialogNavigator::class],
            route = screenDestination.route,
            typeMap = screenDestination.typeMap,
            dialogProperties = DialogProperties(),
        ) {
            with(screenDestination) {
                Content(backStackEntry = it, navController = navController)
            }
        }
    )
}
