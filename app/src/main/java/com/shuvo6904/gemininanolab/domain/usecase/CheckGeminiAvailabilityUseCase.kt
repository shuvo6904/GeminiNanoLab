package com.shuvo6904.gemininanolab.domain.usecase

import com.shuvo6904.gemininanolab.domain.model.GeminiAvailability
import com.shuvo6904.gemininanolab.domain.repository.GeminiRepository

class CheckGeminiAvailabilityUseCase(
    private val repository: GeminiRepository
) {
    suspend operator fun invoke(): GeminiAvailability {
        return repository.checkAvailability()
    }
}
