package com.example.chatwithgeminiapp.data

import android.graphics.Bitmap
import com.example.chatwithgeminiapp.BuildConfig
import com.example.chatwithgeminiapp.utils.Chat
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.ai.client.generativeai.type.content

// This class is used to get the response from the generative model of Gemini.
object ChatData {

    // This function is used to get the response from the generative model of Gemini.
    suspend fun getResponse(prompt: String): Chat {
        // This variable is used to get the generative model of Gemini with model name and api key.
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.apiKey
        )

        // This try catch block is used to get the response from the generative model of Gemini.
        try {
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }

            // This return the response from the generative model of Gemini.
            return Chat(
                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false
            )

        } catch (e: Exception) {
            // This return the error message from the generative model of Gemini.
            return Chat(
                prompt = e.message ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }

    }

    // This function is used to get the response from the generative model of Gemini with image.
    suspend fun getResponseWithImage(prompt: String, bitmap: Bitmap): Chat {
        // This variable is used to get the generative model of Gemini with model name and api key.
        val generativeChatImageModel = GenerativeModel(
            modelName = "gemini-pro-vision",
            apiKey = BuildConfig.apiKey
        )

        // This try catch block is used to get the response from the generative model of Gemini with image.
        try {
            // This variable is used to create the input content for the generative model of Gemini with image and text.
            val inputContent = content {
                image(bitmap)
                text(prompt)
            }

            // This variable is used to get the response from the generative model of Gemini with image.
            val response = withContext(Dispatchers.IO) {
                generativeChatImageModel.generateContent(inputContent)
            }

            // This return the response from the generative model of Gemini with image.
            return Chat(
                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false
            )

        } catch (e: Exception) {
            // This return the error message from the generative model of Gemini with image.
            return Chat(
                prompt = e.message ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }
    }
}