package com.sorrowblue.cmpdestinations.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import com.sorrowblue.cmpdestinations.annotation.DeepLink
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.cmpdestinations.IntentContents
import kotlinx.serialization.Serializable

@Serializable
internal data object DeeplinkRoute

@Composable
@Destination<DeeplinkRoute>(
    deeplinks = [
        DeepLink(uriPattern = "content://**"),
        DeepLink(action = "android.intent.action.VIEW"),
        DeepLink(mimeType = "text/plain"),
        DeepLink(uriPattern = "content://**", action = "android.intent.action.VIEW"),
        DeepLink(uriPattern = "content://**", mimeType = "text/plain"),
        DeepLink(action = "android.intent.action.VIEW", mimeType = "text/plain"),
        DeepLink(action = "android.intent.action.VIEW", mimeType = "image/*"),
        DeepLink(
            uriPattern = "content://**",
            action = "android.intent.action.VIEW",
            mimeType = "text/plain"
        ),
    ]
)
internal fun DeeplinkRouteScreen(navBackStackEntry: NavBackStackEntry) {
    Scaffold { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("DeeplinkRouteScreen", style = MaterialTheme.typography.headlineMedium)
            IntentContents(navBackStackEntry)
        }
    }
}
