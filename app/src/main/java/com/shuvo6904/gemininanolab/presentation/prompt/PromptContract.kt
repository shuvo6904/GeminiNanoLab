package com.shuvo6904.gemininanolab.presentation.prompt

import com.shuvo6904.gemininanolab.domain.model.GeminiAvailability

sealed interface GenerationState {
    data object Idle : GenerationState
    data object Loading : GenerationState
    data class Success(val result: String) : GenerationState
    data class Error(val message: String) : GenerationState
}

data class PromptUiState(
    val promptInput: String = "",
    val availability: GeminiAvailability = GeminiAvailability.Checking,
    val generationState: GenerationState = GenerationState.Idle,
    val isDownloading: Boolean = false
) {
    val isGenerateButtonEnabled: Boolean
        get() = availability is GeminiAvailability.Available &&
                promptInput.isNotBlank() &&
                generationState !is GenerationState.Loading &&
                !isDownloading
}

sealed interface PromptIntent {
    data class OnPromptInputChanged(val newInput: String) : PromptIntent
    data object CheckAvailability : PromptIntent
    data object DownloadModel : PromptIntent
    data object GenerateResponse : PromptIntent
    data object ClearPrompt : PromptIntent
    data object ClearResponse : PromptIntent
}

sealed interface PromptUiEffect {
    data class ShowToast(val message: String) : PromptUiEffect
    data class ShowSnackbar(val message: String) : PromptUiEffect
}
