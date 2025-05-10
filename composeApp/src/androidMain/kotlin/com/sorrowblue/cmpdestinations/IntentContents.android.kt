package com.sorrowblue.cmpdestinations

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

@Composable
actual fun IntentContents(navBackStackEntry: NavBackStackEntry) {
    val context = LocalContext.current
    var bitmap: ImageBitmap? by remember { mutableStateOf(null) }
    var text: String? by remember { mutableStateOf(null) }
    LaunchedEffect(navBackStackEntry) {
        val intent =
            navBackStackEntry.savedStateHandle.get<Intent>(NavController.KEY_DEEP_LINK_INTENT)
                ?: return@LaunchedEffect kotlin.run {
                    text = "no intent"
                }
        val data = intent.data ?: return@LaunchedEffect kotlin.run {
            text = "no data"
        }
        if (intent.type == "image/*") {
            context.contentResolver.openInputStream(data)?.use {
                bitmap = BitmapFactory.decodeStream(it).asImageBitmap()
            } ?: kotlin.run {
                text = "no bitmap"
            }
        } else if (intent.type == "text/plain") {
            context.contentResolver.openInputStream(data)?.bufferedReader()?.use {
                text = it.readLines().joinToString("\n")
            } ?: kotlin.run {
                text = "no text"
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(200.dp)
        )
    }

    if (text != null) {
        Text(text = text!!)
    }
}
