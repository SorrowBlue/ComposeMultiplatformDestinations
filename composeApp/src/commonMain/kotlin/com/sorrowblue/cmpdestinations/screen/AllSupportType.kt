package com.sorrowblue.cmpdestinations.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.cmpdestinations.result.NavResultSender
import kotlinx.serialization.Serializable

@Serializable
internal data class AllSupportType(
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
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AllSupportType

        if (int != other.int) return false
        if (long != other.long) return false
        if (float != other.float) return false
        if (boolean != other.boolean) return false
        if (!intArray.contentEquals(other.intArray)) return false
        if (!longArray.contentEquals(other.longArray)) return false
        if (!floatArray.contentEquals(other.floatArray)) return false
        if (!booleanArray.contentEquals(other.booleanArray)) return false
        if (string != other.string) return false
        if (!arrayString.contentEquals(other.arrayString)) return false
        if (listInt != other.listInt) return false
        if (listLong != other.listLong) return false
        if (listFloat != other.listFloat) return false
        if (listBoolean != other.listBoolean) return false
        if (listString != other.listString) return false
        if (serializableType != other.serializableType) return false
        if (enumType != other.enumType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = int
        result = 31 * result + long.hashCode()
        result = 31 * result + float.hashCode()
        result = 31 * result + boolean.hashCode()
        result = 31 * result + intArray.contentHashCode()
        result = 31 * result + longArray.contentHashCode()
        result = 31 * result + floatArray.contentHashCode()
        result = 31 * result + booleanArray.contentHashCode()
        result = 31 * result + string.hashCode()
        result = 31 * result + arrayString.contentHashCode()
        result = 31 * result + listInt.hashCode()
        result = 31 * result + listLong.hashCode()
        result = 31 * result + listFloat.hashCode()
        result = 31 * result + listBoolean.hashCode()
        result = 31 * result + listString.hashCode()
        result = 31 * result + serializableType.hashCode()
        result = 31 * result + enumType.hashCode()
        return result
    }
}

@Destination<AllSupportType>
@Composable
internal fun AllSupportTypeScreen(
    route: AllSupportType,
    sender: NavResultSender<String>,
) {
    Scaffold {
        val typeList = remember { route.typeList() }
        LazyColumn {
            items(typeList) {
                ListItem(
                    overlineContent = {
                        Text(it.first)
                    },
                    headlineContent = {
                        Text(it.second)
                    },
                    modifier = Modifier.clickable {
                        sender.navigateBack(it.second)
                    }
                )
            }
        }
    }
}

private fun AllSupportType.typeList(): List<Pair<String, String>> {
    return buildList {
        add("int" to int.toString())
        add("intArray" to intArray.joinToString(","))
        add("long" to long.toString())
        add("longArray" to longArray.joinToString(","))
        add("float" to float.toString())
        add("floatArray" to floatArray.joinToString(","))
        add("boolean" to boolean.toString())
        add("booleanArray" to booleanArray.joinToString(","))
        add("string" to string)
        add("arrayString" to arrayString.joinToString(","))
        add("listInt" to listInt.joinToString(","))
        add("listLong" to listLong.joinToString(","))
        add("listFloat" to listFloat.joinToString(","))
        add("listBoolean" to listBoolean.joinToString(","))
        add("listString" to listString.joinToString(","))
        add("serializableType" to serializableType.toString())
        add("enumType" to enumType.toString())
    }
}
