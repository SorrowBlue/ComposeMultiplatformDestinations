package com.sorrowblue.cmpdestinations.annotation

import com.sorrowblue.cmpdestinations.DestinationStyle
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Destination<T : Any>(
    val name: String = COMPOSABLE_NAME,
    val style: KClass<out DestinationStyle> = DestinationStyle.Composable::class,
) {
    companion object {
        const val COMPOSABLE_NAME = "@cmpdestinations.generated@"
    }
}
