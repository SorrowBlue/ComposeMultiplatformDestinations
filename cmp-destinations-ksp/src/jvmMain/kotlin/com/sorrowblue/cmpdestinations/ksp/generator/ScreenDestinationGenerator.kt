package com.sorrowblue.cmpdestinations.ksp.generator

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Modifier
import com.sorrowblue.cmpdestinations.DestinationScope
import com.sorrowblue.cmpdestinations.DestinationScopeImpl
import com.sorrowblue.cmpdestinations.DestinationStyle
import com.sorrowblue.cmpdestinations.DestinationWrapper
import com.sorrowblue.cmpdestinations.ScreenDestination
import com.sorrowblue.cmpdestinations.ksp.isObjectClass
import com.sorrowblue.cmpdestinations.ksp.model.NavDeepLink
import com.sorrowblue.cmpdestinations.ksp.model.ScreenDestinationInfo
import com.sorrowblue.cmpdestinations.ksp.processor.NotSupportException
import com.sorrowblue.cmpdestinations.ksp.util.navDeepLink
import com.sorrowblue.cmpdestinations.ksp.util.navResultReceiver
import com.sorrowblue.cmpdestinations.ksp.util.navResultSender
import com.sorrowblue.cmpdestinations.ksp.util.toRoute
import com.sorrowblue.cmpdestinations.result.NavResultReceiver
import com.sorrowblue.cmpdestinations.result.NavResultSender
import com.squareup.kotlinpoet.ARRAY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal class ScreenDestinationGenerator(private val codeGenerator: CodeGenerator) {

    fun generate(info: ScreenDestinationInfo) {
        val className =
            info.route.toClassName()
                .let { ClassName(it.packageName, it.simpleName + "Destination") }
        val visibility = if (info.route.declaration.modifiers.any { it == Modifier.INTERNAL }) {
            KModifier.INTERNAL
        } else {
            KModifier.PUBLIC
        }
        val classTypeSpec = TypeSpec.objectBuilder(className)
            .addModifiers(visibility)
            .addSuperinterface(ScreenDestination::class)
            .addProperty(routeProperty(info.route))
            .addProperty(styleProperty(info.style))
            .addProperty(wrappersProperty(info.wrappers))
            .addProperty(deepLinksProperty(info.deepLinks))
            .addProperty(typeMapProperty(info.route))
            .addFunction(
                contentFunction(
                    info.function,
                    info.route.toClassName(),
                    info.arguments,
                    info.wrappers.isEmpty()
                )
            )
            .addKdoc(
                CodeBlock.builder()
                    .addStatement(
                        "Generated from [%T] and [%M].",
                        info.route.toClassName(),
                        info.function
                    )
                    .build()
            )
            .build()

        FileSpec.builder(className.packageName, "${className.simpleName}.nav")
            .indent("    ")
            .addKotlinDefaultImports()
            .addType(classTypeSpec)
            .build()
            .writeTo(codeGenerator, Dependencies(true))
    }

    /**
     * Generates a property for the route of the destination.
     *
     * ```
     * override val route: KClass<*> = Route::class
     * ```
     */
    private fun routeProperty(routeType: KSType): PropertySpec =
        PropertySpec.builder(
            "route",
            KClass::class.asTypeName().parameterizedBy(STAR),
            KModifier.OVERRIDE
        )
            .initializer("%T::class", routeType.toClassName())
            .build()

    /**
     * Generates a property for the style of the destination.
     *
     * ```
     * override val style: DestinationStyle = DestinationStyle.Composable
     * ```
     */
    private fun styleProperty(styleType: ClassName): PropertySpec =
        PropertySpec.builder("style", DestinationStyle::class, KModifier.OVERRIDE)
            .initializer("%T", styleType)
            .build()

    /**
     * Generates a property for the deep links of the destination.
     *
     * ```
     * override val deepLinks: Array<NavDeepLink> = emptyArray()
     * ```
     * ```
     * override val deepLinks: Array<NavDeepLink> = arrayOf(
     *     navDeepLink {
     *         action = "android.intent.action.VIEW"
     *         mimeType = "application/pdf"
     *     },
     *     navDeepLink {
     *         action = "android.intent.action.VIEW"
     *         mimeType = "text/plain"
     *     },
     * )
     * ```
     */
    private fun deepLinksProperty(deepLinks: List<NavDeepLink>): PropertySpec =
        PropertySpec.builder(
            "deepLinks",
            ARRAY.parameterizedBy(androidx.navigation.NavDeepLink::class.asClassName()),
            KModifier.OVERRIDE
        )
            .apply {
                if (deepLinks.isEmpty()) {
                    initializer("emptyArray()")
                } else {
                    initializer(
                        CodeBlock.builder().apply {
                            addStatement("arrayOf(⇥")
                            deepLinks.forEach {
                                addStatement("%T {⇥", navDeepLink)
                                if (!it.uriPattern.isNullOrBlank()) {
                                    addStatement("uriPattern = %S", it.uriPattern)
                                }
                                if (!it.action.isNullOrBlank()) {
                                    addStatement("action = %S", it.action)
                                }
                                if (!it.mimeType.isNullOrBlank()) {
                                    addStatement("mimeType = %S", it.mimeType)
                                }
                                addStatement("⇤},")
                            }
                            addStatement("⇤)")
                        }.build()
                    )
                }
            }
            .build()

    /**
     * Generates a property for the wrappers of the destination.
     *
     * ```
     * override val wrappers: Array<out DestinationWrapper> = emptyArray()
     * ```
     * ```
     * override val wrappers: Array<out DestinationWrapper> = arrayOf(
     *     WrapperImpl,
     *     WrapperImpl2,
     *     WrapperImpl3,
     * )
     * ```
     */
    private fun wrappersProperty(wrappers: List<ClassName>): PropertySpec =
        PropertySpec.builder(
            "wrappers",
            ARRAY.parameterizedBy(WildcardTypeName.producerOf(DestinationWrapper::class.asClassName())),
            KModifier.OVERRIDE
        ).apply {
            if (wrappers.isEmpty()) {
                initializer("emptyArray()")
            } else {
                initializer(
                    CodeBlock.builder().apply {
                        addStatement("arrayOf(⇥")
                        wrappers.forEach {
                            addStatement("%T,", it)
                        }
                        addStatement("⇤)")
                    }.build()
                )
            }
        }.build()

    /**
     * Generates a property for the TypeMap of the destination.
     *
     * ```
     * override val typeMap: Map<KType, NavType<*>> = emptyMap()
     * ```
     * ```
     * override val typeMap: Map<KType, NavType<*>> = AllSupportType.typeMap()
     * ```
     */
    private fun typeMapProperty(routeType: KSType): PropertySpec =
        PropertySpec.builder(
            "typeMap",
            Map::class.asClassName()
                .parameterizedBy(
                    KType::class.asTypeName(),
                    NavType::class.asClassName().parameterizedBy(STAR)
                ),
            KModifier.OVERRIDE
        ).apply {
            if (routeType.isObjectClass) {
                initializer("emptyMap()")
            } else {
                initializer("%T.typeMap()", routeType.toClassName())
            }
        }.build()

    private fun contentFunction(
        composableFunction: MemberName,
        routeType: ClassName,
        argumentsNoDefault: List<Pair<String, KSTypeReference>>,
        emptyWrappers: Boolean,
    ): FunSpec = FunSpec.builder("Content")
        .addAnnotation(Composable::class)
        .addModifiers(KModifier.OVERRIDE)
        .receiver(NavBackStackEntry::class)
        .addParameter(ParameterSpec("navController", NavController::class.asClassName()))
        .addCode(
            CodeBlock.builder().apply {
                destinationScopeImpl {
                    wrapRecursively(emptyWrappers) {
                        addComposableScreen(
                            routeType = routeType,
                            screenMember = composableFunction,
                            argumentsNoDefault = argumentsNoDefault
                        )
                    }
                }
            }.build()
        )
        .build()

    private inline fun CodeBlock.Builder.destinationScopeImpl(content: () -> Unit) {
        addStatement("val scope = %T(this, navController)", DestinationScopeImpl::class)
        beginControlFlow("with(scope)")
        content()
        endControlFlow()
    }

    private inline fun CodeBlock.Builder.wrapRecursively(
        emptyWrappers: Boolean,
        content: () -> Unit,
    ) {
        if (emptyWrappers) {
            content()
        } else {
            beginControlFlow("%L(wrappers, 0)", DestinationScope::WrapRecursively.name)
            content()
            endControlFlow()
        }
    }

    private fun CodeBlock.Builder.addComposableScreen(
        routeType: ClassName,
        screenMember: MemberName,
        argumentsNoDefault: List<Pair<String, KSTypeReference>>,
    ) {
        if (argumentsNoDefault.isEmpty()) {
            addStatement("%M()", screenMember)
            return
        }
        addStatement("%M(⇥", screenMember)
        argumentsNoDefault.forEach {
            val typeResolve = it.second.resolve()
            when (val argumentClassName = typeResolve.declaration.qualifiedName?.asString()) {
                NavResultSender::class.qualifiedName -> {
                    val senderType =
                        it.second.resolve().arguments.first().type!!.resolve()
                    addStatement(
                        "%L = navController.%M<%T>(%T::class),",
                        it.first,
                        navResultSender,
                        senderType.toClassName(),
                        routeType
                    )
                }

                NavBackStackEntry::class.qualifiedName -> {
                    addStatement("%L = this@Content,", it.first)
                }

                NavResultReceiver::class.qualifiedName -> {
                    addStatement("%L=%M(),", it.first, navResultReceiver)
                }

                NavController::class.qualifiedName -> {
                    addStatement("%L = navController,", it.first)
                }

                routeType.canonicalName -> {
                    addStatement("${it.first} = %M<%T>(),", toRoute, routeType)
                }

                else -> throw NotSupportException("not support type=$argumentClassName")
            }
        }
        if (argumentsNoDefault.isNotEmpty()) {
            addStatement("⇤)")
        }
    }
}
