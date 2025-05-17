package com.sorrowblue.cmpdestinations.annotation

import com.sorrowblue.cmpdestinations.DestinationStyle
import com.sorrowblue.cmpdestinations.DestinationWrapper
import kotlin.reflect.KClass

/**
 * Annotation to define a navigation destination.
 *
 * By applying this annotation to a function, the function is registered
 * as a navigation destination. You can specify the name, style, and deep
 * links for the destination.
 *
 * @param T The route of the navigation destination. Must be a subclass of
 *    `Any`.
 * @property style The style of the navigation destination. Defaults to
 *    `DestinationStyle.Composable`.
 * @property deeplinks An array of deep links associated with the
 *    navigation destination.
 * @see [com.sorrowblue.cmpdestinations.DestinationStyle]
 * @see [DeepLink]
 */
@Target(AnnotationTarget.FUNCTION)
@Repeatable
@Retention(AnnotationRetention.SOURCE)
annotation class Destination<T : Any>(
    val style: KClass<out DestinationStyle> = DestinationStyle.Composable::class,
    val graph: KClass<*> = Nothing::class,
    val deeplinks: Array<DeepLink> = [],
    val wrappers: Array<KClass<out DestinationWrapper>> = [],
)
