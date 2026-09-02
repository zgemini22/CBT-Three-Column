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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * A row of tappable page titles (e.g. "1. Automatic Thought"), with the current page bolded and
 * underlined. Pairs with a [androidx.compose.foundation.pager.HorizontalPager] on narrow screens
 * where a three-column layout doesn't fit: each numbered section becomes its own swipeable page,
 * and this tab row is where its title now lives instead of repeating atop the page's own content.
 */
@Composable
fun PageTabRow(
    titles: List<String>,
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        titles.forEachIndexed { index, title ->
            val selected = index == currentPage
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onPageSelected(index) }
                    .padding(horizontal = 4.dp, vertical = 10.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth(0.5f)
                        .height(2.dp)
                        .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                )
            }
        }
    }
}
