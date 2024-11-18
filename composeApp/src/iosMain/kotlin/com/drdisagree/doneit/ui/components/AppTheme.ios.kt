package com.drdisagree.doneit.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.drdisagree.doneit.ui.theme.darkColorScheme
import com.drdisagree.doneit.ui.theme.lightColorScheme

@Composable
actual fun AppTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme else lightColorScheme,
        content = content
    )
}