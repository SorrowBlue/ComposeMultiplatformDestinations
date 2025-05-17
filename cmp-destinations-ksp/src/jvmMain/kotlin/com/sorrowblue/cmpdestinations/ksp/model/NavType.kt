package com.sorrowblue.cmpdestinations.ksp.model

internal sealed interface NavType
internal data object CustomNavType : NavType
internal data class NormalNavType(val navTypePropertyName: String) : NavType
