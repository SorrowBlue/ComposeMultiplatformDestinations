package com.sorrowblue.cmpdestinations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable

@Serializable
internal data object AndroidRoute

internal object WrapperImpl : DestinationWrapper {

    @Composable
    override fun DestinationScope.Content(content: @Composable () -> Unit) {
        var show by remember { mutableStateOf(false) }
        if (!show) {
            Scaffold { contentPadding ->
                Box(Modifier.padding(contentPadding)) {
                    Button(onClick = {
                        show = !show
                    }) {
                        Text("Show")
                    }
                }
            }
        } else {
            content()
        }
    }
}

internal object WrapperImpl2 : DestinationWrapper {

    @Composable
    override fun DestinationScope.Content(content: @Composable () -> Unit) {
        var show by remember { mutableStateOf(false) }
        if (!show) {
            Scaffold { contentPadding ->
                Box(Modifier.padding(contentPadding)) {
                    Button(onClick = {
                        show = !show
                    }) {
                        Text("Show2")
                    }
                }
            }
        } else {
            content()
        }
    }
}

internal object WrapperImpl3 : DestinationWrapper {

    @Composable
    override fun DestinationScope.Content(content: @Composable () -> Unit) {
        var show by remember { mutableStateOf(false) }
        if (!show) {
            Scaffold { contentPadding ->
                Box(Modifier.padding(contentPadding)) {
                    Button(onClick = {
                        show = !show
                    }) {
                        Text("Show2")
                    }
                }
            }
        } else {
            content()
        }
    }
}
