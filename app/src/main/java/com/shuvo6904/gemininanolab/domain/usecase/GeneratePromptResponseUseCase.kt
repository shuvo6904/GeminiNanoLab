package com.shuvo6904.gemininanolab.domain.usecase

import com.shuvo6904.gemininanolab.domain.repository.GeminiRepository

class GeneratePromptResponseUseCase(
    private val repository: GeminiRepository
) {
    suspend operator fun invoke(prompt: String): Result<String> {
        if (prompt.isBlank()) {
            return Result.failure(IllegalArgumentException("Prompt cannot be empty"))
        }
        return repository.generateContent(prompt)
    }
}
