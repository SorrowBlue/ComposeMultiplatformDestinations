package com.sorrowblue.cmpdestinations.serializer

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

inline fun <reified D : @Serializable Any> NavType.Companion.kSerializableType(
    isNullableAllowed: Boolean = false,
): KSerializableType<D> {
    return KSerializableType(Json.serializersModule.serializer<D>(), isNullableAllowed)
}

class KSerializableType<D : @Serializable Any?>(
    private val serializer: KSerializer<D>,
    isNullableAllowed: Boolean = false,
) : NavType<D>(isNullableAllowed) {
    override val name get() = serializer.descriptor.serialName

    override fun get(bundle: SavedState, key: String): D? {
        return bundle.read { getString(key) }.let {
            Json.decodeFromString(serializer, it)
        }
    }

    override fun put(bundle: SavedState, key: String, value: D) {
        bundle.write {
            putString(key, Json.encodeToString(serializer, value))
        }
    }

    override fun serializeAsValue(value: D): String {
        return Json.encodeToString(serializer, value)
    }

    override fun parseValue(value: String): D {
        return Json.decodeFromString(serializer, value)
    }
}
