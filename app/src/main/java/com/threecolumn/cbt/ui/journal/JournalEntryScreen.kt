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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.threecolumn.cbt.data.JournalEntry
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

    var prompt by remember { mutableStateOf(if (entryId == null) viewModel.consumePendingPrompt() else "") }
    var body by remember { mutableStateOf("") }
    val createdAt = remember { existing?.createdAt ?: System.currentTimeMillis() }

    LaunchedEffect(entryId) {
        if (entryId != null) {
            val entry = viewModel.getById(entryId)
            existing = entry
            if (entry != null) {
                prompt = entry.prompt
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
                            .format(Date(existing?.createdAt ?: createdAt)),
                        fontFamily = DiaryFont
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DiaryPalette.paper),
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = DiaryPalette.ink)
                    }
                },
                actions = {
                    if (existing != null) {
                        IconButton(onClick = {
                            existing?.let { viewModel.delete(it) }
                            onDone()
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = DiaryPalette.ink)
                        }
                    }
                    IconButton(
                        onClick = {
                            if (body.isNotBlank()) {
                                viewModel.save(
                                    JournalEntry(
                                        id = existing?.id ?: 0,
                                        createdAt = existing?.createdAt ?: createdAt,
                                        prompt = prompt.trim(),
                                        body = body.trim()
                                    )
                                )
                                onDone()
                            }
                        },
                        enabled = body.isNotBlank()
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "Save", tint = DiaryPalette.ink)
                    }
                }
            )
        }
    ) { padding ->
        if (!loaded) return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DiaryPalette.paper)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .ruledPaper(lineSpacing = 32.dp, topInset = 8.dp, marginInset = 32.dp)
                .padding(start = 40.dp, top = 8.dp, end = 20.dp, bottom = 40.dp)
        ) {
            DiaryLineField(
                value = prompt,
                onValueChange = { prompt = it },
                placeholder = "What are you writing about? (optional)",
                textStyle = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = DiaryFont,
                    fontStyle = FontStyle.Italic,
                    color = DiaryPalette.ink
                ),
                singleLine = true
            )
            DiaryLineField(
                value = body,
                onValueChange = { body = it },
                placeholder = "Dear diary…",
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = DiaryFont,
                    fontSize = 17.sp,
                    lineHeight = 32.sp,
                    color = DiaryPalette.ink
                ),
                singleLine = false,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun DiaryLineField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    textStyle: TextStyle,
    singleLine: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        if (value.isEmpty()) {
            Text(text = placeholder, style = textStyle, color = DiaryPalette.inkFaded)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = textStyle,
            singleLine = singleLine,
            cursorBrush = SolidColor(DiaryPalette.ink),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
