package com.threecolumn.cbt.ui.journal

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.threecolumn.cbt.R
import com.threecolumn.cbt.data.JournalEntry
import com.threecolumn.cbt.data.JournalEntryRepository
import com.threecolumn.cbt.data.JournalTitleStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JournalViewModel(
    private val repository: JournalEntryRepository,
    private val titleStore: JournalTitleStore
) : ViewModel() {

    val entries: StateFlow<List<JournalEntry>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Null until the user renames the journal; see [journalTitle]. */
    val customTitle: StateFlow<String?> = titleStore.customTitle

    fun setTitle(title: String, defaultTitle: String) = titleStore.setTitle(title, defaultTitle)

    suspend fun getById(id: Long): JournalEntry? = repository.getById(id)

    fun save(entry: JournalEntry) {
        viewModelScope.launch { repository.save(entry) }
    }

    fun delete(entry: JournalEntry) {
        viewModelScope.launch { repository.delete(entry) }
    }

    /** Persists a drag-reordered list by assigning each entry a fresh, position-based sortIndex. */
    fun persistOrder(orderedEntries: List<JournalEntry>) {
        if (orderedEntries.isEmpty()) return
        val base = orderedEntries.size.toLong()
        val reindexed = orderedEntries.mapIndexed { index, entry ->
            entry.copy(sortIndex = base - index)
        }
        viewModelScope.launch { repository.saveAll(reindexed) }
    }

    class Factory(
        private val repository: JournalEntryRepository,
        private val titleStore: JournalTitleStore
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            JournalViewModel(repository, titleStore) as T
    }
}

/** The title to show: the user's own, or the built-in topic in the current language. */
@Composable
fun journalTitle(customTitle: String?): String = customTitle ?: stringResource(R.string.journal_topic)
