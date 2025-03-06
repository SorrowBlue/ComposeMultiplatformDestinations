package com.sorrowblue.cmpdestinations.ksp.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.sorrowblue.cmpdestinations.ksp.isEnumClass
import com.sorrowblue.cmpdestinations.ksp.util.NavType
import com.sorrowblue.cmpdestinations.ksp.util.Serializable
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlin.reflect.KType

val typeOf = MemberName("kotlin.reflect", "typeOf")

fun resolveTypeMap(
    codeGenerator: CodeGenerator,
    logger: KSPLogger,
    routeType: KSClassDeclaration,
): Unit? {
    logger.info("GenerateTypeMap... ${routeType.qualifiedName?.asString()}")
    val className = routeType.simpleName.asString()
    val packageName = routeType.packageName.asString()
    val func = FunSpec.builder("typeMap")
        .receiver(routeType.toClassName().nestedClass("Companion"))
        .returns(
            Map::class.asClassName()
                .parameterizedBy(KType::class.asClassName(), NavType.parameterizedBy(STAR))
        ).apply {
            addStatement("return mapOf(«⇥")
            addRouteType(routeType, logger) ?: return null
            addStatement("⇤»)")
        }
        .build()
    FileSpec.builder(packageName, "$className.nav")
        .indent("    ")
        .addFunction(func)
        .build()
        .writeTo(codeGenerator, Dependencies(true))
    return Unit
}

fun FunSpec.Builder.addRouteType(
    ksClassDeclaration: KSClassDeclaration,
    logger: KSPLogger,
): Unit? {
    return ksClassDeclaration.primaryConstructor?.parameters?.forEach { parameter ->
        val ksType = parameter.type.resolve()
        when (ksType.makeNotNullable().toTypeName()) {
            Int::class.asTypeName() -> ksType.toClassName() to "IntType"
            IntArray::class.asTypeName() -> ksType.toClassName() to "IntArrayType"
            Long::class.asTypeName() -> ksType.toClassName() to "LongType"
            LongArray::class.asTypeName() -> ksType.toClassName() to "LongArrayType"
            Float::class.asTypeName() -> ksType.toClassName() to "FloatType"
            FloatArray::class.asTypeName() -> ksType.toClassName() to "FloatArrayType"
            Boolean::class.asTypeName() -> ksType.toClassName() to "BoolType"
            BooleanArray::class.asTypeName() -> ksType.toClassName() to "BoolArrayType"
            String::class.asTypeName() -> ksType.toClassName() to "StringType"
            Array::class.asTypeName()
                .parameterizedBy(String::class.asTypeName()),
            -> "Array<String>" to "StringArrayType"

            List::class.asTypeName()
                .parameterizedBy(Int::class.asTypeName()),
            -> "Array<Int>" to "IntListType"

            List::class.asTypeName()
                .parameterizedBy(Long::class.asTypeName()),
            -> "Array<Long>" to "LongListType"

            List::class.asTypeName()
                .parameterizedBy(Float::class.asTypeName()),
            -> "Array<Float>" to "FloatListType"

            List::class.asTypeName()
                .parameterizedBy(Boolean::class.asTypeName()),
            -> "Array<Boolean>" to "BoolListType"

            List::class.asTypeName()
                .parameterizedBy(String::class.asTypeName()),
            -> "List<String>" to "StringListType"

            else -> null
        }?.let {
            if (it.first is String) {
                addStatement("%M<%L>() to %T.%L,", typeOf, it.first, NavType, it.second)
            } else {
                addStatement("%M<%T>() to %T.%L,", typeOf, it.first, NavType, it.second)
            }
        } ?: run {
            if (ksType.declaration.annotations.any {
                    it.annotationType.resolve().toClassName() == Serializable
                } || ksType.isEnumClass
            ) {
                val kSerializableType = MemberName(
                    "com.sorrowblue.cmpdestinations.serializer",
                    "kSerializableType",
                    true
                )
                addStatement(
                    "%M<%T>() to %T.%M<%T>(),",
                    typeOf,
                    ksType.toClassName(),
                    NavType,
                    kSerializableType,
                    ksType.toClassName()
                )
            } else {
                logger.error("$ksType is not support")
                return null
            }
        }
    }
}
