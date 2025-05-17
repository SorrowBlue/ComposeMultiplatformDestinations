package com.sorrowblue.cmpdestinations

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

interface DestinationWrapper {
    @Composable
    fun DestinationScope.Content(content: @Composable () -> Unit)
}

interface DestinationScope {

    val navBackStackEntry: NavBackStackEntry

    val navController: NavController

    @Composable
    fun WrapRecursively(
        wrappers: Array<out DestinationWrapper>,
        idx: Int,
        content: @Composable () -> Unit,
    ) {
        with(wrappers[idx]) {
            Content {
                if (idx < wrappers.lastIndex) {
                    WrapRecursively(wrappers, idx + 1, content)
                } else {
                    content()
                }
            }
        }
    }
}

class DestinationScopeImpl(
    override val navBackStackEntry: NavBackStackEntry,
    override val navController: NavController,
) : DestinationScope
