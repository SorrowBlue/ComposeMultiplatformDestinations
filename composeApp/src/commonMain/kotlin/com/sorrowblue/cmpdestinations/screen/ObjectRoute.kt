package com.sorrowblue.cmpdestinations.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.cmpdestinations.result.NavResult
import com.sorrowblue.cmpdestinations.result.NavResultReceiver
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
internal data object ObjectRoute

@Destination<ObjectRoute>
@Composable
internal fun ObjectRouteScreen(
    navController: NavController,
    receiver: NavResultReceiver<AllSupportType, String>,
) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    receiver.onNavResult {
        scope.launch {
            when (it) {
                NavResult.Canceled -> snackbar.showSnackbar("received result canceled")
                is NavResult.Value<*> -> snackbar.showSnackbar(
                    "received result: ${it.value}"
                )
            }
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbar)
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("ObjectScreen", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = {
                navController.navigate(AllSupportType())
            }) {
                Text("AllSupportType")
            }
        }
    }
}
