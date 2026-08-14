package com.threecolumn.cbt.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.threecolumn.cbt.data.JournalEntry
import com.threecolumn.cbt.data.JournalEntryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JournalViewModel(private val repository: JournalEntryRepository) : ViewModel() {

    val entries: StateFlow<List<JournalEntry>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private var pendingPrompt: String = ""

    fun setPendingPrompt(prompt: String) {
        pendingPrompt = prompt
    }

    /** Reads and clears the prompt queued by the journal list's "new entry" action. */
    fun consumePendingPrompt(): String {
        val prompt = pendingPrompt
        pendingPrompt = ""
        return prompt
    }

    suspend fun getById(id: Long): JournalEntry? = repository.getById(id)

    fun save(entry: JournalEntry) {
        viewModelScope.launch { repository.save(entry) }
    }

    fun delete(entry: JournalEntry) {
        viewModelScope.launch { repository.delete(entry) }
    }

    class Factory(private val repository: JournalEntryRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            JournalViewModel(repository) as T
    }
}
