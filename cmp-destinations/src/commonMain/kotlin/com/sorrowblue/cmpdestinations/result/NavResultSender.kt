package com.sorrowblue.cmpdestinations.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation.NavController
import com.sorrowblue.cmpdestinations.serializer.KSerializerHelper
import com.sorrowblue.cmpdestinations.serializer.kSerializerHelper
import kotlin.reflect.KClass

@Composable
inline fun <reified R : Any> NavController.navResultSender(screen: KClass<out Any>): NavResultSender<R> =
    navResultSender(screen, kSerializerHelper<R>(), this)

@PublishedApi
@Composable
internal fun <R : Any> navResultSender(
    currentNavScreen: KClass<out Any>,
    serializerType: KSerializerHelper<R>,
    navController: NavController,
): NavResultSender<R> {
    val sender = remember(navController, currentNavScreen, serializerType) {
        NavResultSenderImpl(navController, currentNavScreen, serializerType)
    }
    sender.HandleCanceled()
    return sender
}

interface NavResultSender<R : Any> {
    fun setResult(result: R)
    fun navigateBack()
    fun navigateBack(result: R)
}

private class NavResultSenderImpl<R : Any>(
    private val navController: NavController,
    currentNavScreen: KClass<out Any>,
    private val serializerType: KSerializerHelper<R>,
) : NavResultSender<R> {

    private val resultKey = resultKey(currentNavScreen, serializerType)
    private val canceledKey = cancelKey(currentNavScreen, serializerType)

    override fun navigateBack(result: R) {
        setResult(result)
        navigateBack()
    }

    override fun setResult(result: R) {
        navController.previousBackStackEntry?.savedStateHandle?.let {
            it[canceledKey] = false
            it[resultKey] = serializerType.toJsonString(result)
        }
    }

    override fun navigateBack() {
        navController.navigateUp()
    }

    @Composable
    fun HandleCanceled() {
        LifecycleResumeEffect(Unit) {
            val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle
                ?: return@LifecycleResumeEffect onPauseOrDispose { }
            if (!savedStateHandle.contains(canceledKey)) {
                savedStateHandle[canceledKey] = true
            }
            onPauseOrDispose { }
        }
    }
}
