package com.shuvo6904.gemininanolab.presentation.prompt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shuvo6904.gemininanolab.domain.model.GeminiAvailability
import com.shuvo6904.gemininanolab.domain.repository.GeminiRepository
import com.shuvo6904.gemininanolab.domain.usecase.CheckGeminiAvailabilityUseCase
import com.shuvo6904.gemininanolab.domain.usecase.DownloadGeminiModelUseCase
import com.shuvo6904.gemininanolab.domain.usecase.GeneratePromptResponseUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PromptViewModel(
    private val checkGeminiAvailabilityUseCase: CheckGeminiAvailabilityUseCase,
    private val downloadGeminiModelUseCase: DownloadGeminiModelUseCase,
    private val generatePromptResponseUseCase: GeneratePromptResponseUseCase,
    private val repository: GeminiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromptUiState())
    val uiState: StateFlow<PromptUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<PromptUiEffect>()
    val uiEffect: SharedFlow<PromptUiEffect> = _uiEffect.asSharedFlow()

    init {
        handleIntent(PromptIntent.CheckAvailability)
    }

    fun handleIntent(intent: PromptIntent) {
        when (intent) {
            is PromptIntent.OnPromptInputChanged -> {
                _uiState.update { it.copy(promptInput = intent.newInput) }
            }
            PromptIntent.CheckAvailability -> {
                checkAvailability()
            }
            PromptIntent.DownloadModel -> {
                downloadModel()
            }
            PromptIntent.GenerateResponse -> {
                generateResponse()
            }
            PromptIntent.ClearPrompt -> {
                _uiState.update { it.copy(promptInput = "") }
            }
            PromptIntent.ClearResponse -> {
                _uiState.update { it.copy(generationState = GenerationState.Idle) }
            }
        }
    }

    private fun checkAvailability() {
        viewModelScope.launch {
            _uiState.update { it.copy(availability = GeminiAvailability.Checking) }
            val status = checkGeminiAvailabilityUseCase()
            _uiState.update { it.copy(availability = status) }
        }
    }

    private fun downloadModel() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDownloading = true,
                    availability = GeminiAvailability.Downloading()
                )
            }
            val result = downloadGeminiModelUseCase()
            result.onSuccess {
                _uiEffect.emit(PromptUiEffect.ShowToast("Gemini Nano model downloaded successfully!"))
                checkAvailability()
            }.onFailure { error ->
                _uiEffect.emit(PromptUiEffect.ShowSnackbar("Download failed: ${error.localizedMessage}"))
                checkAvailability()
            }
            _uiState.update { it.copy(isDownloading = false) }
        }
    }

    private fun generateResponse() {
        val currentInput = _uiState.value.promptInput.trim()
        if (currentInput.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(generationState = GenerationState.Loading) }
            val result = generatePromptResponseUseCase(currentInput)
            result.onSuccess { text ->
                _uiState.update { it.copy(generationState = GenerationState.Success(text)) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(generationState = GenerationState.Error(error.localizedMessage ?: "Generation failed"))
                }
                _uiEffect.emit(PromptUiEffect.ShowSnackbar("Error: ${error.localizedMessage}"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.close()
    }

    class Factory(
        private val repository: GeminiRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PromptViewModel(
                checkGeminiAvailabilityUseCase = CheckGeminiAvailabilityUseCase(repository),
                downloadGeminiModelUseCase = DownloadGeminiModelUseCase(repository),
                generatePromptResponseUseCase = GeneratePromptResponseUseCase(repository),
                repository = repository
            ) as T
        }
    }
}
