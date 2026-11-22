package com.mastercash.mastercash.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MasterCashTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val customScheme = lightColorScheme(
        primary = Color(0xFF2563EB),
        secondary = Color(0xFF0F766E),
        background = Color(0xFFF4F7FB),
        surface = Color(0xFFFFFFFF),
        error = Color(0xFFDC2626),
        onPrimary = Color(0xFFFFFFFF),
        onSecondary = Color(0xFFFFFFFF),
        onBackground = Color(0xFF1F2937),
        onSurface = Color(0xFF1F2937),
        onError = Color(0xFFFFFFFF)
    )
    MaterialTheme(
        colorScheme = customScheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}
