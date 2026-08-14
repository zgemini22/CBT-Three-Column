package com.threecolumn.cbt.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Fixed paper/ink palette the whole app is themed from — a physical notebook
 * doesn't switch to a dark mode, so this look stays constant regardless of
 * the device theme.
 */
object NotebookColors {
    val paper = Color(0xFFFBF3E3)
    val paperAlt = Color(0xFFF3E7C9)
    val ruleLine = Color(0xFFB7C4DA)
    val marginLine = Color(0xFFD3897E)
    val ink = Color(0xFF33291F)
    val inkFaded = Color(0xFF7A6E5C)
    val penBlue = Color(0xFF2C4A78)
    val onPenBlue = Color(0xFFFBF3E3)
    val highlighter = Color(0xFFF3D48A)
    val onHighlighter = Color(0xFF4A3B12)
    val errorPen = Color(0xFFB23A32)
    val onErrorPen = Color(0xFFFBF3E3)
}

val NotebookFont: FontFamily = FontFamily.Serif

/** Draws faint ruled lines and a margin rule behind whatever it's applied to, like notebook paper. */
fun Modifier.ruledPaper(
    lineSpacing: Dp = 32.dp,
    topInset: Dp = 32.dp,
    marginInset: Dp = 40.dp,
    drawMargin: Boolean = true
): Modifier = this.drawBehind {
    val spacingPx = lineSpacing.toPx()
    var y = topInset.toPx()
    while (y < size.height) {
        drawLine(
            color = NotebookColors.ruleLine,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1.dp.toPx()
        )
        y += spacingPx
    }
    if (drawMargin) {
        val marginPx = marginInset.toPx()
        drawLine(
            color = NotebookColors.marginLine,
            start = Offset(marginPx, 0f),
            end = Offset(marginPx, size.height),
            strokeWidth = 1.5.dp.toPx()
        )
    }
}
