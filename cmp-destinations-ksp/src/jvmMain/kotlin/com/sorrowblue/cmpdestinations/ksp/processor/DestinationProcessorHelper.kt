package com.sorrowblue.cmpdestinations.ksp.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueArgument
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.validate
import com.sorrowblue.cmpdestinations.DestinationStyle
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.cmpdestinations.ksp.generator.ScreenDestinationGenerator
import com.sorrowblue.cmpdestinations.ksp.getArgument
import com.sorrowblue.cmpdestinations.ksp.getFirst
import com.sorrowblue.cmpdestinations.ksp.isObjectClass
import com.sorrowblue.cmpdestinations.ksp.model.NavDeepLink
import com.sorrowblue.cmpdestinations.ksp.model.ScreenDestinationInfo
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toClassNameOrNull

internal class DestinationProcessorHelper(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : ProcessorHelper {

    private val generator = ScreenDestinationGenerator(codeGenerator)
    private val typeMapHelper = TypeMapHelper(codeGenerator, logger)

    val routeMappings = mutableMapOf<ClassName, MutableList<KSType>>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(Destination::class.qualifiedName!!, true)
            .filterIsInstance<KSFunctionDeclaration>()
        val (processable, skip) = symbols.partition(KSFunctionDeclaration::validate)
        processable.forEach { ksFunction ->
            logger.info("@Destination fun ${ksFunction.simpleName.asString()}", ksFunction)
            val (destination, destinationAnnotation) =
                ksFunction.annotations.getFirst(Destination::class.qualifiedName!!)

            // @Destination<Route>
            val route = destination.arguments.first().type!!.resolve()
            if (route.isSerializable) {
                typeMapHelper.process(route.declaration as KSClassDeclaration)
            } else {
                logger.error("    Route class(${route.toClassName().canonicalName}) must be @Serializable.", ksFunction)
            }

            val style = destinationAnnotation.getArgument<KSType>("style")?.toClassNameOrNull()
                ?: DestinationStyle.Composable::class.asClassName()

            val graph = (destinationAnnotation.getArgument<KSType>("graph"))?.toClassNameOrNull()
            if (graph != null && graph != Nothing::class.asClassName()) {
                routeMappings[graph]?.add(route) ?: routeMappings.put(graph, mutableListOf(route))
            }

            val deepLinks =
                (destinationAnnotation.arguments.get("deeplinks") as? ArrayList<*>)
                    ?.filterIsInstance<KSAnnotation>()
                    ?.map {
                        NavDeepLink(
                            uriPattern = it.arguments.get("uriPattern")?.toString().orEmpty(),
                            mimeType = it.arguments.get("mimeType")?.toString().orEmpty(),
                            action = it.arguments.get("action")?.toString().orEmpty()
                        )
                    }.orEmpty()

            val wrappers =
                (destinationAnnotation.arguments.get("wrappers") as? ArrayList<*>)
                    ?.filterIsInstance<KSType>()
                    ?.mapNotNull {
                        if (it.isObjectClass) {
                            it.toClassName()
                        } else {
                            logger.error("${it.toClassName()} must be an Object.", ksFunction)
                            null
                        }
                    }
            val argumentsNoDefault: List<Pair<String, KSTypeReference>> =
                ksFunction.parameters.filterNot(KSValueParameter::hasDefault).map {
                    it.name!!.asString() to it.type
                }
            generator.generate(
                ScreenDestinationInfo(
                    route = route,
                    style = style,
                    wrappers = wrappers.orEmpty(),
                    deepLinks = deepLinks,
                    function = MemberName(
                        ksFunction.packageName.asString(),
                        ksFunction.simpleName.asString()
                    ),
                    arguments = argumentsNoDefault
                )
            )
        }
        return skip
    }

    private fun List<KSValueArgument>.get(name: String): Any? {
        return firstOrNull { it.name?.asString() == name }?.value
    }
}
