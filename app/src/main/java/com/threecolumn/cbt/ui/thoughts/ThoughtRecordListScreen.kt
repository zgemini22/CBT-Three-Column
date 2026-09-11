package com.threecolumn.cbt.ui.thoughts

import androidx.activity.compose.BackHandler
import android.text.format.DateUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.threecolumn.cbt.R
import com.threecolumn.cbt.data.CognitiveDistortion
import com.threecolumn.cbt.data.ThoughtRecord
import com.threecolumn.cbt.ui.components.SearchField
import com.threecolumn.cbt.ui.theme.NotebookColors
import com.threecolumn.cbt.ui.theme.notebookMargin
import java.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ThoughtRecordListScreen(
    viewModel: ThoughtRecordViewModel,
    onOpenRecord: (Long) -> Unit,
    onNewRecord: () -> Unit
) {
    val records by viewModel.records.collectAsState()
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val distortionLabelByEntry = CognitiveDistortion.entries.associateWith { stringResource(it.labelRes) }
    val filteredRecords = remember(records, query, distortionLabelByEntry) {
        filterRecords(records, query, distortionLabelByEntry)
    }
    val groupedRecords = remember(filteredRecords) { groupByRecency(filteredRecords, context) }
    var selectedRecordIds by rememberSaveable { mutableStateOf(emptyList<Long>()) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val selectedIds = selectedRecordIds.toSet()
    val selectionMode = selectedRecordIds.isNotEmpty()

    LaunchedEffect(records) {
        val validIds = records.map { it.id }.toSet()
        val retainedIds = selectedRecordIds.filter(validIds::contains)
        if (retainedIds.size != selectedRecordIds.size) {
            selectedRecordIds = retainedIds
        }
    }

    fun toggleSelection(id: Long) {
        selectedRecordIds = if (id in selectedIds) {
            selectedRecordIds.filterNot { it == id }
        } else {
            selectedRecordIds + id
        }
    }

    BackHandler(enabled = selectionMode) {
        selectedRecordIds = emptyList()
    }

    Scaffold(
        topBar = {
            if (selectionMode) {
                TopAppBar(
                    title = { Text(stringResource(R.string.selected_count, selectedRecordIds.size)) },
                    navigationIcon = {
                        IconButton(onClick = { selectedRecordIds = emptyList() }) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = stringResource(R.string.clear_selection_desc)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showDeleteConfirmation = true }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.delete_desc)
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewRecord,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.new_thought_record_desc))
            }
        }
    ) { padding ->
        if (records.isEmpty()) {
            EmptyState(padding)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                SearchField(query = query, onQueryChange = { query = it })
                if (filteredRecords.isEmpty()) {
                    NoResultsState()
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 96.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .notebookMargin()
                    ) {
                        groupedRecords.forEach { (label, recordsInGroup) ->
                            item(key = "header::$label") {
                                GroupHeader(label)
                            }
                            items(recordsInGroup, key = { it.id }) { record ->
                                ThoughtRecordCard(
                                    record = record,
                                    isSelectionMode = selectionMode,
                                    isSelected = record.id in selectedIds,
                                    onClick = {
                                        if (selectionMode) toggleSelection(record.id) else onOpenRecord(record.id)
                                    },
                                    onLongClick = { toggleSelection(record.id) },
                                    onSelectionToggle = { toggleSelection(record.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(R.string.delete_selected_title)) },
            text = {
                Text(stringResource(R.string.delete_selected_message, selectedRecordIds.size))
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(records.filter { it.id in selectedIds })
                    selectedRecordIds = emptyList()
                    showDeleteConfirmation = false
                }) {
                    Text(stringResource(R.string.delete_desc))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(R.string.cancel_desc))
                }
            }
        )
    }
}

private fun filterRecords(
    records: List<ThoughtRecord>,
    query: String,
    distortionLabelByEntry: Map<CognitiveDistortion, String>
): List<ThoughtRecord> {
    val trimmed = query.trim()
    if (trimmed.isEmpty()) return records
    return records.filter { record ->
        record.situation.contains(trimmed, ignoreCase = true) ||
            record.automaticThought.contains(trimmed, ignoreCase = true) ||
            record.rationalResponse.contains(trimmed, ignoreCase = true) ||
            record.distortionKeys.any { key ->
                CognitiveDistortion.fromStorageKey(key)
                    ?.let { distortionLabelByEntry[it] }
                    ?.contains(trimmed, ignoreCase = true) == true
            }
    }
}

