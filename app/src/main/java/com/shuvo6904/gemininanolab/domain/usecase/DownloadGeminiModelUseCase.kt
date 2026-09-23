package com.shuvo6904.gemininanolab.domain.usecase

import com.shuvo6904.gemininanolab.domain.repository.GeminiRepository

class DownloadGeminiModelUseCase(
    private val repository: GeminiRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.downloadModel()
    }
}
