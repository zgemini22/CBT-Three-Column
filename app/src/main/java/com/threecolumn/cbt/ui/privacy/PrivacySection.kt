package com.threecolumn.cbt.ui.privacy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.threecolumn.cbt.R

/** Lets the user require biometric/device-credential unlock before the app shows any content. */
@Composable
fun PrivacySection(
    appLockEnabled: Boolean,
    biometricAvailable: Boolean,
    onToggleAppLock: (Boolean) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.privacy_section_title),
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.privacy_lock_label),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(
                        if (biometricAvailable) R.string.privacy_lock_description else R.string.privacy_lock_unavailable
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = appLockEnabled && biometricAvailable,
                onCheckedChange = onToggleAppLock,
                enabled = biometricAvailable
            )
        }
    }
}
