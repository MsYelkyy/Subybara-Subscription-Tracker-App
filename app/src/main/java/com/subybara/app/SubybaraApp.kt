package com.subybara.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.navigation.SubybaraNavHost

@Composable
fun SubybaraApp() {
    SubybaraTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SubybaraNavHost()
        }
    }
}
