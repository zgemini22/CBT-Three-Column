package com.threecolumn.cbt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.threecolumn.cbt.ui.theme.NotebookColors

/**
 * A row of tappable page tabs. Each tab shows its entry in [labels] when given, otherwise its
 * number (1, 2, 3, ...). The current page is set in ink and underlined in the accent colour.
 * Pairs with a [androidx.compose.foundation.pager.HorizontalPager] on narrow screens where a
 * three-column layout doesn't fit: each section becomes its own swipeable page instead.
 */
@Composable
fun PageTabRow(
    pageCount: Int,
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    labels: List<String>? = null
) {
    Row(modifier = modifier.fillMaxWidth()) {
        repeat(pageCount) { index ->
            val selected = index == currentPage
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onPageSelected(index) }
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    text = labels?.getOrNull(index) ?: "${index + 1}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected) NotebookColors.ink else NotebookColors.inkFaded,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .fillMaxWidth(0.6f)
                        .height(2.dp)
                        .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                )
            }
        }
    }
}
