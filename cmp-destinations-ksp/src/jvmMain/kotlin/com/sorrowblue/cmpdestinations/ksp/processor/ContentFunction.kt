package com.sorrowblue.cmpdestinations.ksp.processor

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.sorrowblue.cmpdestinations.ksp.isObjectClass
import com.sorrowblue.cmpdestinations.ksp.util.Composable
import com.sorrowblue.cmpdestinations.ksp.util.DestinationStyle
import com.sorrowblue.cmpdestinations.ksp.util.NavBackStackEntry
import com.sorrowblue.cmpdestinations.ksp.util.NavController
import com.sorrowblue.cmpdestinations.ksp.util.NavResultReceiver
import com.sorrowblue.cmpdestinations.ksp.util.NavResultSender
import com.sorrowblue.cmpdestinations.ksp.util.NavType
import com.sorrowblue.cmpdestinations.ksp.util.navResultReceiver
import com.sorrowblue.cmpdestinations.ksp.util.navResultSender
import com.sorrowblue.cmpdestinations.ksp.util.toRoute
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal fun routeProperty(routeType: KSType) =
    PropertySpec.builder(
        "route",
        KClass::class.asTypeName().parameterizedBy(STAR),
        KModifier.OVERRIDE
    )
        .initializer("%T::class", routeType.toClassName())
        .build()

internal fun styleProperty(styleType: ClassName) =
    PropertySpec.builder("style", DestinationStyle, KModifier.OVERRIDE)
        .initializer("%T", styleType)
        .build()

internal fun typeMapProperty(routeType: KSType) =
    PropertySpec.builder(
        "typeMap",
        Map::class.asClassName()
            .parameterizedBy(KType::class.asTypeName(), NavType.parameterizedBy(STAR)),
        KModifier.OVERRIDE
    ).apply {
        if (routeType.isObjectClass) {
            initializer("emptyMap()")
        } else {
            initializer("%T.typeMap()", routeType.toClassName())
        }
    }.build()

internal fun contentFunction(
    composableFunction: MemberName,
    routeType: ClassName,
    argumentsNoDefault: List<Pair<String, KSTypeReference>>,
) = FunSpec.builder("Content")
    .addAnnotation(Composable)
    .addModifiers(KModifier.OVERRIDE)
    .receiver(NavBackStackEntry)
    .addParameter(ParameterSpec("navController", NavController))
    .addCode(
        CodeBlock.builder().apply {
            if (argumentsNoDefault.isEmpty()) {
                add("%M()", composableFunction)
            } else {
                add("%M(«", composableFunction)
            }
            argumentsNoDefault.forEach {
                val typeResolve = it.second.resolve()
                val typeQualifiedName =
                    typeResolve.declaration.qualifiedName?.asString()
                when (typeQualifiedName) {
                    NavResultSender.canonicalName -> {
                        val senderType =
                            it.second.resolve().arguments.first().type!!.resolve()
                        add(
                            "%L = navController.%M<%T>(%T::class),",
                            it.first,
                            navResultSender,
                            senderType.toClassName(),
                            routeType
                        )
                    }

                    NavResultReceiver.canonicalName -> {
                        add("%L=%M(),", it.first, navResultReceiver)
                    }

                    routeType.canonicalName -> {
                        add("${it.first} = %M<%T>(),", toRoute, routeType)
                    }

                    else -> throw NotSupportException("not support type=$typeQualifiedName")
                }
            }
            if (argumentsNoDefault.isNotEmpty()) {
                add("»)")
            }
        }.build()
    )
    .build()