/**
 * Buckets records (already sorted newest-first) into Today / Yesterday / This Week / This Month /
 * "Month Year" groups. Because the input is sorted and the bucket thresholds only get older,
 * a single pass preserves the right group order with no extra sorting.
 */
private fun groupByRecency(
    records: List<ThoughtRecord>,
    context: android.content.Context
): List<Pair<String, List<ThoughtRecord>>> {
    if (records.isEmpty()) return emptyList()

    val now = System.currentTimeMillis()
    val startOfToday = startOfDay(now)
    val startOfYesterday = startOfToday - DAY_MILLIS
    val startOfThisWeek = startOfWeek(now)
    val startOfThisMonth = startOfMonth(now)

    val groups = LinkedHashMap<String, MutableList<ThoughtRecord>>()
    for (record in records) {
        val label = when {
            record.createdAt >= startOfToday -> context.getString(R.string.group_today)
            record.createdAt >= startOfYesterday -> context.getString(R.string.group_yesterday)
            record.createdAt >= startOfThisWeek -> context.getString(R.string.group_this_week)
            record.createdAt >= startOfThisMonth -> context.getString(R.string.group_this_month)
            else -> DateUtils.formatDateTime(
                context,
                record.createdAt,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_NO_MONTH_DAY or DateUtils.FORMAT_SHOW_YEAR
            )
        }
        groups.getOrPut(label) { mutableListOf() }.add(record)
    }
    return groups.map { it.key to it.value }
}

private fun startOfDay(timeMillis: Long): Long = Calendar.getInstance().apply {
    timeInMillis = timeMillis
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

private fun startOfWeek(timeMillis: Long): Long = Calendar.getInstance().apply {
    timeInMillis = timeMillis
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
}.timeInMillis

private fun startOfMonth(timeMillis: Long): Long = Calendar.getInstance().apply {
    timeInMillis = timeMillis
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    set(Calendar.DAY_OF_MONTH, 1)
}.timeInMillis

private const val DAY_MILLIS = 24 * 60 * 60 * 1000L

@Composable
private fun GroupHeader(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = NotebookColors.ink
    )
}

@Composable
private fun EmptyState(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.thought_records_empty_title),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.thought_records_empty_body),
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ThoughtRecordCard(
    record: ThoughtRecord,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onSelectionToggle: () -> Unit
) {
    Card(
        modifier = Modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick),
        border = BorderStroke(1.dp, NotebookColors.line),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = DateUtils.formatDateTime(
                        LocalContext.current,
                        record.createdAt,
                        DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_WEEKDAY or
                            DateUtils.FORMAT_ABBREV_ALL or DateUtils.FORMAT_NO_YEAR
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                if (isSelectionMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onSelectionToggle() },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            if (record.situation.isNotBlank()) {
                Text(
                    text = record.situation,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            Text(
                text = record.automaticThought,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                maxLines = 3,
                modifier = Modifier.padding(top = 4.dp)
            )
            val distortionLabels = record.distortionKeys
                .mapNotNull { CognitiveDistortion.fromStorageKey(it) }
                .map { stringResource(it.labelRes) }
            if (distortionLabels.isNotEmpty()) {
                Text(
                    text = distortionLabels.joinToString(" · "),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            // The rational response is what the exercise produces, so it is the body of the card:
            // set in ink, with a short accent rule marking it as the answer.
            if (record.rationalResponse.isNotBlank()) {
                Row(modifier = Modifier
                    .padding(top = 10.dp)
                    .height(IntrinsicSize.Min)) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = record.rationalResponse,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (record.hasBelief) {
                Text(
                    text = "${record.beliefBefore}% \u2192 ${record.beliefAfter}%",
                    // The serif font has no well-centered arrow glyph, so this line uses the system default.
                    style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Default),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
