package com.sorrowblue.cmpdestinations.ksp.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import com.sorrowblue.cmpdestinations.ksp.get
import com.sorrowblue.cmpdestinations.ksp.getArgument
import com.sorrowblue.cmpdestinations.ksp.isObjectClass
import com.sorrowblue.cmpdestinations.ksp.util.DestinationInGraph
import com.sorrowblue.cmpdestinations.ksp.util.INavGraph
import com.sorrowblue.cmpdestinations.ksp.util.NavGraph
import com.sorrowblue.cmpdestinations.ksp.util.NavTransitions
import com.sorrowblue.cmpdestinations.ksp.util.NavType
import com.sorrowblue.cmpdestinations.ksp.util.NestedNavGraph
import com.sorrowblue.cmpdestinations.ksp.util.ScreenDestination
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal fun resolveNavGraph(
    codeGenerator: CodeGenerator,
    logger: KSPLogger,
    resolver: Resolver,
): List<KSClassDeclaration> {
    val symbols = resolver.getSymbolsWithAnnotation(NavGraph.canonicalName, true)
        .filterIsInstance<KSClassDeclaration>()
    val (processable, next) = symbols.partition { it.validate() }
    processable.forEach { symbol ->
        // @NavGraph(startDestination=XXX::class)
        val route = symbol.asType(emptyList())
        val navGraph = symbol.annotations.get(NavGraph)
        val startDestination = navGraph.getArgument<KSType>("startDestination")!!
        val transitions = navGraph.getArgument<KSType>("transitions")
        val root = navGraph.getArgument<KSType>("root")

        // data class Route(val value: String)
        if (!route.isObjectClass) {
            resolveTypeMap(codeGenerator, logger, route.declaration as KSClassDeclaration)
        }
        logger.info(
            "@NavGraph(startDestination = ${startDestination.toClassName().simpleName}::class, root = ${root?.toClassName()?.simpleName}::class, transition = ${transitions?.toClassName()?.simpleName}::class)",
            symbol
        )
        val inGraphRoute = mutableListOf<KSType>()
        val nestedNavGraphRoute = mutableListOf<KSType>()

        // @DestinationInGraph
        // @NestedNavGraph
        // companion object
        val includeObject: KSClassDeclaration =
            symbol.declarations.filterIsInstance<KSClassDeclaration>()
                .first { it.classKind == ClassKind.OBJECT }
        includeObject.annotations.forEach {
            val resolved = it.annotationType.resolve()
            if (resolved.declaration.qualifiedName?.asString() == DestinationInGraph.canonicalName) {
                inGraphRoute.add(resolved.arguments.first().type!!.resolve())
            } else if (resolved.declaration.qualifiedName?.asString() == NestedNavGraph.canonicalName) {
                nestedNavGraphRoute.add(resolved.arguments.first().type!!.resolve())
            }
        }

        val isRoot = root != null && root.toClassName() != ClassName("java.lang", "Void")
        val className =
            if (isRoot) {
                root!!.declaration.simpleName.asString()
            } else {
                route.declaration.simpleName.asString() + "NavGraph"
            }
        val rootPackage =
            if (isRoot) root!!.declaration.packageName.asString() else route.declaration.packageName.asString()
        val clazz = TypeSpec.classBuilder(className)
            .apply {
                if (isRoot) {
                    if (root!!.declaration.modifiers.any { it == Modifier.INTERNAL }) {
                        addModifiers(KModifier.INTERNAL)
                    }
                    addModifiers(KModifier.ACTUAL)
                }
            }
            .addSuperinterface(INavGraph)
            .addProperty(
                PropertySpec.builder(
                    "startDestination",
                    KClass::class.asTypeName().parameterizedBy(STAR),
                    KModifier.OVERRIDE
                ).apply {
                    if (isRoot) {
                        addModifiers(KModifier.ACTUAL)
                    }
                    initializer("%T::class", startDestination.toClassName())
                }.build()
            )
            .addProperty(
                PropertySpec.builder("transitions", NavTransitions, KModifier.OVERRIDE).apply {
                    if (isRoot) {
                        addModifiers(KModifier.ACTUAL)
                    }
                    transitions?.let {
                        initializer(
                            "%T${if (it.isObjectClass) "" else "()"}",
                            it.toClassName()
                        )
                    } ?: kotlin.run {
                        initializer(
                            "%T",
                            NavTransitions.nestedClass("Default")
                        )
                    }
                }.build()
            )
            .addProperty(
                PropertySpec.builder(
                    "route",
                    KClass::class.asTypeName().parameterizedBy(STAR),
                    KModifier.OVERRIDE
                ).apply {
                    if (isRoot) {
                        addModifiers(KModifier.ACTUAL)
                    }
                    initializer("%T::class", route.toClassName())
                }.build()
            )
            .addProperty(
                PropertySpec.builder(
                    "typeMap",
                    Map::class.asClassName().parameterizedBy(
                        KType::class.asTypeName(),
                        NavType.parameterizedBy(STAR)
                    ),
                    KModifier.OVERRIDE
                ).apply {
                    if (isRoot) {
                        addModifiers(KModifier.ACTUAL)
                    }
                    if (route.isObjectClass) {
                        initializer("emptyMap()")
                    } else {
                        initializer(
                            "%L.typeMap()",
                            route.declaration.qualifiedName!!.asString()
                        )
                    }
                }.build()
            )
            .addProperty(screenDestinations(isRoot, inGraphRoute, includeObject))
            .addProperty(nestedNavGraphs(isRoot, nestedNavGraphRoute))
            .build()
        FileSpec.builder(rootPackage, "$className.nav")
            .indent("    ")
            .addKotlinDefaultImports()
            .addType(clazz)
            .build()
            .writeTo(codeGenerator, Dependencies(true))
    }
    return next
}

