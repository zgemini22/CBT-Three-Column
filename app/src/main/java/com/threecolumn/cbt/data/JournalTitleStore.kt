package com.threecolumn.cbt.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The journal's title, if the user has renamed it. Null means "use R.string.journal_topic",
 * which keeps following the app language; a stored string would freeze it in one language.
 */
class JournalTitleStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _customTitle = MutableStateFlow(prefs.getString(KEY_TITLE, null))
    val customTitle: StateFlow<String?> = _customTitle.asStateFlow()

    /** Blank, or the built-in title itself, clears the override. */
    fun setTitle(title: String?, defaultTitle: String) {
        val normalized = normalize(title, defaultTitle)
        prefs.edit().apply {
            if (normalized == null) remove(KEY_TITLE) else putString(KEY_TITLE, normalized)
        }.apply()
        _customTitle.value = normalized
    }

    companion object {
        private const val PREFS_NAME = "journal_prefs"
        private const val KEY_TITLE = "title"

        /**
         * The title to store, or null to fall back to the built-in one. Line breaks (from pasting)
         * become spaces, since a title is shown on one line of a header.
         */
        fun normalize(title: String?, defaultTitle: String): String? =
            title?.lineSequence()
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?.joinToString(" ")
                ?.takeIf { it.isNotEmpty() && it != defaultTitle }
    }
}
