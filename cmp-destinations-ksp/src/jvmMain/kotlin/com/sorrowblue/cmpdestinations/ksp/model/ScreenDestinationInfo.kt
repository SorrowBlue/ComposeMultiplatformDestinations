package com.sorrowblue.cmpdestinations.ksp.model

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

internal class ScreenDestinationInfo(
    val route: KSType,
    val style: ClassName,
    val wrappers: List<ClassName>,
    val deepLinks: List<NavDeepLink>,
    val function: MemberName,
    val arguments: List<Pair<String, KSTypeReference>>,
)
