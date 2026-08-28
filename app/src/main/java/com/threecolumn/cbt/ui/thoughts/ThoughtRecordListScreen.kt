package com.threecolumn.cbt.ui.thoughts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.threecolumn.cbt.R
import com.threecolumn.cbt.data.CognitiveDistortion
import com.threecolumn.cbt.data.ThoughtRecord
import com.threecolumn.cbt.ui.components.SearchField
import com.threecolumn.cbt.ui.theme.notebookMargin
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNewRecord) {
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
                                ThoughtRecordCard(record = record, onClick = { onOpenRecord(record.id) })
                            }
                        }
                    }
                }
            }
        }
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
    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    val groups = LinkedHashMap<String, MutableList<ThoughtRecord>>()
    for (record in records) {
        val label = when {
            record.createdAt >= startOfToday -> context.getString(R.string.group_today)
            record.createdAt >= startOfYesterday -> context.getString(R.string.group_yesterday)
            record.createdAt >= startOfThisWeek -> context.getString(R.string.group_this_week)
            record.createdAt >= startOfThisMonth -> context.getString(R.string.group_this_month)
            else -> monthYearFormat.format(Date(record.createdAt))
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
        color = MaterialTheme.colorScheme.primary
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

@Composable
private fun ThoughtRecordCard(record: ThoughtRecord, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                    .format(Date(record.createdAt)),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = record.automaticThought,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                maxLines = 3,
                modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
            )
            val distortionLabels = record.distortionKeys
                .mapNotNull { CognitiveDistortion.fromStorageKey(it) }
                .map { stringResource(it.labelRes) }
            if (distortionLabels.isNotEmpty()) {
                Text(
                    text = distortionLabels.joinToString(" · "),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = "${record.beliefBefore}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // A drawn icon, not a text arrow glyph, so it's always centered on this row
                // regardless of the font's own (often bottom-heavy) arrow glyph metrics.
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(14.dp)
                )
                Text(
                    text = "${record.beliefAfter}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
