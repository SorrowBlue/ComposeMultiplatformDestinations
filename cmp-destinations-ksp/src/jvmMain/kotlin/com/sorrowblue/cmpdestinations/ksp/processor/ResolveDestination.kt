package com.sorrowblue.cmpdestinations.ksp.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueArgument
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import com.sorrowblue.cmpdestinations.ksp.get
import com.sorrowblue.cmpdestinations.ksp.isObjectClass
import com.sorrowblue.cmpdestinations.ksp.util.Destination
import com.sorrowblue.cmpdestinations.ksp.util.DestinationStyle_Composable
import com.sorrowblue.cmpdestinations.ksp.util.NavDeepLink
import com.sorrowblue.cmpdestinations.ksp.util.ScreenDestination
import com.sorrowblue.cmpdestinations.ksp.util.Serializable
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toClassNameOrNull
import com.squareup.kotlinpoet.ksp.writeTo

internal fun resolveDestination(
    codeGenerator: CodeGenerator,
    logger: KSPLogger,
    resolver: Resolver,
): List<KSFunctionDeclaration> {
    val symbols = resolver.getSymbolsWithAnnotation(Destination.canonicalName, true)
        .filterIsInstance<KSFunctionDeclaration>()
    val (processable, next) = symbols.partition { it.validate() }
    val resolveTypeMap = mutableListOf<String>()
    val result = processable.filter { ksFunction ->
        logger.info("@Destination fun ${ksFunction.simpleName.asString()}", ksFunction)

        val composableFunction =
            MemberName(ksFunction.packageName.asString(), ksFunction.simpleName.asString())
        val destination = ksFunction.annotations.get(Destination)

        val routeType = destination.annotationType.resolve().arguments.first().type?.resolve()
            ?: run {
                logger.error("routeType is null", ksFunction)
                return@filter true
            }
        logger.info("  route: ${routeType.toClassName().simpleName}", ksFunction)
        val name =
            routeType.toClassName().let { ClassName(it.packageName, it.simpleName + "Destination") }
        if (routeType.declaration.annotations.any {
                it.annotationType.resolve().toClassNameOrNull() == Serializable
            }
        ) {
            if (!routeType.isObjectClass && !resolveTypeMap.contains(routeType.toClassName().canonicalName)) {
                resolveTypeMap.add(routeType.toClassName().canonicalName)
                resolveTypeMap(
                    codeGenerator,
                    logger,
                    routeType.declaration as KSClassDeclaration
                )
            }
        } else {
            logger.error("'${routeType.toClassNameOrNull()?.canonicalName}' requires a Serializable annotation.")
        }
        // @Destination(style = "XXX") を取得
        val style = (destination.arguments.get("style") as? KSType)?.toClassName()
            ?: DestinationStyle_Composable
        logger.info("  style: ${style.simpleName}", ksFunction)

        val deeplinks =
            (destination.arguments.get("deeplinks") as? ArrayList<*>)
                ?.filterIsInstance<KSAnnotation>()
                ?.map {
                    NavDeepLink(
                        uriPattern = it.arguments.get("uriPattern")?.toString(),
                        mimeType = it.arguments.get("mimeType")?.toString(),
                        action = it.arguments.get("action")?.toString()
                    )
                }

        val argumentsNoDefault: List<Pair<String, KSTypeReference>> =
            ksFunction.parameters.filterNot(KSValueParameter::hasDefault).map {
                it.name!!.asString() to it.type
            }
        val clazz = TypeSpec.classBuilder(name)
            .apply {
                if (routeType.declaration.modifiers.any { it == Modifier.INTERNAL }) {
                    addModifiers(KModifier.INTERNAL)
                }
            }
            .addSuperinterface(ScreenDestination)
            .addProperty(routeProperty(routeType))
            .addProperty(styleProperty(style))
            .addProperty(deeplinksProperty(deeplinks.orEmpty()))
            .addProperty(typeMapProperty(routeType))
            .addFunction(
                contentFunction(
                    composableFunction,
                    routeType.toClassName(),
                    argumentsNoDefault
                )
            )
            .build()
        FileSpec.builder(routeType.toClassName().packageName, "${name.simpleName}.nav")
            .indent("    ")
            .addKotlinDefaultImports()
            .addType(clazz)
            .build()
            .writeTo(codeGenerator, Dependencies(true))
        false
    }
    return result + next
}

/** 引数の名前を指定して設定された値を取得する */
private fun List<KSValueArgument>.get(name: String): Any? {
    return firstOrNull { it.name?.asString() == name }?.value
}
