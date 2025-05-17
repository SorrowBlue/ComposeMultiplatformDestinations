package com.sorrowblue.cmpdestinations.ksp.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated

internal interface ProcessorHelper {

    fun process(resolver: Resolver): List<KSAnnotated>
}
