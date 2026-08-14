package com.threecolumn.cbt.ui.hobbies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.threecolumn.cbt.data.HobbyIdea
import com.threecolumn.cbt.data.HobbyIdeaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HobbyIdeaViewModel(private val repository: HobbyIdeaRepository) : ViewModel() {

    val ideas: StateFlow<List<HobbyIdea>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun add(title: String, notes: String = "") {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.save(HobbyIdea(title = title.trim(), notes = notes.trim(), addedAt = System.currentTimeMillis()))
        }
    }

    fun toggleTried(idea: HobbyIdea) {
        viewModelScope.launch { repository.save(idea.copy(tried = !idea.tried)) }
    }

    fun delete(idea: HobbyIdea) {
        viewModelScope.launch { repository.delete(idea) }
    }

    class Factory(private val repository: HobbyIdeaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HobbyIdeaViewModel(repository) as T
    }
}
