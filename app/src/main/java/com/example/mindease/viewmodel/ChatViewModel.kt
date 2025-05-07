package com.example.mindease.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindease.GeminiApiService
import com.example.mindease.data.Content
import com.example.mindease.data.GeminiRequest
import com.example.mindease.data.Part
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> get() = _messages

    private val apiKey = "AIzaSyBrWWu8DTlEDiVhEj2W3SOXndD8EVesKjQ"

    private val geminiService: GeminiApiService by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        retrofit.create(GeminiApiService::class.java)
    }

    fun sendMessage(userMessage: String) {
        appendMessage("You: $userMessage")

        val request = GeminiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = userMessage)))
            )
        )

        viewModelScope.launch {
            try {
                val response = geminiService.generateContent(apiKey, request)
                val reply = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (!reply.isNullOrBlank()) {
                    appendMessage("Bot: $reply")
                } else {
                    Log.e("ChatViewModel", "Empty response from Gemini API.")
                    appendMessage("Bot: Sorry, I didn't understand that.")
                }

            } catch (e: IOException) {
                Log.e("ChatViewModel", "Network error: ${e.localizedMessage}", e)
                appendMessage("Bot: Network error. Please check your connection.")
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Unexpected error: ${e.localizedMessage}", e)
                appendMessage("Bot: Something went wrong. Please try again later.")
            }
        }
    }

    private fun appendMessage(message: String) {
        _messages.value = _messages.value + message
    }
}
