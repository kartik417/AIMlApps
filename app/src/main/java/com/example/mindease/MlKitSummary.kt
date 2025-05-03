package com.example.mindease//package com.example.mindease

fun generateExtractiveSummary(text: String): String {
    val sentences = text.split(Regex("(?<=[.!?])\\s+")) // Split by sentence
    val keywords = listOf("important", "feeling", "today", "stress", "happy", "anxious", "relaxed")

    val scored = sentences.map { sentence ->
        val score = keywords.count { keyword -> sentence.contains(keyword, ignoreCase = true) }
        sentence to score
    }

    val topSentences = scored
        .sortedByDescending { it.second }
        .take(2) // You can take top 2-3 sentences
        .map { it.first }

    return topSentences.joinToString(" ")
}
