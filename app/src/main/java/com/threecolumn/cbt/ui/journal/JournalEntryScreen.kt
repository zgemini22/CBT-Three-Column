package com.threecolumn.cbt.ui.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.threecolumn.cbt.R
import com.threecolumn.cbt.data.JournalEntry
import com.threecolumn.cbt.ui.components.keepCursorInView
import com.threecolumn.cbt.ui.theme.NotebookColors
import com.threecolumn.cbt.ui.theme.NotebookFont
import com.threecolumn.cbt.ui.theme.notebookMargin
import com.threecolumn.cbt.util.shareText
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

    var title by remember { mutableStateOf("") }
    var bodyField by remember { mutableStateOf(TextFieldValue("")) }
    val body = bodyField.text
    val canSave = body.isNotBlank() || title.isNotBlank()
    val focusManager = LocalFocusManager.current
    val newEntryCreatedAt = remember { System.currentTimeMillis() }
    val context = LocalContext.current
    val customTitle by viewModel.customTitle.collectAsState()
    val journalTopicText = journalTitle(customTitle)
    val shareChooserTitle = stringResource(R.string.share_desc)

    LaunchedEffect(entryId) {
        if (entryId != null) {
            val entry = viewModel.getById(entryId)
            existing = entry
            if (entry != null) {
                title = entry.title
                bodyField = TextFieldValue(entry.body)
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
                    IconButton(
                        onClick = {
                            val dateText = DateFormat.getDateInstance(DateFormat.FULL)
                                .format(Date(existing?.createdAt ?: newEntryCreatedAt))
                            // An untitled page is shared under the journal's own title, as before.
                            val heading = JournalEntry.cleanTitle(title).ifEmpty { journalTopicText }
                            val text = "$dateText\n$heading\n\n$body".trimEnd()
                            shareText(context, text, shareChooserTitle)
                        },
                        enabled = canSave
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = shareChooserTitle, tint = NotebookColors.ink)
                    }
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
                            if (canSave) {
                                val cleanTitle = JournalEntry.cleanTitle(title)
                                // copy() keeps sortIndex, so editing a page no longer undoes
                                // its manual position in the list.
                                viewModel.save(
                                    existing?.copy(title = cleanTitle, body = body.trim())
                                        ?: JournalEntry(
                                            createdAt = newEntryCreatedAt,
                                            title = cleanTitle,
                                            body = body.trim()
                                        )
                                )
                                onDone()
                            }
                        },
                        enabled = canSave
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
                .consumeWindowInsets(padding)
                // Ends the scroll area at the keyboard, so "in view" below means above the keyboard.
                .imePadding()
                .verticalScroll(rememberScrollState())
                .notebookMargin(marginInset = 32.dp)
                .padding(start = 40.dp, top = 12.dp, end = 20.dp, bottom = 40.dp)
        ) {
            // The page's own title, edited in place; untitled pages show a faded placeholder.
            val titleStyle = MaterialTheme.typography.titleMedium.copy(
                fontFamily = NotebookFont,
                fontWeight = FontWeight.Bold,
                color = NotebookColors.ink
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                if (title.isEmpty()) {
                    Text(
                        text = stringResource(R.string.journal_page_title_placeholder),
                        style = titleStyle,
                        fontStyle = FontStyle.Italic,
                        color = NotebookColors.inkFaded
                    )
                }
                BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    textStyle = titleStyle,
                    singleLine = true,
                    cursorBrush = SolidColor(NotebookColors.ink),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    modifier = Modifier.fillMaxWidth()
                )
            }
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
                val bodyStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = NotebookFont,
                    fontSize = 17.sp,
                    lineHeight = 32.sp,
                    color = NotebookColors.ink
                )
                BasicTextField(
                    value = bodyField,
                    onValueChange = { bodyField = it },
                    textStyle = bodyStyle,
                    cursorBrush = SolidColor(NotebookColors.ink),
                    modifier = Modifier
                        .fillMaxWidth()
                        .keepCursorInView(bodyField, bodyStyle)
                )
            }
        }
    }
}
