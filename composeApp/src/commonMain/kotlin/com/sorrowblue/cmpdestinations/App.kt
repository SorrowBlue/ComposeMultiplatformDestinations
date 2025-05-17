package com.sorrowblue.cmpdestinations

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.sorrowblue.cmpdestinations.screen.MainGraph
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun App() {
    MaterialTheme {
        NavGraphNavHost(
            graphNavigation = MainGraph,
            navController = rememberNavController(),
        )
    }
}

@Composable
@Preview
private fun AppPreview() {
    App()
}
