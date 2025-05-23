package com.sorrowblue.cmpdestinations.ksp

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName

val KSType.isObjectClass get() = (declaration as? KSClassDeclaration)?.classKind == ClassKind.OBJECT

val KSType.isEnumClass get() = (declaration as? KSClassDeclaration)?.classKind == ClassKind.ENUM_CLASS

fun Sequence<KSAnnotation>.get(className: ClassName): KSAnnotation {
    return first { it.annotationType.resolve().declaration.qualifiedName?.asString() == className.canonicalName }
}

fun Sequence<KSAnnotation>.getFirst(fqName: String): Pair<KSType, KSAnnotation> {
    forEach {
        val annotationType = it.annotationType.resolve()
        if (annotationType.declaration.qualifiedName?.asString() == fqName) {
            return annotationType to it
        }
    }
    throw IllegalArgumentException("No annotation found with fqName: $fqName")
}

fun <T> KSAnnotation.getArgument(name: String): T? {
    @Suppress("UNCHECKED_CAST")
    return arguments.firstOrNull { it.name?.asString() == name }?.value as? T
}
