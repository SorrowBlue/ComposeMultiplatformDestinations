package com.sorrowblue.cmpdestinations.ksp.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.sorrowblue.cmpdestinations.ksp.generator.TypeMapGenerator
import com.sorrowblue.cmpdestinations.ksp.generator.TypeMapInfo
import com.sorrowblue.cmpdestinations.ksp.isEnumClass
import com.sorrowblue.cmpdestinations.ksp.model.CustomNavType
import com.sorrowblue.cmpdestinations.ksp.model.NormalNavType
import com.squareup.kotlinpoet.ARRAY
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import kotlinx.serialization.Serializable

private val KSClassDeclaration.isObject: Boolean
    get() = classKind == ClassKind.OBJECT
private val supportTypeMap: Map<TypeName, String> = mapOf(
    Int::class.asTypeName() to "IntType",
    IntArray::class.asTypeName() to "IntArrayType",
    Long::class.asTypeName() to "LongType",
    LongArray::class.asTypeName() to "LongArrayType",
    Float::class.asTypeName() to "FloatType",
    FloatArray::class.asTypeName() to "FloatArrayType",
    Boolean::class.asTypeName() to "BoolType",
    BooleanArray::class.asTypeName() to "BoolArrayType",
    String::class.asTypeName() to "StringType",
    ARRAY.parameterizedBy(STRING) to "StringArrayType",
    List::class.asTypeName().parameterizedBy(Int::class.asTypeName()) to "IntListType",
    List::class.asTypeName().parameterizedBy(Long::class.asTypeName()) to "LongListType",
    List::class.asTypeName().parameterizedBy(Float::class.asTypeName()) to "FloatListType",
    List::class.asTypeName().parameterizedBy(Boolean::class.asTypeName()) to "BoolListType",
    List::class.asTypeName().parameterizedBy(String::class.asTypeName()) to "StringListType",
)

internal class TypeMapHelper(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) {
    private val generator = TypeMapGenerator(codeGenerator)

    /** 処理済みのクラスを保持する */
    private val processedClasses = mutableSetOf<KSClassDeclaration>()

    /**
     * Generates a type map for the given class declaration. If the class is
     * already processed or is an object, this method does nothing. Throws
     * [NotSupportException] if a parameter type cannot be resolved or is not
     * supported.
     *
     * @param classDeclaration The class declaration to process.
     */
    fun process(classDeclaration: KSClassDeclaration) {
        if (classDeclaration.isObject || processedClasses.contains(classDeclaration)) return
        processedClasses.add(classDeclaration)

        logger.info("TypeMapHelper... ${classDeclaration.qualifiedName?.asString()}")

        val arguments = classDeclaration.primaryConstructor?.parameters?.map { parameter ->
            val ksType = parameter.type.resolve()
            val typeName = ksType.makeNotNullable().toTypeName()
            supportTypeMap[typeName]?.let {
                typeName to NormalNavType(it)
            } ?: kotlin.run {
                if (ksType.isEnumClass || ksType.isSerializable) {
                    typeName to CustomNavType
                } else {
                    throw NotSupportException("    Generic argument : Could not resolve type ($typeName)")
                }
            }
        } ?: run {
            logger.warn("    No primary constructor found for ${classDeclaration.qualifiedName?.asString()}")
            return
        }
        val info = TypeMapInfo(
            route = classDeclaration,
            arguments = arguments
        )
        generator.generate(info)
    }
}

val KSType.isSerializable
    get(): Boolean = declaration.annotations.any {
        it.annotationType.resolve().toClassName() == Serializable::class.asClassName()
    }
