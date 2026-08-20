package com.threecolumn.cbt.ui.journal

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.threecolumn.cbt.R
import com.threecolumn.cbt.data.JournalEntry
import com.threecolumn.cbt.ui.components.SearchField
import com.threecolumn.cbt.ui.theme.NotebookColors
import com.threecolumn.cbt.ui.theme.NotebookFont
import com.threecolumn.cbt.ui.theme.notebookMargin
import java.text.DateFormat
import java.util.Date

private val ItemSpacing = 14.dp

// A generous fixed estimate rather than a live-measured item height: cards vary a little in
// height depending on body length, but a fixed step is simpler and never silently fails to
// react if a measurement hasn't landed yet -- it always swaps predictably every ~step of drag.
private val ReorderStep = 104.dp

@Composable
fun JournalListScreen(
    viewModel: JournalViewModel,
    onOpenEntry: (Long) -> Unit,
    onNewEntry: () -> Unit
) {
    val entries by viewModel.entries.collectAsState()
    var localOrder by remember { mutableStateOf(entries) }
    var draggingId by remember { mutableStateOf<Long?>(null) }
    var dragOffsetY by remember { mutableStateOf(0f) }
    var query by remember { mutableStateOf("") }
    val stepPx = with(LocalDensity.current) { (ReorderStep + ItemSpacing).toPx() }
    val reorderDesc = stringResource(R.string.journal_reorder_desc)
    val isSearching = query.isNotBlank()
    val filteredEntries = remember(localOrder, query) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) localOrder else localOrder.filter { it.body.contains(trimmed, ignoreCase = true) }
    }

    LaunchedEffect(entries) {
        if (draggingId == null) localOrder = entries
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNewEntry) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.journal_new_page_desc))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TopicHeader()
            if (entries.isEmpty()) {
                EmptyState()
            } else {
                SearchField(query = query, onQueryChange = { query = it })
                if (filteredEntries.isEmpty()) {
                    NoResultsState()
                } else if (isSearching) {
                    // Reordering a filtered subset has no well-defined meaning, so search results
                    // are shown as a plain, non-draggable list instead of the reorderable one below.
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 96.dp),
                        verticalArrangement = Arrangement.spacedBy(ItemSpacing),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredEntries, key = { it.id }) { entry ->
                            JournalEntryCard(
                                entry = entry,
                                onClick = { onOpenEntry(entry.id) },
                                reorderDesc = reorderDesc,
                                showDragHandle = false
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 96.dp),
                        verticalArrangement = Arrangement.spacedBy(ItemSpacing),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(localOrder, key = { it.id }) { entry ->
                            val isDragging = entry.id == draggingId
                            val cardModifier = Modifier
                                .zIndex(if (isDragging) 1f else 0f)
                                .graphicsLayer(translationY = if (isDragging) dragOffsetY else 0f)

                            val dragHandleModifier = Modifier.pointerInput(entry.id) {
                                detectDragGestures(
                                    onDragStart = {
                                        draggingId = entry.id
                                        dragOffsetY = 0f
                                    },
                                    onDragEnd = {
                                        draggingId = null
                                        dragOffsetY = 0f
                                        viewModel.persistOrder(localOrder)
                                    },
                                    onDragCancel = {
                                        draggingId = null
                                        dragOffsetY = 0f
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffsetY += dragAmount.y
                                        val currentIndex = localOrder.indexOfFirst { it.id == entry.id }
                                        if (dragOffsetY > stepPx / 2 && currentIndex < localOrder.lastIndex) {
                                            localOrder = localOrder.toMutableList().apply {
                                                add(currentIndex + 1, removeAt(currentIndex))
                                            }
                                            dragOffsetY -= stepPx
                                        } else if (dragOffsetY < -stepPx / 2 && currentIndex > 0) {
                                            localOrder = localOrder.toMutableList().apply {
                                                add(currentIndex - 1, removeAt(currentIndex))
                                            }
                                            dragOffsetY += stepPx
                                        }
                                    }
                                )
                            }
                            JournalEntryCard(
                                entry = entry,
                                onClick = { if (draggingId == null) onOpenEntry(entry.id) },
                                reorderDesc = reorderDesc,
                                modifier = cardModifier,
                                dragHandleModifier = dragHandleModifier
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopicHeader() {
    Column(modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)) {
        Text(
            text = stringResource(R.string.journal_topic_header),
            style = MaterialTheme.typography.labelLarge,
            color = NotebookColors.inkFaded
        )
        Text(
            text = stringResource(R.string.journal_topic),
            style = MaterialTheme.typography.titleMedium,
            fontFamily = NotebookFont,
            fontWeight = FontWeight.Bold,
            color = NotebookColors.ink,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.journal_empty_title),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.journal_empty_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NoResultsState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.search_no_results_title),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.search_no_results_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun JournalEntryCard(
    entry: JournalEntry,
    onClick: () -> Unit,
    reorderDesc: String,
    modifier: Modifier = Modifier,
    dragHandleModifier: Modifier = Modifier,
    showDragHandle: Boolean = true
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = NotebookColors.paper),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .notebookMargin(marginInset = 28.dp)
                .padding(start = 36.dp, top = 12.dp, end = 8.dp, bottom = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = DateFormat.getDateInstance(DateFormat.FULL).format(Date(entry.createdAt)),
                    style = MaterialTheme.typography.labelMedium,
                    color = NotebookColors.inkFaded,
                    modifier = Modifier.weight(1f)
                )
                if (showDragHandle) {
                    // A generous 44dp touch target around a smaller glyph, so the handle is easy to grab.
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .then(dragHandleModifier),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DragHandle,
                            contentDescription = reorderDesc,
                            tint = NotebookColors.inkFaded,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
            Text(
                text = entry.body,
                style = MaterialTheme.typography.bodyLarge,
                color = NotebookColors.ink,
                maxLines = 4,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}
