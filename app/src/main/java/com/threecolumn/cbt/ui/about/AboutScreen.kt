package com.threecolumn.cbt.ui.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.threecolumn.cbt.R
import com.threecolumn.cbt.data.JournalEntryRepository
import com.threecolumn.cbt.data.ThoughtRecordRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    thoughtRecordRepository: ThoughtRecordRepository,
    journalEntryRepository: JournalEntryRepository,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc))
                    }
                }
            )
        }
    ) { padding ->
        val uriHandler = LocalUriHandler.current
        val licenseUrl = stringResource(R.string.license_url)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.about_technique_heading), style = MaterialTheme.typography.titleMedium)
            Text(
                stringResource(R.string.about_technique_body),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(stringResource(R.string.about_journal_heading), style = MaterialTheme.typography.titleMedium)
            Text(
                stringResource(R.string.about_journal_body, stringResource(R.string.journal_topic)),
                style = MaterialTheme.typography.bodyMedium
            )

            ThemePicker()
            LanguagePicker()
            DataTransferSection(
                thoughtRecordRepository = thoughtRecordRepository,
                journalEntryRepository = journalEntryRepository
            )

            Column {
                Text(
                    text = "${stringResource(R.string.about_author_label)}: ${stringResource(R.string.author_name)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${stringResource(R.string.about_license_label)}: $licenseUrl",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { uriHandler.openUri(licenseUrl) }
                )
            }

            Text(
                stringResource(R.string.about_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
