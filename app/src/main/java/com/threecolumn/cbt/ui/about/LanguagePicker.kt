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
import androidx.core.os.LocaleListCompat
import com.threecolumn.cbt.R

private enum class AppLanguage(val tag: String?) {
    SYSTEM_DEFAULT(null),
    ENGLISH("en"),
    CHINESE("zh")
}

private fun currentAppLanguage(): AppLanguage {
    val locales = AppCompatDelegate.getApplicationLocales()
    if (locales.isEmpty) return AppLanguage.SYSTEM_DEFAULT
    return when (locales.get(0)?.language) {
        "zh" -> AppLanguage.CHINESE
        "en" -> AppLanguage.ENGLISH
        else -> AppLanguage.SYSTEM_DEFAULT
    }
}

/** Lets the user override the app's display language, independent of the device's system language. */
@Composable
fun LanguagePicker() {
    var selected by remember { mutableStateOf(currentAppLanguage()) }

    fun select(language: AppLanguage) {
        selected = language
        val locales = language.tag?.let { LocaleListCompat.forLanguageTags(it) } ?: LocaleListCompat.getEmptyLocaleList()
        AppCompatDelegate.setApplicationLocales(locales)
    }

    Column {
        Text(
            text = stringResource(R.string.language_section_title),
            style = MaterialTheme.typography.titleMedium
        )
        SelectableOptionRow(
            label = stringResource(R.string.language_system_default),
            selected = selected == AppLanguage.SYSTEM_DEFAULT,
            onClick = { select(AppLanguage.SYSTEM_DEFAULT) }
        )
        SelectableOptionRow(
            label = stringResource(R.string.language_english),
            selected = selected == AppLanguage.ENGLISH,
            onClick = { select(AppLanguage.ENGLISH) }
        )
        SelectableOptionRow(
            label = stringResource(R.string.language_chinese),
            selected = selected == AppLanguage.CHINESE,
            onClick = { select(AppLanguage.CHINESE) }
        )
    }
}
