package com.example.mindease.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindease.*
import com.example.mindease.data.Content
import com.example.mindease.data.GeminiRequest
import com.example.mindease.data.GeminiResponse
import com.example.mindease.data.Part
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> = _messages

    private val apiKey = "AIzaSyBrWWu8DTlEDiVhEj2W3SOXndD8EVesKjQ"

    // Optional: Logging for debugging
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val geminiService = retrofit.create(GeminiApiService::class.java)

    fun sendMessage(userMessage: String) {
        val currentMessages = _messages.value.toMutableList()
        currentMessages.add("You: $userMessage")
        _messages.value = currentMessages

        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = userMessage)
                    )
                )
            )
        )

        geminiService.generateContent(apiKey, request)
            .enqueue(object : Callback<GeminiResponse> {
                override fun onResponse(call: Call<GeminiResponse>, response: Response<GeminiResponse>) {
                    if (response.isSuccessful) {
                        val reply = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                        if (!reply.isNullOrEmpty()) {
                            val updatedMessages = _messages.value.toMutableList()
                            updatedMessages.add("Bot: $reply")
                            _messages.value = updatedMessages
                        } else {
                            Log.e("ChatViewModel", "Empty reply from Gemini")
                        }
                    } else {
                        Log.e("ChatViewModel", "Gemini API error: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
                    Log.e("ChatViewModel", "Gemini API call failed", t)
                }
            })
    }
}
