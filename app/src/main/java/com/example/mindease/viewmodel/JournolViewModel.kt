package com.example.mindease.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindease.data.JournalEntry
import com.example.mindease.repository.JournalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class JournalViewModel(private val repository: JournalRepository) : ViewModel() {

    private val _entries = MutableStateFlow<List<JournalEntry>>(emptyList())
    val entries: StateFlow<List<JournalEntry>> = _entries

    init {
        viewModelScope.launch {
            repository.getJournalEntries().collect { entries ->
                _entries.value = entries
            }
        }
    }

    fun saveEntry(text: String) {
        viewModelScope.launch {
            repository.saveJournalEntry(text)
        }
    }

    fun deleteEntry(entry: JournalEntry) {
        viewModelScope.launch {
            // Perform deletion from the repository
            repository.deleteEntry(entry)

            // After deletion, refresh the entries list to reflect the change
            _entries.value = repository.getJournalEntries().first() // Assuming this fetches the latest list
        }
    }
}
