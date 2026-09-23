package com.shuvo6904.gemininanolab.domain.repository

import com.shuvo6904.gemininanolab.domain.model.GeminiAvailability

interface GeminiRepository {
    suspend fun checkAvailability(): GeminiAvailability
    suspend fun downloadModel(): Result<Unit>
    suspend fun generateContent(prompt: String): Result<String>
    fun close()
}
