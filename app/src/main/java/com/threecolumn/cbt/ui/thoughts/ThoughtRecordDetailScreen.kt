package com.threecolumn.cbt.ui.thoughts

import android.text.format.DateUtils
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import java.util.Locale

/** Below this width, two side-by-side columns get too narrow to read; use two pages instead. */
private const val WideScreenMinWidthDp = 600

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ThoughtRecordDetailScreen(
    recordId: Long,
    viewModel: ThoughtRecordViewModel,
    onBack: () -> Unit,
    onEdit: (Long, Int) -> Unit
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
    var beliefExpanded by rememberSaveable { mutableStateOf(false) }
    var currentPage by rememberSaveable(recordId) { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        record?.let {
                            DateUtils.formatDateTime(
                                context, it.createdAt,
                                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR or
                                    DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_ABBREV_WEEKDAY
                            )
                        }.orEmpty(),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc))
                    }
                },
                actions = {
                    IconButton(onClick = { beliefExpanded = !beliefExpanded }) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = stringResource(
                                if (beliefExpanded) R.string.summary_hide else R.string.summary_show
                            )
                        )
                    }
                    IconButton(onClick = {
                        record?.let { rec ->
                            val distortionLabels = rec.distortionKeys
                                .mapNotNull { CognitiveDistortion.fromStorageKey(it) }
                                .mapNotNull { distortionLabelByEntry[it] }
                            val lines = buildList {
                                if (rec.situation.isNotBlank()) {
                                    add("$situationLabel: ${rec.situation}")
                                    add("")
                                }
                                add(automaticThoughtLabel)
                                add(rec.automaticThought)
                                if (rec.beliefBefore >= 0) add(String.format(Locale.getDefault(), beliefBeforePattern, rec.beliefBefore))
                                add("")
                                add(distortionsLabel)
                                add(if (distortionLabels.isEmpty()) noneSelectedLabel else distortionLabels.joinToString(" · "))
                                add("")
                                add(rationalResponseLabel)
                                add(rec.rationalResponse)
                                if (rec.beliefAfter >= 0) add(String.format(Locale.getDefault(), beliefAfterPattern, rec.beliefAfter))
                            }
                            shareText(context, lines.joinToString("\n"), shareChooserTitle)
                        }
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = shareChooserTitle)
                    }
                    IconButton(onClick = { onEdit(recordId, currentPage) }) {
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
                if (beliefExpanded) {
                    BeliefCard(current)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        ThoughtPage(current)
                    }
                    ColumnDivider()
                    Column(modifier = Modifier.weight(1f)) {
                        ResponsePage(current)
                    }
                }
            }
        } else {
            // Two swipeable/tappable pages instead of side-by-side columns: a phone is too
            // narrow for two columns of full sentences to stay readable.
            val pagerState = rememberPagerState(
                initialPage = currentPage.coerceIn(0, 1),
                pageCount = { 2 }
            )
            val scope = rememberCoroutineScope()
            LaunchedEffect(pagerState.currentPage) {
                currentPage = pagerState.currentPage
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (beliefExpanded) {
                    BeliefCard(current, modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp))
                }
                PageTabRow(
                    pageCount = 2,
                    labels = listOf(
                        stringResource(R.string.tab_automatic_thought),
                        stringResource(R.string.tab_distortions_response)
                    ),
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
                        if (page == 0) ThoughtPage(current) else ResponsePage(current)
                    }
                }
            }
        }
    }
}

/** What happened (if noted), then the thought. */
@Composable
private fun ThoughtPage(current: ThoughtRecord) {
    if (current.situation.isNotBlank()) {
        Text(
            text = current.situation,
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 10.dp)
        )
    }
    Text(text = current.automaticThought, style = MaterialTheme.typography.bodyLarge)
}

/** The distortion(s) named, then the answer. */
@Composable
private fun ResponsePage(current: ThoughtRecord) {
    DistortionsList(current)
    Spacer(Modifier.height(16.dp))
    Text(text = current.rationalResponse, style = MaterialTheme.typography.bodyLarge)
}

/** The optional before/after belief ratings, shown only when toggled on via the info icon. */
@Composable
private fun BeliefCard(current: ThoughtRecord, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(R.string.belief_card_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (current.hasBelief) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = stringResource(R.string.belief_before_display, current.beliefBefore),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(R.string.belief_after_display, current.beliefAfter),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.belief_not_set),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DistortionsList(current: ThoughtRecord) {
    val distortionLabels = current.distortionKeys
        .mapNotNull { CognitiveDistortion.fromStorageKey(it) }
        .map { stringResource(it.labelRes) }
    Text(
        text = if (distortionLabels.isEmpty()) stringResource(R.string.distortions_none_selected)
        else distortionLabels.joinToString(" · "),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
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
