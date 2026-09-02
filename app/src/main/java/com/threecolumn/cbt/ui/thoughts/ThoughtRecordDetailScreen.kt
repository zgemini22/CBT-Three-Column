package com.threecolumn.cbt.ui.thoughts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.threecolumn.cbt.R
import com.threecolumn.cbt.data.CognitiveDistortion
import com.threecolumn.cbt.data.ThoughtRecord
import com.threecolumn.cbt.ui.components.PageTabRow
import com.threecolumn.cbt.util.shareText
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import java.util.Locale

/** Below this width, three side-by-side columns get too narrow to read; stack instead. */
private const val WideScreenMinWidthDp = 600

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ThoughtRecordDetailScreen(
    recordId: Long,
    viewModel: ThoughtRecordViewModel,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val record by remember(recordId) { viewModel.observeById(recordId) }.collectAsState(initial = null)
    val context = LocalContext.current

    val situationLabel = stringResource(R.string.situation_display_label)
    val automaticThoughtLabel = stringResource(R.string.section_automatic_thought)
    val beliefBeforePattern = stringResource(R.string.belief_before_display)
    val distortionsLabel = stringResource(R.string.section_distortions)
    val noneSelectedLabel = stringResource(R.string.distortions_none_selected)
    val rationalResponseLabel = stringResource(R.string.section_rational_response)
    val beliefAfterPattern = stringResource(R.string.belief_after_display)
    val shareChooserTitle = stringResource(R.string.share_desc)
    val distortionLabelByEntry = CognitiveDistortion.entries.associateWith { stringResource(it.labelRes) }

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
                        record?.let { rec ->
                            val distortionLabels = rec.distortionKeys
                                .mapNotNull { CognitiveDistortion.fromStorageKey(it) }
                                .mapNotNull { distortionLabelByEntry[it] }
                            val text = buildString {
                                if (rec.situation.isNotBlank()) {
                                    appendLine("$situationLabel: ${rec.situation}")
                                    appendLine()
                                }
                                appendLine("1. $automaticThoughtLabel")
                                appendLine(rec.automaticThought)
                                appendLine(String.format(Locale.getDefault(), beliefBeforePattern, rec.beliefBefore))
                                appendLine()
                                appendLine("2. $distortionsLabel")
                                appendLine(if (distortionLabels.isEmpty()) noneSelectedLabel else distortionLabels.joinToString(" · "))
                                appendLine()
                                appendLine("3. $rationalResponseLabel")
                                appendLine(rec.rationalResponse)
                                append(String.format(Locale.getDefault(), beliefAfterPattern, rec.beliefAfter))
                            }
                            shareText(context, text, shareChooserTitle)
                        }
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = shareChooserTitle)
                    }
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
        val isWideScreen = LocalConfiguration.current.screenWidthDp >= WideScreenMinWidthDp

        if (isWideScreen) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                SummaryCard(current)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = current.automaticThought, style = MaterialTheme.typography.bodyLarge)
                    }
                    ColumnDivider()
                    Column(modifier = Modifier.weight(1f)) {
                        DistortionsList(current)
                    }
                    ColumnDivider()
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = current.rationalResponse, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        } else {
            // Three swipeable/tappable pages instead of side-by-side columns: a phone is too
            // narrow for three columns of full sentences to stay readable.
            val pagerState = rememberPagerState(pageCount = { 3 })
            val scope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                SummaryCard(current, modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp))
                PageTabRow(
                    pageCount = 3,
                    currentPage = pagerState.currentPage,
                    onPageSelected = { page -> scope.launch { pagerState.animateScrollToPage(page) } },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        when (page) {
                            0 -> Text(text = current.automaticThought, style = MaterialTheme.typography.bodyLarge)
                            1 -> DistortionsList(current)
                            else -> Text(text = current.rationalResponse, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Situation, the three section titles, and the before/after belief once, in one place.
 * Collapsed by default so it doesn't push the record's actual content down the screen.
 */
@Composable
private fun SummaryCard(current: ThoughtRecord, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    if (!expanded) {
        // Collapsed: just a small info icon in the top-right, no card behind it.
        SummaryToggle(
            descRes = R.string.summary_show,
            icon = Icons.Outlined.Info,
            onClick = { expanded = true },
            modifier = modifier
        )
        return
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        SummaryToggle(
            descRes = R.string.summary_hide,
            icon = Icons.Outlined.Info,
            onClick = { expanded = false },
            modifier = Modifier.padding(top = 4.dp, end = 4.dp)
        )
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (current.situation.isNotBlank()) {
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
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            Text(
                text = "1. ${stringResource(R.string.section_automatic_thought)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "2. ${stringResource(R.string.section_distortions)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "3. ${stringResource(R.string.section_rational_response)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = stringResource(R.string.belief_before_display, current.beliefBefore),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.belief_after_display, current.beliefAfter),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** A deliberately quiet expand/collapse affordance: a small info icon in the top-right. */
@Composable
private fun SummaryToggle(
    descRes: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(descRes),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun DistortionsList(current: ThoughtRecord) {
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

/** A thin vertical rule between side-by-side columns. */
@Composable
private fun ColumnDivider() {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}
