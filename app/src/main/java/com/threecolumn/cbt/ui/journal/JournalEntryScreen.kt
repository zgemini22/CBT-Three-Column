package com.threecolumn.cbt.ui.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.threecolumn.cbt.R
import com.threecolumn.cbt.data.JournalEntry
import com.threecolumn.cbt.ui.theme.NotebookColors
import com.threecolumn.cbt.ui.theme.NotebookFont
import com.threecolumn.cbt.ui.theme.ruledPaper
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntryScreen(
    entryId: Long?,
    viewModel: JournalViewModel,
    onDone: () -> Unit
) {
    var existing by remember { mutableStateOf<JournalEntry?>(null) }
    var loaded by remember { mutableStateOf(entryId == null) }

    var body by remember { mutableStateOf("") }
    val newEntryCreatedAt = remember { System.currentTimeMillis() }

    LaunchedEffect(entryId) {
        if (entryId != null) {
            val entry = viewModel.getById(entryId)
            existing = entry
            if (entry != null) {
                body = entry.body
            }
            loaded = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        DateFormat.getDateInstance(DateFormat.FULL)
                            .format(Date(existing?.createdAt ?: newEntryCreatedAt))
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NotebookColors.paper),
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc), tint = NotebookColors.ink)
                    }
                },
                actions = {
                    if (existing != null) {
                        IconButton(onClick = {
                            existing?.let { viewModel.delete(it) }
                            onDone()
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete_desc), tint = NotebookColors.ink)
                        }
                    }
                    IconButton(
                        onClick = {
                            if (body.isNotBlank()) {
                                viewModel.save(
                                    JournalEntry(
                                        id = existing?.id ?: 0,
                                        createdAt = existing?.createdAt ?: newEntryCreatedAt,
                                        body = body.trim()
                                    )
                                )
                                onDone()
                            }
                        },
                        enabled = body.isNotBlank()
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.save_desc), tint = NotebookColors.ink)
                    }
                }
            )
        }
    ) { padding ->
        if (!loaded) return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NotebookColors.paper)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .ruledPaper(lineSpacing = 32.dp, topInset = 60.dp, marginInset = 32.dp)
                .padding(start = 40.dp, top = 12.dp, end = 20.dp, bottom = 40.dp)
        ) {
            Text(
                text = stringResource(R.string.journal_topic),
                style = MaterialTheme.typography.titleSmall,
                fontStyle = FontStyle.Italic,
                color = NotebookColors.inkFaded,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                if (body.isEmpty()) {
                    Text(
                        text = stringResource(R.string.journal_write_placeholder),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = NotebookFont,
                            fontSize = 17.sp,
                            lineHeight = 32.sp
                        ),
                        color = NotebookColors.inkFaded
                    )
                }
                BasicTextField(
                    value = body,
                    onValueChange = { body = it },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = NotebookFont,
                        fontSize = 17.sp,
                        lineHeight = 32.sp,
                        color = NotebookColors.ink
                    ),
                    cursorBrush = SolidColor(NotebookColors.ink),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
