package com.threecolumn.cbt.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** A paper/ink color set. Two instances exist (light and dark) so the notebook look can follow the system theme. */
data class NotebookPalette(
    val paper: Color,
    val paperAlt: Color,
    val marginLine: Color,
    val ink: Color,
    val inkFaded: Color,
    val penBlue: Color,
    val onPenBlue: Color,
    val highlighter: Color,
    val onHighlighter: Color,
    val errorPen: Color,
    val onErrorPen: Color
)

val LightNotebookPalette = NotebookPalette(
    paper = Color(0xFFFDF9EF),
    paperAlt = Color(0xFFF5EDD9),
    marginLine = Color(0xFFD3897E),
    ink = Color(0xFF221A12),
    inkFaded = Color(0xFF4A4034),
    penBlue = Color(0xFF2C4A78),
    onPenBlue = Color(0xFFFBF3E3),
    highlighter = Color(0xFFF3D48A),
    onHighlighter = Color(0xFF4A3B12),
    errorPen = Color(0xFFB23A32),
    onErrorPen = Color(0xFFFBF3E3)
)

val DarkNotebookPalette = NotebookPalette(
    paper = Color(0xFF231F1A),
    paperAlt = Color(0xFF2E2820),
    marginLine = Color(0xFFA85C52),
    ink = Color(0xFFEDE3D0),
    inkFaded = Color(0xFFB0A48D),
    penBlue = Color(0xFF8FB4E3),
    onPenBlue = Color(0xFF162335),
    highlighter = Color(0xFF5B4A20),
    onHighlighter = Color(0xFFF3D48A),
    errorPen = Color(0xFFE0897F),
    onErrorPen = Color(0xFF3A1512)
)

val LocalNotebookPalette = staticCompositionLocalOf { LightNotebookPalette }

/** Reads the current (light or dark, per the active theme) notebook palette. */
object NotebookColors {
    val paper: Color @Composable get() = LocalNotebookPalette.current.paper
    val paperAlt: Color @Composable get() = LocalNotebookPalette.current.paperAlt
    val marginLine: Color @Composable get() = LocalNotebookPalette.current.marginLine
    val ink: Color @Composable get() = LocalNotebookPalette.current.ink
    val inkFaded: Color @Composable get() = LocalNotebookPalette.current.inkFaded
    val penBlue: Color @Composable get() = LocalNotebookPalette.current.penBlue
    val onPenBlue: Color @Composable get() = LocalNotebookPalette.current.onPenBlue
    val highlighter: Color @Composable get() = LocalNotebookPalette.current.highlighter
    val onHighlighter: Color @Composable get() = LocalNotebookPalette.current.onHighlighter
    val errorPen: Color @Composable get() = LocalNotebookPalette.current.errorPen
    val onErrorPen: Color @Composable get() = LocalNotebookPalette.current.onErrorPen
}

val NotebookFont: FontFamily = FontFamily.Serif

/**
 * A single vertical margin rule, like the red line on ruled notebook paper.
 * Horizontal ruling was tried and dropped: with variable-height wrapped text,
 * fixed-interval lines can't stay aligned to real text baselines and end up
 * cutting through the middle of words instead of sitting under them.
 */
@Composable
fun Modifier.notebookMargin(marginInset: Dp = 40.dp): Modifier {
    val color = NotebookColors.marginLine
    return this.drawBehind {
        val marginPx = marginInset.toPx()
        drawLine(
            color = color,
            start = Offset(marginPx, 0f),
            end = Offset(marginPx, size.height),
            strokeWidth = 1.5.dp.toPx()
        )
    }
}
