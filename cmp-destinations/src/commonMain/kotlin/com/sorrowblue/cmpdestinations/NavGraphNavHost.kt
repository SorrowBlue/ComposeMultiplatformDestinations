package com.sorrowblue.cmpdestinations

import androidx.compose.runtime.Composable
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
import com.sorrowblue.cmpdestinations.animation.NavTransitions
import kotlin.reflect.KClass

@Composable
fun NavGraphNavHost(
    navGraph: NavGraph,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: KClass<*>? = null,
    isCompact: Boolean = false,
    contentAlignment: Alignment = Alignment.TopStart,
) {
    val navTransition = remember { navGraph.transitions }
    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = startDestination ?: navGraph.startDestination,
        modifier = modifier,
        contentAlignment = contentAlignment,
        route = navGraph.route,
        enterTransition = { with(navTransition) { enterTransition() } },
        exitTransition = { with(navTransition) { exitTransition() } },
        popEnterTransition = { with(navTransition) { popEnterTransition() } },
        popExitTransition = { with(navTransition) { popExitTransition() } },
        sizeTransform = { with(navTransition) { sizeTransform() } },
    ) {
        navGraph.nestedNavGraphs.forEach {
            navGraphNavigation(navGraph = it, navController = navController, isCompact = isCompact)
        }
        navGraph.screenDestinations.forEach {
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
    navGraph: NavGraph,
    navController: NavController,
    isCompact: Boolean,
) {
    val navTransition = navGraph.transitions
    navigation(
        startDestination = navGraph.startDestination,
        route = navGraph.route,
        typeMap = navGraph.typeMap,
        enterTransition = { with(navTransition) { enterTransition() } },
        exitTransition = { with(navTransition) { exitTransition() } },
        popEnterTransition = { with(navTransition) { popEnterTransition() } },
        popExitTransition = { with(navTransition) { popExitTransition() } },
        sizeTransform = { with(navTransition) { sizeTransform() } },
    ) {
        navGraph.nestedNavGraphs.forEach {
            navGraphNavigation(navGraph = it, navController = navController, isCompact = isCompact)
        }
        navGraph.screenDestinations.forEach {
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
    screenDestination: ScreenDestination,
    navController: NavController,
    isCompact: Boolean,
    navTransitions: NavTransitions,
) {
    when (screenDestination.style) {
        DestinationStyle.Composable ->
            addComposable(
                screenDestination = screenDestination,
                navController = navController,
                navTransitions = navTransitions
            )

        DestinationStyle.Dialog ->
            addDialog(screenDestination = screenDestination, navController = navController)

        DestinationStyle.Auto -> {
            if (isCompact) {
                addComposable(
                    screenDestination = screenDestination,
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
            with(screenDestination) {
                it.Content(navController = navController)
            }
        }.apply {
            this.enterTransition = { with(navTransitions) { enterTransition() } }
            this.exitTransition = { with(navTransitions) { exitTransition() } }
            this.popEnterTransition = { with(navTransitions) { popEnterTransition() } }
            this.popExitTransition = { with(navTransitions) { popExitTransition() } }
            this.sizeTransform = { with(navTransitions) { sizeTransform() } }
        }
    )
}

private fun NavGraphBuilder.addDialog(
    screenDestination: ScreenDestination,
    navController: NavController,
) {
    destination(
        DialogNavigatorDestinationBuilder(
            navigator = provider[DialogNavigator::class],
            route = screenDestination.route,
            typeMap = screenDestination.typeMap,
            dialogProperties = DialogProperties(),
        ) {
            // TODO("たぶんViewModelとかでrouteを取得するときに必要？")
            //  rememberKoinModules { listOf(module { single { screenDestination.typeMap } }) }
            with(screenDestination) {
                it.Content(navController = navController)
            }
        }
    )
}
