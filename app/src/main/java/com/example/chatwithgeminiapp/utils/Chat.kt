package com.example.chatwithgeminiapp.utils

import android.graphics.Bitmap

// This is the data class for the chat
data class Chat (
    val prompt: String,
    val bitmap: Bitmap?,
    val isFromUser: Boolean
)