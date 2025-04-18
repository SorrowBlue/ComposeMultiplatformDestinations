package com.sorrowblue.cmpdestinations.ksp.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

internal class BuilderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("ModuleName: ${resolver.getModuleName().asString()}")
        return resolveDestination(codeGenerator, logger, resolver) + resolveNavGraph(
            codeGenerator,
            logger,
            resolver
        )
    }
}
