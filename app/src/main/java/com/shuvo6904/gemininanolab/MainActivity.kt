package com.shuvo6904.gemininanolab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shuvo6904.gemininanolab.data.repository.GeminiRepositoryImpl
import com.shuvo6904.gemininanolab.presentation.prompt.PromptScreen
import com.shuvo6904.gemininanolab.presentation.prompt.PromptViewModel
import com.shuvo6904.gemininanolab.ui.theme.GeminiNanoLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeminiNanoLabTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val repository = remember { GeminiRepositoryImpl() }
                    val viewModel: PromptViewModel = viewModel(
                        factory = PromptViewModel.Factory(repository)
                    )
                    PromptScreen(viewModel = viewModel)
                }
            }
        }
    }
}
