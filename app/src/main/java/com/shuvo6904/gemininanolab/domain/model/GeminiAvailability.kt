package com.shuvo6904.gemininanolab.domain.model

sealed interface GeminiAvailability {
    data object Checking : GeminiAvailability
    data object Available : GeminiAvailability
    data object Downloadable : GeminiAvailability
    data class Downloading(val progressPercentage: Int? = null) : GeminiAvailability
    data class Unavailable(val reason: String = "Gemini Nano is not supported on this device or OS version.") : GeminiAvailability
}
