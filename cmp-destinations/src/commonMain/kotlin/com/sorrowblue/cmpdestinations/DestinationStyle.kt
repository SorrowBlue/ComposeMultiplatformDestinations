package com.sorrowblue.cmpdestinations

sealed interface DestinationStyle {

    data object Composable : DestinationStyle

    data object Dialog : DestinationStyle

    data object Auto : DestinationStyle
}
