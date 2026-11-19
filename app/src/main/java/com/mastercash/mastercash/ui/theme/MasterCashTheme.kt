package com.mastercash.mastercash.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.platform.LocalContext
import com.mastercash.mastercash.R

fun MasterCashTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        darkTheme -> dynamicDarkColorScheme(context)
        else -> dynamicLightColorScheme(context)
    }
    val customScheme = ColorScheme(
        primary = androidx.compose.ui.graphics.Color(0xFF2563EB),
        primaryVariant = androidx.compose.ui.graphics.Color(0xFF1E40AF),
        secondary = androidx.compose.ui.graphics.Color(0xFF0F766E),
        background = androidx.compose.ui.graphics.Color(0xFFF4F7FB),
        surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
        error = androidx.compose.ui.graphics.Color(0xFFDC2626),
        onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
        onSecondary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
        onBackground = androidx.compose.ui.graphics.Color(0xFF1F2937),
        onSurface = androidx.compose.ui.graphics.Color(0xFF1F2937),
        onError = androidx.compose.ui.graphics.Color(0xFFFFFFFF)
    )
    MaterialTheme(
        colorScheme = customScheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}
