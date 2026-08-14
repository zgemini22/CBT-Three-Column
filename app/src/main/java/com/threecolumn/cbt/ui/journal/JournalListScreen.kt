package com.threecolumn.cbt.ui.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.threecolumn.cbt.data.JournalEntry
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalListScreen(
    viewModel: JournalViewModel,
    onOpenEntry: (Long) -> Unit,
    onNewEntry: (String) -> Unit
) {
    val entries by viewModel.entries.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onNewEntry("") }) {
                Icon(Icons.Filled.Add, contentDescription = "New journal entry")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            PromptRow(onPromptSelected = onNewEntry)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PromptRow(onPromptSelected: (String) -> Unit) {
    Text(
        text = "Need a prompt?",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(suggestedJournalPrompts) { prompt ->
            SuggestionChip(
                onClick = { onPromptSelected(prompt) },
                label = { Text(prompt, maxLines = 2) },
                colors = SuggestionChipDefaults.suggestionChipColors()
            )
        }
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
                text = "Your journal is empty",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Pick a prompt above or tap + to start writing freely — about this topic, a hobby idea, or anything else on your mind.",
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
        colors = CardDefaults.cardColors(containerColor = DiaryPalette.paper),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .ruledPaper(lineSpacing = 28.dp, topInset = 46.dp, marginInset = 28.dp)
                .padding(start = 36.dp, top = 12.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Text(
                text = DateFormat.getDateInstance(DateFormat.FULL).format(Date(entry.createdAt)),
                style = MaterialTheme.typography.labelMedium,
                fontFamily = DiaryFont,
                color = DiaryPalette.inkFaded
            )
            if (entry.prompt.isNotBlank()) {
                Text(
                    text = entry.prompt,
                    style = MaterialTheme.typography.titleSmall,
                    fontFamily = DiaryFont,
                    fontStyle = FontStyle.Italic,
                    color = DiaryPalette.ink,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Text(
                text = entry.body,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = DiaryFont,
                color = DiaryPalette.ink,
                maxLines = 3,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}
