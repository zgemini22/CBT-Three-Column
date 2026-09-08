package com.threecolumn.cbt.ui.thoughts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.threecolumn.cbt.data.ThoughtRecord
import com.threecolumn.cbt.data.ThoughtRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThoughtRecordViewModel(private val repository: ThoughtRecordRepository) : ViewModel() {

    val records: StateFlow<List<ThoughtRecord>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    suspend fun getById(id: Long): ThoughtRecord? = repository.getById(id)
    fun observeById(id: Long): Flow<ThoughtRecord?> = repository.observeById(id)

    fun save(record: ThoughtRecord) {
        viewModelScope.launch { repository.save(record) }
    }

    fun delete(record: ThoughtRecord) {
        viewModelScope.launch { repository.delete(record) }
    }

    fun delete(records: List<ThoughtRecord>) {
        viewModelScope.launch {
            records.forEach { repository.delete(it) }
        }
    }

    class Factory(private val repository: ThoughtRecordRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ThoughtRecordViewModel(repository) as T
    }
}
