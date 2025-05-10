package com.sorrowblue.cmpdestinations.annotation

@Retention(AnnotationRetention.SOURCE)
annotation class DeepLink(
    val uriPattern: String = "",
    val action: String = "",
    val mimeType: String = "",
)
