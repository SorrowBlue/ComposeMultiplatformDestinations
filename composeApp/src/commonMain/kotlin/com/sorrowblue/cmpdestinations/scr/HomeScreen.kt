package com.sorrowblue.cmpdestinations.scr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.cmpdestinations.annotation.DestinationInGraph
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.cmpdestinations.annotation.NestedNavGraph
import kotlinx.serialization.Serializable

@Serializable
data class SeriType(
    val int: Int,
)

enum class Types {
    Nomal,
    Hard,
}

@Serializable
data class Home(
    val int: Int,
    val intArray: IntArray,
    val long: Long,
    val longArray: LongArray,
    val float: Float,
    val floatArray: FloatArray,
    val boolean: Boolean,
    val booleanArray: BooleanArray,
    val string: String,
    val arrayString: Array<String>,
    val listInt: List<Int>,
    val listLong: List<Long>,
    val listFloat: List<Float>,
    val listBoolean: List<Boolean>,
    val listString: List<String>,
    val ks: SeriType,
    val types: Types,
)

@Serializable
data class Home2(
    val int: Int,
    val intArray: IntArray,
    val long: Long,
    val longArray: LongArray,
    val float: Float,
    val floatArray: FloatArray,
    val boolean: Boolean,
    val booleanArray: BooleanArray,
    val string: String,
    val arrayString: Array<String>,
    val listInt: List<Int>,
    val listLong: List<Long>,
    val listFloat: List<Float>,
    val listBoolean: List<Boolean>,
    val listString: List<String>,
    val ks: SeriType,
)

@Composable
@Destination<Home2>
fun HomeScreen2() {
    LaunchedEffect(Unit) {
        println("HomeScreen2")
    }
}

@Composable
@Destination<Home>
fun HomeScreen() {
    LaunchedEffect(Unit) {
        println("HomeScreen")
    }
}

@Serializable
data object A1

@Serializable
data object A2

@Serializable
data object A3

@Serializable
data object A4

@Serializable
data object A5

@Composable
@Destination<A1>
fun AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA1Screen() = Unit

@Composable
@Destination<A2>
fun AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA1Scree2() = Unit

@Composable
@Destination<A3>
fun AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA1Scree3() = Unit

@Composable
@Destination<A4>
fun AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA1Scree4() = Unit

@Composable
@Destination<A5>
fun AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA1Scree5() = Unit

@Serializable
data object Second

@Composable
@Destination<Second>
fun SecondScreen(route: Second) {
    LaunchedEffect(Unit) {
        println("SecondScreen(route: $route)")
    }
}

@NavGraph(startDestination = Home::class)
@Serializable
data object MainNavGraph {

    @NestedNavGraph<SecondNavGraph>
    @DestinationInGraph<Home>
    object Include
}

@NavGraph(startDestination = Second::class)
@Serializable
data object SecondNavGraph {

    @DestinationInGraph<Second>
    @DestinationInGraph<Home>
    @DestinationInGraph<A1>
    @DestinationInGraph<A2>
    @DestinationInGraph<A3>
    @DestinationInGraph<A4>
    @DestinationInGraph<A5>
    object Include
}
