package com.sorrowblue.cmpdestinations.screen

import androidx.navigation.NavType
import com.sorrowblue.cmpdestinations.ScreenDestination
import com.sorrowblue.cmpdestinations.animation.NavTransitions
import com.sorrowblue.cmpdestinations.annotation.DestinationInGraph
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlinx.serialization.Serializable

@NavGraph(startDestination = ObjectRoute::class, root = MainNavGraphImpl::class)
@Serializable
data object MainNavGraph {

    @DestinationInGraph<AllSupportType>
    @DestinationInGraph<ObjectRoute>
    @DestinationInGraph<DeeplinkRoute>
    object Include
}

@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class MainNavGraphImpl() : com.sorrowblue.cmpdestinations.NavGraph {

    override val startDestination: KClass<*>
    override val route: KClass<*>
    override val typeMap: Map<KType, NavType<*>>
    override val screenDestinations: List<ScreenDestination>
    override val nestedNavGraphs: List<com.sorrowblue.cmpdestinations.NavGraph>
    override val transitions: NavTransitions
}
