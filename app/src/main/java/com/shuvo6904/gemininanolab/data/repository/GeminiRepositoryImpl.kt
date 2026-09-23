package com.shuvo6904.gemininanolab.data.repository

import com.google.mlkit.genai.common.DownloadStatus
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.GenerativeModel
import com.shuvo6904.gemininanolab.domain.model.GeminiAvailability
import com.shuvo6904.gemininanolab.domain.repository.GeminiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRepositoryImpl : GeminiRepository {

    private var generativeModel: GenerativeModel? = null

    private fun getOrCreateModel(): GenerativeModel {
        return generativeModel ?: Generation.getClient().also { generativeModel = it }
    }

    override suspend fun checkAvailability(): GeminiAvailability = withContext(Dispatchers.IO) {
        try {
            val model = getOrCreateModel()
            when (val status = model.checkStatus()) {
                FeatureStatus.AVAILABLE -> GeminiAvailability.Available
                FeatureStatus.DOWNLOADABLE -> GeminiAvailability.Downloadable
                FeatureStatus.DOWNLOADING -> GeminiAvailability.Downloading()
                FeatureStatus.UNAVAILABLE -> GeminiAvailability.Unavailable("Gemini Nano is unavailable on this device.")
                else -> GeminiAvailability.Unavailable("Unknown feature status code: $status")
            }
        } catch (e: Exception) {
            GeminiAvailability.Unavailable(e.localizedMessage ?: "Failed to check Gemini Nano availability.")
        }
    }

    override suspend fun downloadModel(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val model = getOrCreateModel()
            var downloadError: Exception? = null
            model.download().collect { status ->
                when (status) {
                    is DownloadStatus.DownloadFailed -> {
                        downloadError = status.e
                    }
                    else -> {}
                }
            }
            val err = downloadError
            if (err != null) {
                Result.failure(err)
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generateContent(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val model = getOrCreateModel()
            val response = model.generateContent(prompt)
            val text = response.candidates.firstOrNull()?.text
                ?: return@withContext Result.failure(IllegalStateException("No response candidate generated from model."))
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun close() {
        generativeModel?.close()
        generativeModel = null
    }
}
