package com.threecolumn.cbt.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private fun colorSchemeFor(palette: NotebookPalette, dark: Boolean): ColorScheme =
    if (dark) {
        darkColorScheme(
            primary = palette.penBlue,
            onPrimary = palette.onPenBlue,
            primaryContainer = palette.paperAlt,
            onPrimaryContainer = palette.ink,
            secondary = palette.penBlue,
            onSecondary = palette.onPenBlue,
            secondaryContainer = palette.highlighter,
            onSecondaryContainer = palette.onHighlighter,
            background = palette.paper,
            onBackground = palette.ink,
            surface = palette.paper,
            onSurface = palette.ink,
            surfaceVariant = palette.paperAlt,
            onSurfaceVariant = palette.inkFaded,
            outline = palette.inkFaded,
            error = palette.errorPen,
            onError = palette.onErrorPen
        )
    } else {
        lightColorScheme(
            primary = palette.penBlue,
            onPrimary = palette.onPenBlue,
            primaryContainer = palette.paperAlt,
            onPrimaryContainer = palette.ink,
            secondary = palette.penBlue,
            onSecondary = palette.onPenBlue,
            secondaryContainer = palette.highlighter,
            onSecondaryContainer = palette.onHighlighter,
            background = palette.paper,
            onBackground = palette.ink,
            surface = palette.paper,
            onSurface = palette.ink,
            surfaceVariant = palette.paperAlt,
            onSurfaceVariant = palette.inkFaded,
            outline = palette.inkFaded,
            error = palette.errorPen,
            onError = palette.onErrorPen
        )
    }

private val baseTypography = Typography()

private val NotebookTypography = Typography(
    displayLarge = baseTypography.displayLarge.copy(fontFamily = NotebookFont),
    displayMedium = baseTypography.displayMedium.copy(fontFamily = NotebookFont),
    displaySmall = baseTypography.displaySmall.copy(fontFamily = NotebookFont),
    headlineLarge = baseTypography.headlineLarge.copy(fontFamily = NotebookFont),
    headlineMedium = baseTypography.headlineMedium.copy(fontFamily = NotebookFont),
    headlineSmall = baseTypography.headlineSmall.copy(fontFamily = NotebookFont),
    titleLarge = baseTypography.titleLarge.copy(fontFamily = NotebookFont),
    titleMedium = baseTypography.titleMedium.copy(fontFamily = NotebookFont),
    titleSmall = baseTypography.titleSmall.copy(fontFamily = NotebookFont),
    bodyLarge = baseTypography.bodyLarge.copy(fontFamily = NotebookFont),
    bodyMedium = baseTypography.bodyMedium.copy(fontFamily = NotebookFont),
    bodySmall = baseTypography.bodySmall.copy(fontFamily = NotebookFont),
    labelLarge = baseTypography.labelLarge.copy(fontFamily = NotebookFont),
    labelMedium = baseTypography.labelMedium.copy(fontFamily = NotebookFont),
    labelSmall = baseTypography.labelSmall.copy(fontFamily = NotebookFont)
)

@Composable
fun ThreeColumnCbtTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val palette = if (dark) DarkNotebookPalette else LightNotebookPalette
    CompositionLocalProvider(LocalNotebookPalette provides palette) {
        MaterialTheme(
            colorScheme = colorSchemeFor(palette, dark),
            typography = NotebookTypography,
            content = content
        )
    }
}
