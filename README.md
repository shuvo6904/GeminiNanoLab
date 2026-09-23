# 🧪 Gemini Nano Lab

A modern Android application demonstrating on-device generative AI capabilities using **Gemini Nano** and the **ML Kit GenAI Prompt API**, built with **Clean Architecture**, **MVI (Model-View-Intent)**, and **Jetpack Compose (Material 3)**.

---

## ✨ Features

- **Device Capabilities Check**: Automatically checks if the current device/emulator supports on-device Gemini Nano via Android AICore.
- **Model Lifecycle Management**: Handles all availability states (`Checking`, `Available`, `Downloadable`, `Downloading`, and `Unavailable`) with an option to trigger on-device model downloads.
- **Prompt Execution**: Interactive text input interface allowing users to send natural language prompts for local, low-latency, privacy-focused inference.
- **Material 3 MVI UI**: Reactive UI built with Material 3 components, dynamic status cards, copy-to-clipboard functionality, and structured feedback (Toasts/Snackbars).

---

## 🏗️ Architecture

The project follows **Clean Architecture** principles separated into distinct layers:

```text
com.shuvo6904.gemininanolab/
├── domain/                      # Business Logic & Interfaces
│   ├── model/
│   │   └── GeminiAvailability.kt
│   ├── repository/
│   │   └── GeminiRepository.kt
│   └── usecase/
│       ├── CheckGeminiAvailabilityUseCase.kt
│       ├── DownloadGeminiModelUseCase.kt
│       └── GeneratePromptResponseUseCase.kt
│
├── data/                        # Framework Implementation & ML Kit
│   └── repository/
│       └── GeminiRepositoryImpl.kt
│
└── presentation/                # MVI & Jetpack Compose UI
    └── prompt/
        ├── PromptContract.kt    # State, Intent, Effect definitions
        ├── PromptViewModel.kt   # MVI State Management
        └── PromptScreen.kt      # Material 3 UI Components
```

### MVI Design Pattern
- **State (`PromptUiState`)**: Single source of truth containing input text, availability status, model loading/generation state, and button flags.
- **Intent (`PromptIntent`)**: Explicit user actions (`CheckAvailability`, `DownloadModel`, `OnPromptInputChanged`, `GenerateResponse`, `ClearPrompt`, `ClearResponse`).
- **Side Effect (`PromptUiEffect`)**: One-time events emitted via `SharedFlow` for Snackbars and Toasts.

---

## 🛠️ Tech Stack & Dependencies

- **Language**: Kotlin 2.2
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: Clean Architecture + MVI
- **AI Engine**: `com.google.mlkit:genai-prompt:1.0.0-beta4` (powered by Android AICore & Gemini Nano)
- **Coroutines**: `kotlinx-coroutines-android`
- **Build System**: Android Gradle Plugin 9.3+ with Gradle Version Catalog (`libs.versions.toml`)

---

## 📲 Hardware & Setup Requirements

Gemini Nano executes locally via Android's **AICore** system service.

### Emulator Configuration (Recommended)
- **Target SDK**: Android 14 (API Level 34) or Android 15 (API Level 35) with **Google Play APIs** (`x86_64`).
- **RAM**: **8 GB** minimum (12 GB recommended).
- **Internal Storage**: **16 GB** minimum.
- **Developer Options Settings**:
  1. Go to **Settings** → **About emulated device** → Tap **Build Number** 7 times.
  2. Go to **Settings** → **System** → **Developer Options**.
  3. Enable **Enable AICore Persistent Mode** under the AICore section.

### Supported Physical Devices
- **Google Pixel**: Pixel 8 / 8a / 8 Pro, Pixel 9 / 9 Pro / 9 Pro XL / 9 Pro Fold.
- **Samsung Galaxy**: Galaxy S24 / S23 series, Galaxy Z Fold5 / Z Flip5 running Android 14+.

---

## 🚀 Getting Started

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/shuvo6904/GeminiNanoLab.git
   cd GeminiNanoLab
   ```

2. **Open in Android Studio**:
   Open the project using Android Studio Ladybug or newer.

3. **Build & Run**:
   Select an AICore-capable emulator or physical device and click **Run app**.
