package com.threecolumn.cbt.ui.thoughts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.threecolumn.cbt.R
import com.threecolumn.cbt.data.CognitiveDistortion
import com.threecolumn.cbt.data.ThoughtRecord
import com.threecolumn.cbt.ui.components.PageTabRow
import kotlinx.coroutines.launch

/** Below this width, three side-by-side columns get too narrow to read; stack instead. */
private const val WideScreenMinWidthDp = 600

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun ThoughtRecordEditScreen(
    recordId: Long?,
    viewModel: ThoughtRecordViewModel,
    onDone: () -> Unit
) {
    var existing by remember { mutableStateOf<ThoughtRecord?>(null) }
    var loaded by remember { mutableStateOf(recordId == null) }

    var situation by remember { mutableStateOf("") }
    var automaticThought by remember { mutableStateOf("") }
    var rationalResponse by remember { mutableStateOf("") }
    var beliefBefore by remember { mutableStateOf(70f) }
    var beliefAfter by remember { mutableStateOf(30f) }
    var selectedDistortions by remember { mutableStateOf(setOf<CognitiveDistortion>()) }

    LaunchedEffect(recordId) {
        if (recordId != null) {
            val record = viewModel.getById(recordId)
            existing = record
            if (record != null) {
                situation = record.situation
                automaticThought = record.automaticThought
                rationalResponse = record.rationalResponse
                beliefBefore = record.beliefBefore.toFloat()
                beliefAfter = record.beliefAfter.toFloat()
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
                                        beliefBefore = beliefBefore.toInt(),
                                        beliefAfter = beliefAfter.toInt()
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

        if (isWideScreen) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                SummaryCard(
                    situation = situation,
                    onSituationChange = { situation = it },
                    beliefBefore = beliefBefore,
                    onBeliefBeforeChange = { beliefBefore = it },
                    beliefAfter = beliefAfter,
                    onBeliefAfterChange = { beliefAfter = it }
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    AutomaticThoughtColumn(
                        automaticThought = automaticThought,
                        onAutomaticThoughtChange = { automaticThought = it },
                        modifier = Modifier.weight(1f)
                    )
                    ColumnDivider()
                    DistortionsColumn(
                        selectedDistortions = selectedDistortions,
                        onToggle = { distortion, selected ->
                            selectedDistortions = if (selected) {
                                selectedDistortions - distortion
                            } else {
                                selectedDistortions + distortion
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    ColumnDivider()
                    RationalResponseColumn(
                        rationalResponse = rationalResponse,
                        onRationalResponseChange = { rationalResponse = it },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            // Three swipeable/tappable pages instead of side-by-side columns: a phone is too
            // narrow for three columns of full sentences to stay usable for editing.
            val pagerState = rememberPagerState(pageCount = { 3 })
            val scope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                SummaryCard(
                    situation = situation,
                    onSituationChange = { situation = it },
                    beliefBefore = beliefBefore,
                    onBeliefBeforeChange = { beliefBefore = it },
                    beliefAfter = beliefAfter,
                    onBeliefAfterChange = { beliefAfter = it },
                    modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp)
                )
                PageTabRow(
                    pageCount = 3,
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
                        when (page) {
                            0 -> AutomaticThoughtColumn(
                                automaticThought = automaticThought,
                                onAutomaticThoughtChange = { automaticThought = it }
                            )
                            1 -> DistortionsColumn(
                                selectedDistortions = selectedDistortions,
                                onToggle = { distortion, selected ->
                                    selectedDistortions = if (selected) {
                                        selectedDistortions - distortion
                                    } else {
                                        selectedDistortions + distortion
                                    }
                                }
                            )
                            else -> RationalResponseColumn(
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

@Composable
private fun AutomaticThoughtColumn(
    automaticThought: String,
    onAutomaticThoughtChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = automaticThought,
            onValueChange = onAutomaticThoughtChange,
            minLines = 2,
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
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Situation, the three section titles, and the before/after belief sliders once, in one place.
 * Collapsed by default so the thought/response fields stay at the top of the screen.
 */
@Composable
private fun SummaryCard(
    situation: String,
    onSituationChange: (String) -> Unit,
    beliefBefore: Float,
    onBeliefBeforeChange: (Float) -> Unit,
    beliefAfter: Float,
    onBeliefAfterChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    if (!expanded) {
        // Collapsed: a quiet inline link, no card behind it.
        SummaryToggle(
            textRes = R.string.summary_show,
            icon = Icons.Filled.ExpandMore,
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
            textRes = R.string.summary_hide,
            icon = Icons.Filled.ExpandLess,
            onClick = { expanded = false },
            modifier = Modifier.padding(start = 16.dp, top = 12.dp)
        )
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = situation,
                onValueChange = onSituationChange,
                label = { Text(stringResource(R.string.situation_label)) },
                placeholder = { Text(stringResource(R.string.situation_placeholder)) },
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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

/** A deliberately quiet expand/collapse affordance: small, muted, and only as wide as its text. */
@Composable
private fun SummaryToggle(
    textRes: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
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
private fun BeliefSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Text(
            text = "$label ${value.toInt()}%",
            style = MaterialTheme.typography.bodyMedium
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..100f,
            steps = 19
        )
    }
}
