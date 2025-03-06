package com.sorrowblue.cmpdestinations.result

import com.sorrowblue.cmpdestinations.serializer.KSerializerHelper
import kotlin.reflect.KClass

internal fun <T> resultKey(
    route: KClass<out Any>,
    kSerializerHelper: KSerializerHelper<T>,
): String {
    return "nav-result-value@${route.qualifiedName}@${kSerializerHelper::class.qualifiedName}"
}

internal fun <T> cancelKey(
    route: KClass<out Any>,
    kSerializerHelper: KSerializerHelper<T>,
): String {
    return "nav-result-cancel@${route.qualifiedName}@${kSerializerHelper::class.qualifiedName}"
}
