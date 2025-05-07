package com.example.mindease

import com.example.mindease.data.GeminiRequest
import com.example.mindease.data.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {

    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    @POST("/v1beta/models/gemini-2.0-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}
