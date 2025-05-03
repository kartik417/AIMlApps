package com.example.mindease.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mindease.data.MoodDatabase
import com.example.mindease.data.MoodEntry
import com.example.mindease.repository.MoodRepository
import kotlinx.coroutines.launch

class MoodViewModel(application: Application) : AndroidViewModel(application) {
    private val moodRepository = MoodRepository(MoodDatabase.getDatabase(application).moodDao())

    private val _moods = MutableLiveData<List<MoodEntry>>()
    val moods: LiveData<List<MoodEntry>> get() = _moods

    private val _moodSaved = MutableLiveData<Boolean>()
    val moodSaved: LiveData<Boolean> get() = _moodSaved

    fun saveMood(mood: Float, note: String) {
        viewModelScope.launch {
            moodRepository.saveMood(mood, note)
            _moodSaved.value = true // Trigger save event
            loadRecentMoods()
        }
    }

    fun deleteMood(moodEntry: MoodEntry) {
        viewModelScope.launch {
            moodRepository.deleteMood(moodEntry)
            loadRecentMoods() // Refresh the list after deletion
        }
    }
    fun resetMoodSavedFlag() {
        _moodSaved.value = false // Reset the event
    }

    private fun loadRecentMoods() {
        viewModelScope.launch {
            _moods.value = moodRepository.getRecentMoods()
        }
    }
}
