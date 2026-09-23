package com.shuvo6904.gemininanolab.presentation.prompt

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shuvo6904.gemininanolab.domain.model.GeminiAvailability

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptScreen(
    viewModel: PromptViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is PromptUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is PromptUiEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gemini Nano Lab",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Card
            AvailabilityStatusCard(
                availability = uiState.availability,
                onCheckStatus = { viewModel.handleIntent(PromptIntent.CheckAvailability) },
                onDownloadModel = { viewModel.handleIntent(PromptIntent.DownloadModel) }
            )

            // Prompt Input Card
            PromptInputCard(
                promptInput = uiState.promptInput,
                onPromptChanged = { viewModel.handleIntent(PromptIntent.OnPromptInputChanged(it)) },
                onClearPrompt = { viewModel.handleIntent(PromptIntent.ClearPrompt) },
                onGenerateClicked = { viewModel.handleIntent(PromptIntent.GenerateResponse) },
                isGenerateEnabled = uiState.isGenerateButtonEnabled,
                isGenerating = uiState.generationState is GenerationState.Loading
            )

            // Output Response Card
            ResponseResultCard(
                generationState = uiState.generationState,
                onCopyClicked = { text ->
                    clipboardManager.setText(AnnotatedString(text))
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                onClearResponse = { viewModel.handleIntent(PromptIntent.ClearResponse) }
            )
        }
    }
}

@Composable
private fun AvailabilityStatusCard(
    availability: GeminiAvailability,
    onCheckStatus: () -> Unit,
    onDownloadModel: () -> Unit
) {
    val (cardColor, titleText, descText, icon) = when (availability) {
        GeminiAvailability.Checking -> StatusCardConfig(
            color = MaterialTheme.colorScheme.surfaceVariant,
            title = "Checking Availability...",
            description = "Determining whether this device supports on-device Gemini Nano.",
            icon = null
        )
        GeminiAvailability.Available -> StatusCardConfig(
            color = Color(0xFFE8F5E9),
            title = "Gemini Nano Available",
            description = "This device supports Gemini Nano, and the model is ready for on-device AI inference.",
            icon = Icons.Default.CheckCircle
        )
        GeminiAvailability.Downloadable -> StatusCardConfig(
            color = Color(0xFFFFF8E1),
            title = "Model Download Required",
            description = "Gemini Nano is supported on this device, but the model needs to be downloaded before first use.",
            icon = Icons.Default.Download
        )
        is GeminiAvailability.Downloading -> StatusCardConfig(
            color = Color(0xFFE3F2FD),
            title = "Downloading Gemini Nano...",
            description = "Downloading on-device AI model binaries. Please stay on this screen.",
            icon = null
        )
        is GeminiAvailability.Unavailable -> StatusCardConfig(
            color = Color(0xFFFFEBEE),
            title = "Gemini Nano Unavailable",
            description = availability.reason,
            icon = Icons.Default.ErrorOutline
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (availability is GeminiAvailability.Checking || availability is GeminiAvailability.Downloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else if (icon != null) {
                    val iconTint = when (availability) {
                        GeminiAvailability.Available -> Color(0xFF2E7D32)
                        GeminiAvailability.Downloadable -> Color(0xFFF57F17)
                        is GeminiAvailability.Unavailable -> Color(0xFFC62828)
                        else -> MaterialTheme.colorScheme.primary
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = descText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (availability is GeminiAvailability.Downloadable) {
                    Button(
                        onClick = onDownloadModel,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download Model")
                    }
                } else {
                    OutlinedButton(
                        onClick = onCheckStatus,
                        enabled = availability !is GeminiAvailability.Checking && availability !is GeminiAvailability.Downloading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Re-check Status")
                    }
                }
            }
        }
    }
}

private data class StatusCardConfig(
    val color: Color,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector?
)

@Composable
private fun PromptInputCard(
    promptInput: String,
    onPromptChanged: (String) -> Unit,
    onClearPrompt: () -> Unit,
    onGenerateClicked: () -> Unit,
    isGenerateEnabled: Boolean,
    isGenerating: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Write Prompt",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = promptInput,
                onValueChange = onPromptChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                placeholder = {
                    Text("Enter your prompt here...\ne.g. Summarize the key features of Jetpack Compose.")
                },
                trailingIcon = {
                    if (promptInput.isNotEmpty()) {
                        IconButton(onClick = onClearPrompt) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear prompt"
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${promptInput.length} chars",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onGenerateClicked,
                    enabled = isGenerateEnabled,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generating...")
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Generate Response")
                    }
                }
            }
        }
    }
}

@Composable
private fun ResponseResultCard(
    generationState: GenerationState,
    onCopyClicked: (String) -> Unit,
    onClearResponse: () -> Unit
) {
    AnimatedVisibility(
        visible = generationState !is GenerationState.Idle,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Model Response",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    if (generationState is GenerationState.Success) {
                        Row {
                            IconButton(onClick = { onCopyClicked(generationState.result) }) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy text"
                                )
                            }
                            IconButton(onClick = onClearResponse) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear response"
                                )
                            }
                        }
                    }
                }

                when (generationState) {
                    GenerationState.Idle -> {}
                    GenerationState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Gemini Nano is generating response on-device...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    is GenerationState.Success -> {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = generationState.result,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    is GenerationState.Error -> {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                text = generationState.message,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}
