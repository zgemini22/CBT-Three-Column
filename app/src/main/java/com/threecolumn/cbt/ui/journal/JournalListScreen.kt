package com.threecolumn.cbt.ui.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.threecolumn.cbt.data.JournalEntry
import com.threecolumn.cbt.ui.theme.NotebookColors
import com.threecolumn.cbt.ui.theme.NotebookFont
import com.threecolumn.cbt.ui.theme.ruledPaper
import java.text.DateFormat
import java.util.Date

@Composable
fun JournalListScreen(
    viewModel: JournalViewModel,
    onOpenEntry: (Long) -> Unit,
    onNewEntry: () -> Unit
) {
    val entries by viewModel.entries.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNewEntry) {
                Icon(Icons.Filled.Add, contentDescription = "New journal page")
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
                LazyColumn(
                    contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 96.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(entries, key = { it.id }) { entry ->
                        JournalEntryCard(entry = entry, onClick = { onOpenEntry(entry.id) })
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
            text = "This journal's topic",
            style = MaterialTheme.typography.labelLarge,
            color = NotebookColors.inkFaded
        )
        Text(
            text = JOURNAL_TOPIC,
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
                text = "No pages yet",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Tap + to write your first page on this topic. Come back and add more pages whenever a new thought about it occurs to you.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun JournalEntryCard(entry: JournalEntry, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = NotebookColors.paper),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .ruledPaper(lineSpacing = 28.dp, topInset = 40.dp, marginInset = 28.dp)
                .padding(start = 36.dp, top = 12.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Text(
                text = DateFormat.getDateInstance(DateFormat.FULL).format(Date(entry.createdAt)),
                style = MaterialTheme.typography.labelMedium,
                color = NotebookColors.inkFaded
            )
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
