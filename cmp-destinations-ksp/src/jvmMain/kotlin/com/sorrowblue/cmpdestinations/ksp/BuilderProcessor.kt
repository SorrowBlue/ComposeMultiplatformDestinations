package com.sorrowblue.cmpdestinations.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.sorrowblue.cmpdestinations.ksp.processor.DestinationProcessorHelper
import com.sorrowblue.cmpdestinations.ksp.processor.NavGraphProcessorHelper

internal class BuilderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private val context = ProcessingContext(logger)

    private val destinationProcessorHelper =
        DestinationProcessorHelper(codeGenerator, logger)

    private val navGraphProcessorHelper = NavGraphProcessorHelper(codeGenerator, logger)

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("ModuleName: ${resolver.getModuleName().asString()}")
        val resolvedDestinations = destinationProcessorHelper.process(resolver)
        navGraphProcessorHelper.graphDestination = destinationProcessorHelper.routeMappings
        val resolvedNavGraph = navGraphProcessorHelper.process(resolver)
        return resolvedDestinations + resolvedNavGraph
    }

    override fun finish() {
        super.finish()
        context.clear()
    }
}
