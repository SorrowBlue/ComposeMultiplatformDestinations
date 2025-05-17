package com.sorrowblue.cmpdestinations.ksp.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.sorrowblue.cmpdestinations.ksp.model.CustomNavType
import com.sorrowblue.cmpdestinations.ksp.model.NavType
import com.sorrowblue.cmpdestinations.ksp.model.NormalNavType
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlin.reflect.KType

internal class TypeMapInfo(
    val route: KSClassDeclaration,
    val arguments: List<Pair<TypeName, NavType>>,
)

internal class TypeMapGenerator(private val codeGenerator: CodeGenerator) {

    fun generate(info: TypeMapInfo) {
        val func = FunSpec.builder("typeMap")
            .apply {
                if (info.route.modifiers.any { it == Modifier.INTERNAL }) {
                    addModifiers(KModifier.INTERNAL)
                }
            }
            .receiver(info.route.toClassName().nestedClass("Companion"))
            .returns(
                Map::class.asClassName()
                    .parameterizedBy(
                        KType::class.asClassName(),
                        androidx.navigation.NavType::class.asClassName().parameterizedBy(STAR)
                    )
            ).apply {
                addStatement("return mapOf(«⇥")
                info.arguments.forEach {
                    when (val navType = it.second) {
                        is NormalNavType -> addStatement(
                            "%M<%L>() to %T.%L,",
                            typeOf,
                            it.first,
                            androidx.navigation.NavType::class,
                            navType.navTypePropertyName
                        )

                        is CustomNavType -> addStatement(
                            "%M<%T>() to %T.%M<%T>(),",
                            typeOf,
                            it.first,
                            androidx.navigation.NavType::class,
                            kSerializableType,
                            it.first
                        )
                    }
                }
                addStatement("⇤»)")
            }
            .build()
        FileSpec.builder(
            info.route.packageName.asString(),
            "${info.route.simpleName.asString()}.nav"
        )
            .indent("    ")
            .addFunction(func)
            .build()
            .writeTo(codeGenerator, Dependencies(true))
    }
}

private val typeOf = MemberName("kotlin.reflect", "typeOf")
private val kSerializableType = MemberName(
    "com.sorrowblue.cmpdestinations.serializer",
    "kSerializableType",
    true
)
