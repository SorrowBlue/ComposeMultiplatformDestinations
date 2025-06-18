package com.sorrowblue.cmpdestinations.ksp.generator

import androidx.navigation.NavType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSType
import com.sorrowblue.cmpdestinations.Destination
import com.sorrowblue.cmpdestinations.GraphNavigation
import com.sorrowblue.cmpdestinations.animation.NavTransitions
import com.sorrowblue.cmpdestinations.ksp.isObjectClass
import com.squareup.kotlinpoet.ARRAY
import com.squareup.kotlinpoet.AnnotationSpec
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
import kotlin.reflect.KType
import kotlinx.serialization.Serializable

data class NavGraphInfo(
    val name: ClassName,
    val isActual: Boolean,
    val isInternal: Boolean,
    val startDestination: ClassName,
    val route: KSType,
    val destinations: List<KSType>,
    val nestedGraphs: List<KSType>,
    val transitions: ClassName,
)

class NavGraphGenerator(private val codeGenerator: CodeGenerator) {

    fun generate(info: NavGraphInfo) {
        val clazz = TypeSpec.objectBuilder(info.name).apply {
            if (info.isActual) {
                addModifiers(KModifier.ACTUAL)
                addAnnotation(
                    AnnotationSpec.builder(Suppress::class)
                        .addMember("%S", "ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
                        .build()
                )
                addAnnotation(Serializable::class)
            }
            if (info.isInternal) {
                addModifiers(KModifier.INTERNAL)
            }
            addSuperinterface(GraphNavigation::class)
            addStartDestination(info.startDestination, info.isActual)
            addRoute(info.route.toClassName(), info.isActual)
            addDestinationsProperty(info.destinations, info.isActual)
            addNestedGraphsProperty(info.nestedGraphs, info.isActual)
            addTransitionsProperty(info.transitions, info.isActual)
            addTypeMapProperty(info.route, info.isActual)
        }
            .build()
        FileSpec.builder(info.name.packageName, "${info.name.simpleName}.nav")
            .indent("    ")
            .addKotlinDefaultImports()
            .addType(clazz)
            .build()
            .writeTo(codeGenerator, Dependencies(true))
    }

    private fun TypeSpec.Builder.addStartDestination(
        startDestination: ClassName,
        isActual: Boolean,
    ) =
        addProperty(
            PropertySpec.builder(
                "startDestination",
                ClassName("kotlin.reflect.", "KClass").parameterizedBy(STAR),
                KModifier.OVERRIDE
            ).apply {
                if (isActual) {
                    addModifiers(KModifier.ACTUAL)
                }
                initializer("%T::class", startDestination)
            }.build()
        )

    private fun TypeSpec.Builder.addRoute(
        route: ClassName,
        isActual: Boolean,
    ) = addProperty(
        PropertySpec.builder(
            "route",
            ClassName("kotlin.reflect.", "KClass").parameterizedBy(STAR),
            KModifier.OVERRIDE
        ).apply {
            if (isActual) {
                addModifiers(KModifier.ACTUAL)
            }
            initializer("%T::class", route)
        }.build()
    )

    private fun TypeSpec.Builder.addDestinationsProperty(
        destinationTypes: List<KSType>,
        isActual: Boolean,
    ) = addProperty(
        PropertySpec.builder(
            "destinations",
            ARRAY.parameterizedBy(Destination::class.asClassName()),
            KModifier.OVERRIDE
        ).apply {
            if (isActual) {
                addModifiers(KModifier.ACTUAL)
            }
            initializer(
                CodeBlock.builder().apply {
                    if (destinationTypes.isEmpty()) {
                        add("emptyArray()")
                    } else {
                        add("arrayOf(«")
                        destinationTypes.forEachIndexed { index, ksType ->
                            add(
                                "${if (index == 0) "" else "·"}%T,",
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
                }.build()
            )
        }.build()
    )

    private fun TypeSpec.Builder.addNestedGraphsProperty(
        nestedGraphs: List<KSType>,
        isActual: Boolean,
    ) = addProperty(
        PropertySpec.builder(
            "nestedGraphs",
            ARRAY.parameterizedBy(GraphNavigation::class.asClassName()),
            KModifier.OVERRIDE
        ).apply {
            if (isActual) {
                addModifiers(KModifier.ACTUAL)
            }
            initializer(
                CodeBlock.builder().apply {
                    if (nestedGraphs.isEmpty()) {
                        add("emptyArray()")
                    } else {
                        add("arrayOf(«")
                        nestedGraphs.forEachIndexed { index, ksType ->
                            add(
                                "${if (index == 0) "" else "·"}%T,",
                                ksType.toClassName().let {
                                    ClassName(
                                        it.packageName,
                                        it.simpleName + "Navigation"
                                    )
                                }
                            )
                        }
                        add("»)")
                    }
                }.build()
            )
        }.build()
    )

    private fun TypeSpec.Builder.addTransitionsProperty(
        transitions: ClassName,
        isActual: Boolean,
    ) = addProperty(
        PropertySpec.builder(
            "transitions",
            NavTransitions::class,
            KModifier.OVERRIDE
        ).apply {
            if (isActual) {
                addModifiers(KModifier.ACTUAL)
            }
            initializer("%T", transitions)
        }.build()
    )

    private fun TypeSpec.Builder.addTypeMapProperty(
        routeType: KSType,
        isActual: Boolean,
    ) = addProperty(
        PropertySpec.builder(
            "typeMap",
            Map::class.asClassName()
                .parameterizedBy(
                    KType::class.asTypeName(),
                    NavType::class.asClassName().parameterizedBy(STAR)
                ),
            KModifier.OVERRIDE
        ).apply {
            if (isActual) {
                addModifiers(KModifier.ACTUAL)
            }
            if (routeType.isObjectClass) {
                initializer("emptyMap()")
            } else {
                initializer("%T.typeMap()", routeType.toClassName())
            }
        }.build()
    )
}
