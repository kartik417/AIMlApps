package com.example.mindease.repository

import com.example.mindease.data.MoodDao
import com.example.mindease.data.MoodEntry

class MoodRepository(private val moodDao: MoodDao) {

    suspend fun saveMood(mood: Float, note: String) {
        val moodEntry = MoodEntry(mood = mood, note = note)
        moodDao.insertMood(moodEntry)
    }

    suspend fun getRecentMoods(): List<MoodEntry> {
        return moodDao.getRecentMoods()
    }
    suspend fun deleteMood(moodEntry: MoodEntry) {
        moodDao.delete(moodEntry)
    }
}
