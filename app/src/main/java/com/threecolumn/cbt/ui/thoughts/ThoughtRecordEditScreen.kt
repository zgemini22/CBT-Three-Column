package com.threecolumn.cbt.ui.thoughts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.threecolumn.cbt.data.CognitiveDistortion
import com.threecolumn.cbt.data.ThoughtRecord
import com.threecolumn.cbt.ui.theme.ruledPaper

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
                title = { Text(if (recordId == null) "New thought record" else "Edit thought record") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (existing != null) {
                        IconButton(onClick = {
                            existing?.let { viewModel.delete(it) }
                            onDone()
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete")
                        }
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
                                        beliefBefore = beliefBefore.toInt(),
                                        beliefAfter = beliefAfter.toInt()
                                    )
                                )
                                onDone()
                            }
                        },
                        enabled = automaticThought.isNotBlank() && rationalResponse.isNotBlank()
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        if (!loaded) return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .ruledPaper(lineSpacing = 40.dp, topInset = 12.dp, marginInset = 20.dp, drawMargin = false)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OutlinedTextField(
                value = situation,
                onValueChange = { situation = it },
                label = { Text("Situation (optional)") },
                placeholder = { Text("What was happening when the thought showed up?") },
                modifier = Modifier.fillMaxWidth()
            )

            SectionHeader(number = "1", title = "Automatic Thought")
            OutlinedTextField(
                value = automaticThought,
                onValueChange = { automaticThought = it },
                placeholder = { Text("The upsetting thought that popped into your mind") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            BeliefSlider(
                label = "How much did you believe it?",
                value = beliefBefore,
                onValueChange = { beliefBefore = it }
            )

            SectionHeader(number = "2", title = "Cognitive Distortion(s)")
            Text(
                text = "Tap any that apply — descriptions appear below once selected.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CognitiveDistortion.entries.forEach { distortion ->
                    val selected = distortion in selectedDistortions
                    FilterChip(
                        selected = selected,
                        onClick = {
                            selectedDistortions = if (selected) {
                                selectedDistortions - distortion
                            } else {
                                selectedDistortions + distortion
                            }
                        },
                        label = { Text(distortion.label) },
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
            selectedDistortions.forEach { distortion ->
                Text(
                    text = "${distortion.label}: ${distortion.description}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp)
                )
            }

            SectionHeader(number = "3", title = "Rational Response")
            OutlinedTextField(
                value = rationalResponse,
                onValueChange = { rationalResponse = it },
                placeholder = { Text("Talk back to the distorted thought with a fairer, evidence-based response") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            BeliefSlider(
                label = "How much do you believe the automatic thought now?",
                value = beliefAfter,
                onValueChange = { beliefAfter = it }
            )
        }
    }
}

@Composable
private fun SectionHeader(number: String, title: String) {
    Text(
        text = "$number. $title",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
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
