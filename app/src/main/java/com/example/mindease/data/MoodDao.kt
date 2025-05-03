package com.example.mindease.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MoodDao {
    @Insert
    suspend fun insertMood(moodEntry: MoodEntry)
    @Delete
    suspend fun delete(moodEntry: MoodEntry)
    @Query("SELECT * FROM mood_entries ORDER BY date DESC LIMIT 7")
    suspend fun getRecentMoods(): List<MoodEntry>
}
