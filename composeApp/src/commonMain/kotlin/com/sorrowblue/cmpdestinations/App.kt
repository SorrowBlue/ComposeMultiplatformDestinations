package com.sorrowblue.cmpdestinations

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.sorrowblue.cmpdestinations.screen.MainNavGraphImpl
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun App() {
    MaterialTheme {
        NavGraphNavHost(
            navGraph = remember { MainNavGraphImpl() },
            navController = rememberNavController(),
        )
    }
}

@Composable
@Preview
private fun AppPreview() {
    App()
}
