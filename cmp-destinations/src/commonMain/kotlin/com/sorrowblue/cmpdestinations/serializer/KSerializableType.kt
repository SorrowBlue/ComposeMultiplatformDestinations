package com.sorrowblue.cmpdestinations.serializer

import androidx.core.bundle.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.serializer

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified D : @Serializable Any> NavType.Companion.kSerializableType(
    isNullableAllowed: Boolean = false,
): KSerializableType<D> {
    return KSerializableType(Cbor.serializersModule.serializer<D>(), isNullableAllowed)
}

@OptIn(ExperimentalSerializationApi::class)
class KSerializableType<D : @Serializable Any?>(
    private val serializer: KSerializer<D>,
    isNullableAllowed: Boolean = false,
) : NavType<D>(isNullableAllowed) {
    override val name get() = serializer.descriptor.serialName

    override fun get(bundle: Bundle, key: String): D? {
        return bundle.getByteArray(key)?.let {
            Cbor.decodeFromByteArray(serializer, it)
        }
    }

    override fun put(bundle: Bundle, key: String, value: D) {
        bundle.putByteArray(key, Cbor.encodeToByteArray(serializer, value))
    }

    override fun serializeAsValue(value: D): String {
        return Cbor.encodeToHexString(serializer, value)
    }

    override fun parseValue(value: String): D {
        return Cbor.decodeFromHexString(serializer, value)
    }
}
