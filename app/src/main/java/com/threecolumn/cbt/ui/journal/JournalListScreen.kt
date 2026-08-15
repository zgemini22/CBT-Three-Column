package com.threecolumn.cbt.ui.journal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.material.icons.filled.PushPin
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.threecolumn.cbt.R
import com.threecolumn.cbt.data.JournalEntry
import com.threecolumn.cbt.ui.theme.NotebookColors
import com.threecolumn.cbt.ui.theme.NotebookFont
import com.threecolumn.cbt.ui.theme.notebookMargin
import java.text.DateFormat
import java.util.Date

private val ItemSpacing = 14.dp

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
    val itemHeights = remember { mutableStateMapOf<Long, Int>() }
    val itemSpacingPx = with(LocalDensity.current) { ItemSpacing.toPx() }
    val reorderDesc = stringResource(R.string.journal_reorder_desc)

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
            if (localOrder.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 96.dp),
                    verticalArrangement = Arrangement.spacedBy(ItemSpacing),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(localOrder, key = { it.id }) { entry ->
                        val isDragging = entry.id == draggingId
                        val cardModifier = Modifier
                            .onGloballyPositioned { coords -> itemHeights[entry.id] = coords.size.height }
                            .zIndex(if (isDragging) 1f else 0f)
                            .graphicsLayer(translationY = if (isDragging) dragOffsetY else 0f)
                            .let { if (isDragging) it else it.animateItem() }
                        val dragHandleModifier = Modifier.pointerInput(entry.id) {
                            detectDragGesturesAfterLongPress(
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
                                    val height = itemHeights[entry.id]
                                    if (height != null) {
                                        val step = height + itemSpacingPx
                                        val currentIndex = localOrder.indexOfFirst { it.id == entry.id }
                                        if (dragOffsetY > step / 2 && currentIndex < localOrder.lastIndex) {
                                            localOrder = localOrder.toMutableList().apply {
                                                add(currentIndex + 1, removeAt(currentIndex))
                                            }
                                            dragOffsetY -= step
                                        } else if (dragOffsetY < -step / 2 && currentIndex > 0) {
                                            localOrder = localOrder.toMutableList().apply {
                                                add(currentIndex - 1, removeAt(currentIndex))
                                            }
                                            dragOffsetY += step
                                        }
                                    }
                                }
                            )
                        }
                        JournalEntryCard(
                            entry = entry,
                            onClick = { if (draggingId == null) onOpenEntry(entry.id) },
                            onTogglePin = { viewModel.togglePin(entry) },
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
private fun JournalEntryCard(
    entry: JournalEntry,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    reorderDesc: String,
    modifier: Modifier = Modifier,
    dragHandleModifier: Modifier = Modifier
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
                .padding(start = 36.dp, top = 12.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = DateFormat.getDateInstance(DateFormat.FULL).format(Date(entry.createdAt)),
                    style = MaterialTheme.typography.labelMedium,
                    color = NotebookColors.inkFaded,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.PushPin,
                    contentDescription = stringResource(
                        if (entry.pinned) R.string.journal_unpin_desc else R.string.journal_pin_desc
                    ),
                    tint = if (entry.pinned) NotebookColors.penBlue else NotebookColors.inkFaded,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(onClick = onTogglePin)
                )
                Icon(
                    imageVector = Icons.Filled.DragHandle,
                    contentDescription = reorderDesc,
                    tint = NotebookColors.inkFaded,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp)
                        .then(dragHandleModifier)
                )
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
