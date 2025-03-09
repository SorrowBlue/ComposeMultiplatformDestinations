package com.sorrowblue.cmpdestinations.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

inline fun <reified T> kSerializerHelper() =
    KSerializerHelper(Json.serializersModule.serializer<T>())

class KSerializerHelper<T>(private val serializer: KSerializer<T>) {
    fun toJsonString(value: T): String = Json.encodeToString(serializer, value)
    fun fromJsonString(json: String): T = Json.decodeFromString(serializer, json)
}
