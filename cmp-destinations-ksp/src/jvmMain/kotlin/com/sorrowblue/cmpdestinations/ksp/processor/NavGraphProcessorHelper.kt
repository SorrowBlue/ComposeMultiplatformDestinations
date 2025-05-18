package com.sorrowblue.cmpdestinations.ksp.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import com.sorrowblue.cmpdestinations.animation.NavTransitions
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.cmpdestinations.ksp.generator.NavGraphGenerator
import com.sorrowblue.cmpdestinations.ksp.generator.NavGraphInfo
import com.sorrowblue.cmpdestinations.ksp.getArgument
import com.sorrowblue.cmpdestinations.ksp.getFirst
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toClassNameOrNull

internal class NavGraphProcessorHelper(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : ProcessorHelper {

    private val typeMapHelper = TypeMapHelper(codeGenerator, logger)

    private val generator = NavGraphGenerator(codeGenerator)

    val navGraphCanonicalName = NavGraph::class.asClassName().canonicalName
    var graphDestination: Map<ClassName, List<KSType>> = mapOf()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(navGraphCanonicalName, true)
            .filterIsInstance<KSClassDeclaration>()
        val (processable, skip) = symbols.partition(KSClassDeclaration::validate)
        processable.forEach { ksClass ->
            val expectGraph = ksClass.modifiers.contains(Modifier.EXPECT)
            val isInternal = ksClass.modifiers.contains(Modifier.INTERNAL)
            logger.info(
                "@NavGraph ${ksClass.simpleName.asString()} expectGraph: $expectGraph",
                ksClass
            )

            if (expectGraph) {
                if (!ksClass.superTypes.any {
                        it.resolve().toClassNameOrNull() == ClassName(
                            "com.sorrowblue.cmpdestinations",
                            "GraphNavigation"
                        )
                    }
                ) {
                    logger.error("@NavGraph expectGraph must be super GraphNavigation", ksClass)
                }
            }

            val (graph, graphAnnotation) = ksClass.annotations.getFirst(navGraphCanonicalName)

            val destinations = graphAnnotation.getArgument<List<KSType>>("destinations").orEmpty() +
                graphDestination[ksClass.toClassName()].orEmpty()
            logger.info(
                "@NavGraph destinations = ${destinations.joinToString(",")}",
                ksClass
            )

            typeMapHelper.process(ksClass)

            val nestedGraphs = graphAnnotation.getArgument<List<KSType>>("nestedGraphs").orEmpty()
            logger.info("@NavGraph nestedGraphs = ${nestedGraphs.joinToString(",")}", ksClass)
            val startDestination = graphAnnotation.getArgument<KSType>(NavGraph::startDestination.name)!!
            val transitions = graphAnnotation.getArgument<KSType>(NavGraph::transitions.name)?.toClassNameOrNull()
                ?: NavTransitions.Default::class.asClassName()

            val name = ksClass.toClassName().let {
                ClassName(
                    it.packageName,
                    if (expectGraph) it.simpleName else it.simpleName + "Navigation"
                )
            }

            generator.generate(
                NavGraphInfo(
                    name = name,
                    isActual = expectGraph,
                    isInternal = isInternal,
                    startDestination = startDestination.toClassName(),
                    route = ksClass.asType(emptyList()),
                    destinations = destinations,
                    nestedGraphs = nestedGraphs,
                    transitions = transitions
                )
            )
        }
        return skip
    }
}
