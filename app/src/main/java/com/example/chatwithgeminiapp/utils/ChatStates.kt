package com.example.chatwithgeminiapp.utils

import android.graphics.Bitmap

// This is the data class for the chat states.
data class ChatStates(
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt: String = "",
    val bitmap: Bitmap? = null
)