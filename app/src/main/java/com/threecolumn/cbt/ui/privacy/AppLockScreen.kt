package com.threecolumn.cbt.ui.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.threecolumn.cbt.R
import com.threecolumn.cbt.ui.theme.NotebookColors

/**
 * Shown instead of the app's content whenever it's locked. Requests unlock as soon as it
 * appears, and offers a button to retry in case the system prompt was dismissed or canceled.
 */
@Composable
fun AppLockScreen(onUnlockRequested: () -> Unit) {
    LaunchedEffect(Unit) { onUnlockRequested() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NotebookColors.paper),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                tint = NotebookColors.inkFaded,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = stringResource(R.string.lock_screen_title),
                style = MaterialTheme.typography.titleMedium,
                color = NotebookColors.ink,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = stringResource(R.string.lock_screen_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = NotebookColors.inkFaded,
                modifier = Modifier.padding(top = 4.dp)
            )
            TextButton(onClick = onUnlockRequested, modifier = Modifier.padding(top = 20.dp)) {
                Text(stringResource(R.string.lock_screen_unlock_button))
            }
        }
    }
}
