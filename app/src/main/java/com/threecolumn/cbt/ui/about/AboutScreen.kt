package com.threecolumn.cbt.ui.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About this app") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("The Three-Column Technique", style = MaterialTheme.typography.titleMedium)
            Text(
                "This method, popularized by psychiatrist David Burns in \"Feeling Good: " +
                    "The New Mood Therapy,\" is a simple way to talk back to upsetting " +
                    "thoughts. Write down the automatic thought as it occurred to you, " +
                    "identify which distortion(s) are twisting your thinking, then write " +
                    "a rational response that answers the thought fairly. Rating how much " +
                    "you believe the thought before and after helps you see the shift.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text("Building your own idea list", style = MaterialTheme.typography.titleMedium)
            Text(
                "A separate chapter of the book tackles the need for other people's " +
                    "approval — the belief that an activity is only worth doing if someone " +
                    "else signs off on it. One antidote is keeping a running list of " +
                    "hobbies and activities you'd like to try simply because they interest " +
                    "you. Whenever a new idea occurs to you, add it to the list on the " +
                    "Hobbies tab — no one else's approval required.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "This app is an independent, unofficial companion tool for practicing " +
                    "these techniques and is not affiliated with or endorsed by the book's " +
                    "author or publisher. It is not a substitute for professional care.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
