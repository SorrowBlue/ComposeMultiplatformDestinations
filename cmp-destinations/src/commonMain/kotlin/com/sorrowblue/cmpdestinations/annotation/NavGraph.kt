package com.sorrowblue.cmpdestinations.annotation

import com.sorrowblue.cmpdestinations.animation.NavTransitions
import kotlin.reflect.KClass

/**
 * Annotation to define a navigation graph.
 *
 * By applying this annotation to a class, the class is registered as a
 * navigation graph. You can specify the start destination, destinations,
 * nested graphs, and transitions for the graph.
 *
 * @property startDestination The start destination of the navigation
 *    graph.
 * @property destinations An array of destinations associated with the
 *    navigation graph.
 * @property nestedGraphs An array of nested graphs associated with the
 *    navigation graph.
 * @property transitions The transitions to be used for the navigation
 *    graph.
 */
@Target(AnnotationTarget.CLASS)
@Repeatable
@Retention(AnnotationRetention.SOURCE)
annotation class NavGraph(
    val startDestination: KClass<*>,
    val destinations: Array<KClass<*>> = [],
    val nestedGraphs: Array<KClass<*>> = [],
    val transitions: KClass<out NavTransitions> = NavTransitions.Default::class,
)
