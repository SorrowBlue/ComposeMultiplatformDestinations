package com.sorrowblue.cmpdestinations.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.cmpdestinations.AndroidRoute
import com.sorrowblue.cmpdestinations.WrapperImpl
import com.sorrowblue.cmpdestinations.WrapperImpl2
import com.sorrowblue.cmpdestinations.WrapperImpl3
import com.sorrowblue.cmpdestinations.annotation.Destination

@Destination<AndroidRoute>(
    graph = MainGraph::class,
    wrappers = [WrapperImpl2::class, WrapperImpl::class, WrapperImpl3::class]
)
@Composable
internal fun AndroidScreen() {
    Scaffold { contentPadding ->
        Box(Modifier.padding(contentPadding)) {
            Text("AndroidScreen")
        }
    }
}
