package com.threecolumn.cbt.ui.journal

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Colors chosen to read as aged paper regardless of the app's light/dark theme. */
object DiaryPalette {
    val paper = Color(0xFFFBF3E3)
    val ruleLine = Color(0xFFB7C4DA)
    val marginLine = Color(0xFFD3897E)
    val ink = Color(0xFF33291F)
    val inkFaded = Color(0xFF7A6E5C)
}

val DiaryFont: FontFamily = FontFamily.Serif

fun Modifier.ruledPaper(
    lineSpacing: Dp = 32.dp,
    topInset: Dp = 32.dp,
    marginInset: Dp = 40.dp,
    drawMargin: Boolean = true
): Modifier = this
    .drawBehind {
        val spacingPx = lineSpacing.toPx()
        var y = topInset.toPx()
        while (y < size.height) {
            drawLine(
                color = DiaryPalette.ruleLine,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
            y += spacingPx
        }
        if (drawMargin) {
            val marginPx = marginInset.toPx()
            drawLine(
                color = DiaryPalette.marginLine,
                start = Offset(marginPx, 0f),
                end = Offset(marginPx, size.height),
                strokeWidth = 1.5.dp.toPx()
            )
        }
    }
