package com.threecolumn.cbt.ui.about

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.threecolumn.cbt.R
import com.threecolumn.cbt.data.CognitiveDistortion
import com.threecolumn.cbt.data.DataTransfer
import com.threecolumn.cbt.data.JournalEntryRepository
import com.threecolumn.cbt.data.ThoughtRecordRepository
import kotlinx.coroutines.launch

private const val IMPORT_FORMAT_EXAMPLE = """{
  "thoughtRecords": [
    {
      "situation": "Optional context",
      "automaticThought": "The upsetting thought",
      "distortions": ["ALL_OR_NOTHING", "LABELING"],
      "rationalResponse": "A fairer response",
      "beliefBefore": 80,
      "beliefAfter": 30
    }
  ],
  "journalEntries": [
    { "body": "Free-form text for a page", "pinned": false }
  ]
}"""

@Composable
fun DataTransferSection(
    thoughtRecordRepository: ThoughtRecordRepository,
    journalEntryRepository: JournalEntryRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showImportFormatDialog by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val json = DataTransfer.export(
                thoughtRecordRepository.getAllOnce(),
                journalEntryRepository.getAllOnce()
            )
            statusMessage = try {
                context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
                context.getString(R.string.data_export_success)
            } catch (e: Exception) {
                context.getString(R.string.data_export_failed)
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            statusMessage = try {
                val text = context.contentResolver.openInputStream(uri)?.use { it.bufferedReader().readText() }
                    ?: throw DataTransfer.ImportException("Could not read the selected file.")
                val result = DataTransfer.parseImport(text)
                thoughtRecordRepository.saveAll(result.thoughtRecords)
                journalEntryRepository.saveAll(result.journalEntries)
                context.getString(
                    R.string.data_import_success,
                    result.thoughtRecords.size,
                    result.journalEntries.size
                )
            } catch (e: Exception) {
                context.getString(R.string.data_import_failed)
            }
        }
    }

    if (showImportFormatDialog) {
        AlertDialog(
            onDismissRequest = { showImportFormatDialog = false },
            title = { Text(stringResource(R.string.data_import_format_title)) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(R.string.data_import_format_intro), style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = IMPORT_FORMAT_EXAMPLE,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                    )
                    Text(stringResource(R.string.data_import_format_note), style = MaterialTheme.typography.bodySmall)
                    Text(
                        stringResource(R.string.data_import_format_distortion_codes),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = CognitiveDistortion.entries.joinToString(", ") { it.name },
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showImportFormatDialog = false
                    importLauncher.launch(arrayOf("application/json"))
                }) {
                    Text(stringResource(R.string.data_import_choose_file))
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportFormatDialog = false }) {
                    Text(stringResource(R.string.cancel_desc))
                }
            }
        )
    }

    Column {
        Text(stringResource(R.string.data_section_title), style = MaterialTheme.typography.titleMedium)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            TextButton(onClick = { exportLauncher.launch("three-column-method-export.json") }) {
                Text(stringResource(R.string.data_export_action))
            }
            TextButton(onClick = { showImportFormatDialog = true }) {
                Text(stringResource(R.string.data_import_action))
            }
        }
        statusMessage?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
