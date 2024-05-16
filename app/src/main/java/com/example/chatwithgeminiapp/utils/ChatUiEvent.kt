package com.example.chatwithgeminiapp.utils

import android.graphics.Bitmap

// Define the UI events
sealed class ChatUiEvent {
    data class UpdatePrompt(val newPrompt: String) : ChatUiEvent()
    data class SendPrompt(val prompt: String, val bitmap: Bitmap?) : ChatUiEvent()
}