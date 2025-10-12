package com.sorrowblue.cmpdestinations

import androidx.compose.ui.window.DialogProperties

interface DestinationStyle {

    data object Composable : DestinationComposableStyle

    data object Dialog : DestinationDialogStyle

    @Deprecated(message = "Use DestinationDialogStyle instead")
    data object Auto : DestinationComposableStyle, DestinationDialogStyle
}

interface DestinationDialogStyle : DestinationStyle {
    val dialogProperties: DialogProperties get() = DialogProperties()
}

interface DestinationComposableStyle : DestinationStyle
