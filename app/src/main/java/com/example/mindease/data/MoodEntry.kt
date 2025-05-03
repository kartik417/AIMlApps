package com.example.mindease.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mood: Float, // Mood rating from 0 to 10
    val note: String?, // Optional note
    val date: Long = System.currentTimeMillis() // Timestamp for the entry
)




