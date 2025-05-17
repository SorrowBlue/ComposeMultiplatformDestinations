package com.sorrowblue.cmpdestinations.ksp.util

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

/** @see [com.sorrowblue.cmpdestinations.result.navResultSender] */
internal val navResultSender =
    MemberName("com.sorrowblue.cmpdestinations.result", "navResultSender")

/** @see [com.sorrowblue.cmpdestinations.result.navResultReceiver] */
internal val navResultReceiver =
    MemberName("com.sorrowblue.cmpdestinations.result", "navResultReceiver")

/** @see [androidx.navigation.toRoute] */
internal val toRoute = MemberName("androidx.navigation", "toRoute")

/** @see [androidx.navigation.navDeepLink] */
internal val navDeepLink =
    ClassName("androidx.navigation", "navDeepLink")

/** @see [androidx.navigation.navDeepLink] */
internal val WrapRecursively =
    MemberName("com.sorrowblue.cmpdestinations", "WrapRecursively", true)
