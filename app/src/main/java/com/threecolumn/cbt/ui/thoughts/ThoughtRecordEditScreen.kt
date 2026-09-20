package com.threecolumn.cbt.ui.thoughts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.threecolumn.cbt.R
import com.threecolumn.cbt.data.CognitiveDistortion
import com.threecolumn.cbt.data.ThoughtRecord
import com.threecolumn.cbt.ui.components.PageTabRow
import kotlinx.coroutines.launch

/** Below this width, two side-by-side columns get too narrow to read; use two pages instead. */
private const val WideScreenMinWidthDp = 600

/**
 * Two halves, the way Burns fills the table in: the automatic thought on the left, and on the
 * right the distortion(s) it contains followed by the rational response that answers it.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun ThoughtRecordEditScreen(
    recordId: Long?,
    viewModel: ThoughtRecordViewModel,
    onDone: () -> Unit,
    initialPage: Int = 0
) {
    var existing by remember { mutableStateOf<ThoughtRecord?>(null) }
    var loaded by remember { mutableStateOf(recordId == null) }

    var situation by remember { mutableStateOf("") }
    var automaticThought by remember { mutableStateOf("") }
    var rationalResponse by remember { mutableStateOf("") }
    // Belief ratings are optional: null until the user touches a slider.
    var beliefBefore by remember { mutableStateOf<Int?>(null) }
    var beliefAfter by remember { mutableStateOf<Int?>(null) }
    var selectedDistortions by remember { mutableStateOf(setOf<CognitiveDistortion>()) }
    var beliefExpanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(recordId) {
        if (recordId != null) {
            val record = viewModel.getById(recordId)
            existing = record
            if (record != null) {
                situation = record.situation
                automaticThought = record.automaticThought
                rationalResponse = record.rationalResponse
                beliefBefore = record.beliefBefore.takeIf { it >= 0 }
                beliefAfter = record.beliefAfter.takeIf { it >= 0 }
                selectedDistortions = record.distortionKeys
                    .mapNotNull { CognitiveDistortion.fromStorageKey(it) }
                    .toSet()
            }
            loaded = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (recordId == null) R.string.new_thought_record_title
                            else R.string.edit_thought_record_title
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDone) {
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
                    IconButton(
                        onClick = {
                            if (automaticThought.isNotBlank() && rationalResponse.isNotBlank()) {
                                viewModel.save(
                                    ThoughtRecord(
                                        id = existing?.id ?: 0,
                                        createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                                        situation = situation.trim(),
                                        automaticThought = automaticThought.trim(),
                                        distortionKeys = selectedDistortions.map { it.name },
                                        rationalResponse = rationalResponse.trim(),
                                        beliefBefore = beliefBefore ?: ThoughtRecord.BELIEF_UNSET,
                                        beliefAfter = beliefAfter ?: ThoughtRecord.BELIEF_UNSET
                                    )
                                )
                                onDone()
                            }
                        },
                        enabled = automaticThought.isNotBlank() && rationalResponse.isNotBlank()
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.save_desc))
                    }
                }
            )
        }
    ) { padding ->
        if (!loaded) return@Scaffold
        val isWideScreen = LocalConfiguration.current.screenWidthDp >= WideScreenMinWidthDp
        val onToggle: (CognitiveDistortion, Boolean) -> Unit = { distortion, selected ->
            selectedDistortions = if (selected) selectedDistortions - distortion else selectedDistortions + distortion
        }

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
                    BeliefCard(beliefBefore, { beliefBefore = it }, beliefAfter, { beliefAfter = it })
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    ThoughtColumn(
                        situation = situation,
                        onSituationChange = { situation = it },
                        automaticThought = automaticThought,
                        onAutomaticThoughtChange = { automaticThought = it },
                        modifier = Modifier.weight(1f)
                    )
                    ColumnDivider()
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        DistortionsColumn(selectedDistortions = selectedDistortions, onToggle = onToggle)
                        RationalResponseColumn(
                            rationalResponse = rationalResponse,
                            onRationalResponseChange = { rationalResponse = it }
                        )
                    }
                }
            }
        } else {
            // Two swipeable/tappable pages instead of side-by-side columns: a phone is too
            // narrow for two columns of full sentences to stay usable for editing.
            val pagerState = rememberPagerState(
                initialPage = initialPage.coerceIn(0, 1),
                pageCount = { 2 }
            )
            val scope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (beliefExpanded) {
                    BeliefCard(
                        beliefBefore, { beliefBefore = it }, beliefAfter, { beliefAfter = it },
                        modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp)
                    )
                }
                PageTabRow(
                    pageCount = 2,
                    labels = listOf(
                        stringResource(R.string.tab_automatic_thought),
                        stringResource(R.string.tab_distortions_response)
                    ),
                    currentPage = pagerState.currentPage,
                    onPageSelected = { page -> scope.launch { pagerState.animateScrollToPage(page) } },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                        if (page == 0) {
                            ThoughtColumn(
                                situation = situation,
                                onSituationChange = { situation = it },
                                automaticThought = automaticThought,
                                onAutomaticThoughtChange = { automaticThought = it }
                            )
                        } else {
                            DistortionsColumn(selectedDistortions = selectedDistortions, onToggle = onToggle)
                            Spacer(Modifier.height(20.dp))
                            RationalResponseColumn(
                                rationalResponse = rationalResponse,
                                onRationalResponseChange = { rationalResponse = it }
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Page 1: what happened (optional), then the thought it set off. */
@Composable
private fun ThoughtColumn(
    situation: String,
    onSituationChange: (String) -> Unit,
    automaticThought: String,
    onAutomaticThoughtChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = situation,
            onValueChange = onSituationChange,
            label = { Text(stringResource(R.string.situation_label)) },
            placeholder = { Text(stringResource(R.string.situation_placeholder)) },
            maxLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = automaticThought,
            onValueChange = onAutomaticThoughtChange,
            label = { Text(stringResource(R.string.section_automatic_thought)) },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DistortionsColumn(
    selectedDistortions: Set<CognitiveDistortion>,
    onToggle: (CognitiveDistortion, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.distortions_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CognitiveDistortion.entries.forEach { distortion ->
                val selected = distortion in selectedDistortions
                FilterChip(
                    selected = selected,
                    onClick = { onToggle(distortion, selected) },
                    label = { Text(stringResource(distortion.labelRes)) },
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
        selectedDistortions.forEach { distortion ->
            Text(
                text = "${stringResource(distortion.labelRes)}: ${stringResource(distortion.descriptionRes)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun RationalResponseColumn(
    rationalResponse: String,
    onRationalResponseChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = rationalResponse,
            onValueChange = onRationalResponseChange,
            label = { Text(stringResource(R.string.section_rational_response)) },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * The optional before/after belief ratings, shown only when toggled on via the info icon.
 * A slider that has never been touched reads "not set" and stores nothing.
 */
@Composable
private fun BeliefCard(
    beliefBefore: Int?,
    onBeliefBeforeChange: (Int?) -> Unit,
    beliefAfter: Int?,
    onBeliefAfterChange: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.belief_card_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            BeliefSlider(
                label = stringResource(R.string.belief_before_label),
                value = beliefBefore,
                onValueChange = onBeliefBeforeChange
            )
            BeliefSlider(
                label = stringResource(R.string.belief_after_label),
                value = beliefAfter,
                onValueChange = onBeliefAfterChange
            )
        }
    }
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

@Composable
private fun BeliefSlider(label: String, value: Int?, onValueChange: (Int?) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (value == null) "$label · ${stringResource(R.string.belief_not_set)}" else "$label $value%",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            if (value != null) {
                TextButton(onClick = { onValueChange(null) }) {
                    Text(stringResource(R.string.belief_clear))
                }
            }
        }
        Slider(
            value = (value ?: 0).toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..100f,
            steps = 19
        )
    }
}