private fun screenDestinations(
    isRootGraph: Boolean,
    inGraphRoutes: List<KSType>,
    includeObject: KSClassDeclaration,
): PropertySpec {
    return PropertySpec.builder(
        "screenDestinations",
        List::class.asClassName().parameterizedBy(ScreenDestination),
        KModifier.OVERRIDE
    ).apply {
        if (isRootGraph) {
            addModifiers(KModifier.ACTUAL)
        }
        if (inGraphRoutes.isEmpty()) {
            initializer("emptyList()")
        } else {
            val codeBlock = CodeBlock.builder().apply {
                add("♢listOf(«")

                inGraphRoutes.forEachIndexed { index, ksType ->
                    add(
                        "${if (index == 0) "" else "·"}%T(),",
                        ksType.toClassName().let {
                            ClassName(
                                it.packageName,
                                it.simpleName + "Destination"
                            )
                        }
                    )
                }
                add("»)")
            }
            initializer(codeBlock.build())
        }
        addKdoc(
            "Retrieved from [${(includeObject.parent as KSClassDeclaration).simpleName.asString()}.${includeObject.simpleName.asString()}]"
        )
    }.build()
}

private fun nestedNavGraphs(isRootGraph: Boolean, nestedNavGraphRoute: List<KSType>): PropertySpec {
    return PropertySpec.builder(
        "nestedNavGraphs",
        List::class.asClassName().parameterizedBy(INavGraph),
        KModifier.OVERRIDE
    ).apply {
        if (isRootGraph) {
            addModifiers(KModifier.ACTUAL)
        }
        if (nestedNavGraphRoute.isEmpty()) {
            initializer("emptyList()")
        } else {
            val codeBlock = CodeBlock.builder().apply {
                add("listOf(«")
                nestedNavGraphRoute.forEach { ksType ->
                    add(
                        "%T(),",
                        ksType.toClassName().let {
                            ClassName(it.packageName, it.simpleName + "NavGraph")
                        }
                    )
                }
                add("»)")
            }
            initializer(codeBlock.build())
        }
    }.build()
}
