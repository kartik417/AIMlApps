package com.example.mindease

import com.example.mindease.data.QuoteResponse
import retrofit2.http.GET

interface QuoteApiService {
    @GET("random")
    suspend fun getRandomQuote(): QuoteResponse
}
