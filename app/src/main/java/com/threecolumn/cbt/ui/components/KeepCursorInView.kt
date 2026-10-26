package com.threecolumn.cbt.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Where an OutlinedTextField with a label draws its text: 8dp of label room, then 16dp of padding. */
val OutlinedTextFieldTextTop = 24.dp
val OutlinedTextFieldTextSide = 16.dp

/**
 * Keeps the line being typed on screen, above the keyboard, inside a scrolling column.
 *
 * Compose (1.6) scrolls a text field's cursor into view only at the moment the field gains focus,
 * which is before the keyboard has finished opening, and never again while typing. So in a field
 * near the bottom of a page, or one that grows as you type, the line being written ends up under
 * the keyboard. This follows the cursor whenever it moves, the text reflows, or the keyboard
 * changes height. The scrolling column must stop at the keyboard (imePadding) for "in view" to
 * mean above it.
 *
 * The cursor position is found by laying the text out again with [textStyle], since decorated
 * fields like OutlinedTextField don't expose their layout; [textTop] and [textSide] say where in
 * the field the text starts.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.keepCursorInView(
    value: TextFieldValue,
    textStyle: TextStyle,
    textTop: Dp = 0.dp,
    textSide: Dp = 0.dp
): Modifier {
    val requester = remember { BringIntoViewRequester() }
    val measurer = rememberTextMeasurer()
    var focused by remember { mutableStateOf(false) }
    var fieldWidth by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)

    LaunchedEffect(focused, value.text, value.selection, fieldWidth, imeBottom) {
        if (!focused || fieldWidth == 0) return@LaunchedEffect
        val top = with(density) { textTop.toPx() }
        val side = with(density) { textSide.toPx() }
        val textWidth = (fieldWidth - 2 * side).toInt().coerceAtLeast(1)
        val layout = measurer.measure(value.text, textStyle, constraints = Constraints(maxWidth = textWidth))
        val cursor = layout.getCursorRect(value.selection.end.coerceIn(0, value.text.length))
            .translate(side, top)
        // A little room below, so the line doesn't sit flush against the keyboard.
        requester.bringIntoView(cursor.copy(bottom = cursor.bottom + with(density) { 16.dp.toPx() }))
    }

    return this
        .bringIntoViewRequester(requester)
        .onSizeChanged { fieldWidth = it.width }
        .onFocusChanged { focused = it.hasFocus }
}
