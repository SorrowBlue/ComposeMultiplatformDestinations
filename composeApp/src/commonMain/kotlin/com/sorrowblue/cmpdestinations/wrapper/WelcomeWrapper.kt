package com.sorrowblue.cmpdestinations.wrapper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sorrowblue.cmpdestinations.DestinationScope
import com.sorrowblue.cmpdestinations.DestinationWrapper

internal object WelcomeWrapper : DestinationWrapper {

    @Composable
    override fun DestinationScope.Content(content: @Composable () -> Unit) {
        var showContent by rememberSaveable { mutableStateOf(false) }
        if (!showContent) {
            Scaffold { contentPadding ->
                Column(
                    Modifier.padding(contentPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Welcome", style = MaterialTheme.typography.headlineMedium)
                    Button(onClick = {
                        showContent = true
                    }) {
                        Text("Start")
                    }
                }
            }
        } else {
            content()
        }
    }
}
