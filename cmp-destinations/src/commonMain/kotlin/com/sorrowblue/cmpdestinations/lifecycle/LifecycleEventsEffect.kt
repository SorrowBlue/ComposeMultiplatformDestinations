package com.sorrowblue.cmpdestinations.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
internal fun LifecycleEventsEffect(
    vararg events: Lifecycle.Event,
    key: Any,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: () -> Unit,
) {
    require(!events.contains(Lifecycle.Event.ON_DESTROY)) {
        "LifecycleEventEffect cannot be used to " +
            "listen for Lifecycle.Event.ON_DESTROY, since Compose disposes of the " +
            "composition before ON_DESTROY observers are invoked."
    }
    val currentOnEvent by rememberUpdatedState(onEvent)
    DisposableEffect(key, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, e ->
            if (events.contains(e)) {
                currentOnEvent()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
