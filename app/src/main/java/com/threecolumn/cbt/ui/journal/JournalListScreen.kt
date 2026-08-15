package com.threecolumn.cbt.ui.journal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.threecolumn.cbt.R
import com.threecolumn.cbt.data.JournalEntry
import com.threecolumn.cbt.ui.theme.NotebookColors
import com.threecolumn.cbt.ui.theme.NotebookFont
import com.threecolumn.cbt.ui.theme.notebookMargin
import java.text.DateFormat
import java.util.Date

@Composable
fun JournalListScreen(
    viewModel: JournalViewModel,
    onOpenEntry: (Long) -> Unit,
    onNewEntry: () -> Unit
) {
    val entries by viewModel.entries.collectAsState()
    var sortAscending by remember { mutableStateOf(false) }
    val sortedEntries = remember(entries, sortAscending) {
        val byDate = if (sortAscending) {
            compareByDescending<JournalEntry> { it.pinned }.thenBy { it.createdAt }
        } else {
            compareByDescending<JournalEntry> { it.pinned }.thenByDescending { it.createdAt }
        }
        entries.sortedWith(byDate)
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
            if (sortedEntries.isEmpty()) {
                EmptyState()
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    SortToggle(ascending = sortAscending, onToggle = { sortAscending = !sortAscending })
                }
                LazyColumn(
                    contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 96.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(sortedEntries, key = { it.id }) { entry ->
                        JournalEntryCard(
                            entry = entry,
                            onClick = { onOpenEntry(entry.id) },
                            onTogglePin = { viewModel.togglePin(entry) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SortToggle(ascending: Boolean, onToggle: () -> Unit) {
    TextButton(onClick = onToggle) {
        Icon(
            imageVector = if (ascending) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = if (ascending) stringResource(R.string.journal_sort_oldest) else stringResource(R.string.journal_sort_newest),
            style = MaterialTheme.typography.labelLarge
        )
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
private fun JournalEntryCard(entry: JournalEntry, onClick: () -> Unit, onTogglePin: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = NotebookColors.paper),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
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
