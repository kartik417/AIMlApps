package com.example.mindease.repository

import com.example.mindease.data.JournalDao
import com.example.mindease.data.JournalEntry
import kotlinx.coroutines.flow.Flow

class JournalRepository(private val dao: JournalDao) {
    suspend fun saveJournalEntry(content: String) {
        dao.insert(JournalEntry(content = content))
    }

    fun getJournalEntries(): Flow<List<JournalEntry>> {
        return dao.getAllEntries()
    }
    suspend fun deleteEntry(entry: JournalEntry) {
        dao.delete(entry)
    }

}
