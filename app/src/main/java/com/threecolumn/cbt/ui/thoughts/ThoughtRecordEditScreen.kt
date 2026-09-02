package com.threecolumn.cbt.ui.thoughts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
                OutlinedTextField(
                    value = situation,
                    onValueChange = { situation = it },
                    label = { Text(stringResource(R.string.situation_label)) },
                    placeholder = { Text(stringResource(R.string.situation_placeholder)) },
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    AutomaticThoughtColumn(
                        automaticThought = automaticThought,
                        onAutomaticThoughtChange = { automaticThought = it },
                        beliefBefore = beliefBefore,
                        onBeliefBeforeChange = { beliefBefore = it },
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
                        beliefAfter = beliefAfter,
                        onBeliefAfterChange = { beliefAfter = it },
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
                Column(modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp)) {
                    OutlinedTextField(
                        value = situation,
                        onValueChange = { situation = it },
                        label = { Text(stringResource(R.string.situation_label)) },
                        placeholder = { Text(stringResource(R.string.situation_placeholder)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
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
                                onAutomaticThoughtChange = { automaticThought = it },
                                beliefBefore = beliefBefore,
                                onBeliefBeforeChange = { beliefBefore = it }
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
                                onRationalResponseChange = { rationalResponse = it },
                                beliefAfter = beliefAfter,
                                onBeliefAfterChange = { beliefAfter = it }
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
    beliefBefore: Float,
    onBeliefBeforeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(number = "1", title = stringResource(R.string.section_automatic_thought))
        OutlinedTextField(
            value = automaticThought,
            onValueChange = onAutomaticThoughtChange,
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
        BeliefSlider(
            label = stringResource(R.string.belief_before_label),
            value = beliefBefore,
            onValueChange = onBeliefBeforeChange
        )
    }
}

@Composable
private fun DistortionsColumn(
    selectedDistortions: Set<CognitiveDistortion>,
    onToggle: (CognitiveDistortion, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(number = "2", title = stringResource(R.string.section_distortions))
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
    beliefAfter: Float,
    onBeliefAfterChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(number = "3", title = stringResource(R.string.section_rational_response))
        OutlinedTextField(
            value = rationalResponse,
            onValueChange = onRationalResponseChange,
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
        BeliefSlider(
            label = stringResource(R.string.belief_after_label),
            value = beliefAfter,
            onValueChange = onBeliefAfterChange
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
private fun SectionHeader(number: String, title: String) {
    Text(
        text = "$number. $title",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
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
