package com.sorrowblue.cmpdestinations.screen

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import com.sorrowblue.cmpdestinations.GraphNavigation
import com.sorrowblue.cmpdestinations.ScreenDestination
import com.sorrowblue.cmpdestinations.animation.NavTransitions
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlinx.serialization.Serializable

@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@NavGraph(
    startDestination = ObjectRoute::class,
    destinations = [
        AllSupportType::class,
        ObjectRoute::class,
        DeeplinkRoute::class
    ],
    nestedGraphs = [
        NestedGraph::class
    ]
)
expect object MainGraph : GraphNavigation {
    override val typeMap: Map<KType, NavType<*>>
    override val destinations: Array<ScreenDestination>
    override val nestedGraphs: Array<GraphNavigation>
    override val route: KClass<*>
    override val startDestination: KClass<*>
    override val transitions: NavTransitions
}

@NavGraph(
    startDestination = Foo::class,
    destinations = [
        Foo::class,
    ]
)
@Serializable
data class NestedGraph(
    val int: Int = 0,
    val intArray: IntArray = intArrayOf(1, 1, 1),
    val long: Long = 1,
    val longArray: LongArray = longArrayOf(1, 1, 1),
    val float: Float = 1.0f,
    val floatArray: FloatArray = floatArrayOf(1.0f, 1.0f, 1.0f),
    val boolean: Boolean = true,
    val booleanArray: BooleanArray = booleanArrayOf(false, true),
    val string: String = "Hello",
    val arrayString: Array<String> = arrayOf("World", "Kotlin"),
    val listInt: List<Int> = listOf(1, 1, 1),
    val listLong: List<Long> = listOf(1L, 1L, 1L),
    val listFloat: List<Float> = listOf(1.0f, 1.0f, 1.0f),
    val listBoolean: List<Boolean> = listOf(true, false),
    val listString: List<String> = listOf("Kotlin", "Compose"),
    val serializableType: SerializableType = SerializableType(1),
    val enumType: EnumType = EnumType.Nomal,
)

@Serializable
internal data class Foo(val string: String)

@Destination<Foo>
@Composable
internal fun FooScreen(route: Foo) {
    Scaffold {
        Text("Hello Foo: ${route.string}")
    }
}
