package com.threecolumn.cbt.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val NotebookColorScheme = lightColorScheme(
    primary = NotebookColors.penBlue,
    onPrimary = NotebookColors.onPenBlue,
    primaryContainer = NotebookColors.paperAlt,
    onPrimaryContainer = NotebookColors.ink,
    secondary = NotebookColors.penBlue,
    onSecondary = NotebookColors.onPenBlue,
    secondaryContainer = NotebookColors.highlighter,
    onSecondaryContainer = NotebookColors.onHighlighter,
    background = NotebookColors.paper,
    onBackground = NotebookColors.ink,
    surface = NotebookColors.paper,
    onSurface = NotebookColors.ink,
    surfaceVariant = NotebookColors.paperAlt,
    onSurfaceVariant = NotebookColors.inkFaded,
    outline = NotebookColors.inkFaded,
    error = NotebookColors.errorPen,
    onError = NotebookColors.onErrorPen
)

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
    MaterialTheme(
        colorScheme = NotebookColorScheme,
        typography = NotebookTypography,
        content = content
    )
}
