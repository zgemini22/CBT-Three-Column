package com.threecolumn.cbt.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

/** A quiet, private-feeling color set. Two instances exist (light and dark) so it can follow the system theme. */
data class NotebookPalette(
    val paper: Color,
    val paperAlt: Color,
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
    paper = Color(0xFFF4F6FA),
    paperAlt = Color(0xFFE7EBF2),
    ink = Color(0xFF1B2333),
    inkFaded = Color(0xFF5B6472),
    penBlue = Color(0xFF2A4E93),
    onPenBlue = Color(0xFFFFFFFF),
    highlighter = Color(0xFFE1E7FA),
    onHighlighter = Color(0xFF23345E),
    errorPen = Color(0xFFB3261E),
    onErrorPen = Color(0xFFFFFFFF)
)

val DarkNotebookPalette = NotebookPalette(
    paper = Color(0xFF12151C),
    paperAlt = Color(0xFF1C212C),
    ink = Color(0xFFE4E8F0),
    inkFaded = Color(0xFF97A0B2),
    penBlue = Color(0xFF7FA6FF),
    onPenBlue = Color(0xFF0E1B33),
    highlighter = Color(0xFF26304A),
    onHighlighter = Color(0xFFC4D2FF),
    errorPen = Color(0xFFFFB4AB),
    onErrorPen = Color(0xFF601410)
)

val LocalNotebookPalette = staticCompositionLocalOf { LightNotebookPalette }

/** Reads the current (light or dark, per the active theme) palette. */
object NotebookColors {
    val paper: Color @Composable get() = LocalNotebookPalette.current.paper
    val paperAlt: Color @Composable get() = LocalNotebookPalette.current.paperAlt
    val ink: Color @Composable get() = LocalNotebookPalette.current.ink
    val inkFaded: Color @Composable get() = LocalNotebookPalette.current.inkFaded
    val penBlue: Color @Composable get() = LocalNotebookPalette.current.penBlue
    val onPenBlue: Color @Composable get() = LocalNotebookPalette.current.onPenBlue
    val highlighter: Color @Composable get() = LocalNotebookPalette.current.highlighter
    val onHighlighter: Color @Composable get() = LocalNotebookPalette.current.onHighlighter
    val errorPen: Color @Composable get() = LocalNotebookPalette.current.errorPen
    val onErrorPen: Color @Composable get() = LocalNotebookPalette.current.onErrorPen
}

val NotebookFont: FontFamily = FontFamily.Default
