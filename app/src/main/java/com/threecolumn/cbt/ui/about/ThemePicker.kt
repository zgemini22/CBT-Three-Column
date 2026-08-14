package com.threecolumn.cbt.ui.about

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.threecolumn.cbt.R

private enum class AppTheme(val nightMode: Int) {
    SYSTEM_DEFAULT(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
    LIGHT(AppCompatDelegate.MODE_NIGHT_NO),
    DARK(AppCompatDelegate.MODE_NIGHT_YES)
}

private fun currentAppTheme(): AppTheme = when (AppCompatDelegate.getDefaultNightMode()) {
    AppCompatDelegate.MODE_NIGHT_NO -> AppTheme.LIGHT
    AppCompatDelegate.MODE_NIGHT_YES -> AppTheme.DARK
    else -> AppTheme.SYSTEM_DEFAULT
}

/** Lets the user pin the app's light/dark appearance, or leave it following the system setting. */
@Composable
fun ThemePicker() {
    var selected by remember { mutableStateOf(currentAppTheme()) }

    fun select(theme: AppTheme) {
        selected = theme
        AppCompatDelegate.setDefaultNightMode(theme.nightMode)
    }

    Column {
        Text(
            text = stringResource(R.string.theme_section_title),
            style = MaterialTheme.typography.titleMedium
        )
        SelectableOptionRow(
            label = stringResource(R.string.theme_system_default),
            selected = selected == AppTheme.SYSTEM_DEFAULT,
            onClick = { select(AppTheme.SYSTEM_DEFAULT) }
        )
        SelectableOptionRow(
            label = stringResource(R.string.theme_light),
            selected = selected == AppTheme.LIGHT,
            onClick = { select(AppTheme.LIGHT) }
        )
        SelectableOptionRow(
            label = stringResource(R.string.theme_dark),
            selected = selected == AppTheme.DARK,
            onClick = { select(AppTheme.DARK) }
        )
    }
}
