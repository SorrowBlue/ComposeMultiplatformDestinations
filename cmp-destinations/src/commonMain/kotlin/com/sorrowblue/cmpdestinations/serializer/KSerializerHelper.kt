package com.sorrowblue.cmpdestinations.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.serializer

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> kSerializerHelper() =
    KSerializerHelper(Cbor.serializersModule.serializer<T>())

@OptIn(ExperimentalSerializationApi::class)
class KSerializerHelper<T>(private val serializer: KSerializer<T>) {
    fun toByteArray(value: T): ByteArray = Cbor.encodeToByteArray(serializer, value)
    fun fromByteArray(bytes: ByteArray): T = Cbor.decodeFromByteArray(serializer, bytes)
}
