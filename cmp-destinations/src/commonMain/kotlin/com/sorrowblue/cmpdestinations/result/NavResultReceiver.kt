package com.sorrowblue.cmpdestinations.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation.NavBackStackEntry
import com.sorrowblue.cmpdestinations.lifecycle.LifecycleEventsEffect
import com.sorrowblue.cmpdestinations.serializer.KSerializerHelper
import com.sorrowblue.cmpdestinations.serializer.kSerializerHelper
import kotlin.reflect.KClass

sealed interface NavResult<out R> {
    data object Canceled : NavResult<Nothing>
    data class Value<R>(val value: R) : NavResult<R>
}

@Composable
inline fun <reified N : Any, reified R : Any> NavBackStackEntry.navResultReceiver(): NavResultReceiver<N, R> =
    navResultReceiver(this, N::class, kSerializerHelper())

@PublishedApi
@Composable
internal fun <N : Any, R : Any> navResultReceiver(
    backStackEntry: NavBackStackEntry,
    originalNavScreen: KClass<out Any>,
    kSerializerByteArray: KSerializerHelper<R>,
): NavResultReceiver<N, R> = remember(backStackEntry, originalNavScreen, kSerializerByteArray) {
    NavResultReceiverImpl(backStackEntry, originalNavScreen, kSerializerByteArray)
}

interface NavResultReceiver<N, R : Any> {
    @Suppress("ComposableNaming")
    @Composable
    fun onNavResult(listener: @DisallowComposableCalls (NavResult<R>) -> Unit)
}

private class NavResultReceiverImpl<N : Any, R : Any>(
    private val backStackEntry: NavBackStackEntry,
    originalNavScreen: KClass<out Any>,
    private val kSerializerByteArray: KSerializerHelper<R>,
) : NavResultReceiver<N, R> {

    private val resultKey = resultKey(originalNavScreen, kSerializerByteArray)
    private val canceledKey = cancelKey(originalNavScreen, kSerializerByteArray)

    @Composable
    override fun onNavResult(listener: (NavResult<R>) -> Unit) {
        val currentListener by rememberUpdatedState(listener)
        LifecycleEventsEffect(
            Lifecycle.Event.ON_START,
            Lifecycle.Event.ON_RESUME,
            key = backStackEntry
        ) {
            handleResult(currentListener)
        }
        LifecycleResumeEffect(key1 = backStackEntry) {
            handleResult(currentListener)
            onPauseOrDispose { }
        }
    }

    private fun handleResult(listener: (NavResult<R>) -> Unit) {
        if (!hasAnyResult()) return

        val canceled = backStackEntry.savedStateHandle.remove<Boolean>(canceledKey)

        if (canceled == true) {
            listener(NavResult.Canceled)
        } else if (backStackEntry.savedStateHandle.contains(resultKey)) {
            val result = backStackEntry.savedStateHandle.get<String>(resultKey)?.let {
                kSerializerByteArray.fromJsonString(it)
            }!!
            backStackEntry.savedStateHandle.remove<Any?>(resultKey)
            listener(NavResult.Value(result))
        }
    }

    private fun hasAnyResult(): Boolean {
        return backStackEntry.savedStateHandle.contains(canceledKey) ||
            backStackEntry.savedStateHandle.contains(resultKey)
    }
}
