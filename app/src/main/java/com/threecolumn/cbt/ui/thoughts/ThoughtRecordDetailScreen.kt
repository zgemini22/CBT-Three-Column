package com.threecolumn.cbt.ui.thoughts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.threecolumn.cbt.R
import com.threecolumn.cbt.data.CognitiveDistortion
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThoughtRecordDetailScreen(
    recordId: Long,
    viewModel: ThoughtRecordViewModel,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val record by remember(recordId) { viewModel.observeById(recordId) }.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        record?.let {
                            DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(it.createdAt))
                        }.orEmpty()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        record?.let { viewModel.delete(it) }
                        onBack()
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete_desc))
                    }
                    IconButton(onClick = { onEdit(recordId) }) {
                        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit_desc))
                    }
                }
            )
        }
    ) { padding ->
        val current = record ?: return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (current.situation.isNotBlank()) {
                Column {
                    Text(
                        text = stringResource(R.string.situation_display_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = current.situation,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            DetailSection(number = "1", title = stringResource(R.string.section_automatic_thought)) {
                Text(text = current.automaticThought, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = stringResource(R.string.belief_before_display, current.beliefBefore),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            DetailSection(number = "2", title = stringResource(R.string.section_distortions)) {
                val distortionLabels = current.distortionKeys
                    .mapNotNull { CognitiveDistortion.fromStorageKey(it) }
                    .map { stringResource(it.labelRes) }
                Text(
                    text = if (distortionLabels.isEmpty()) {
                        stringResource(R.string.distortions_none_selected)
                    } else {
                        distortionLabels.joinToString(" · ")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (distortionLabels.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else Color.Unspecified
                )
            }

            DetailSection(number = "3", title = stringResource(R.string.section_rational_response)) {
                Text(text = current.rationalResponse, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = stringResource(R.string.belief_after_display, current.beliefAfter),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun DetailSection(number: String, title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = "$number. $title",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        content()
    }
}
