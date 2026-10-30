package com.example.banglatranslator.data

import android.util.Log
import com.example.banglatranslator.BuildConfig
import com.example.banglatranslator.model.TargetLanguage
import com.example.banglatranslator.model.Tone
import com.example.banglatranslator.model.TranslationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiTranslationService {

    private const val TAG = "GeminiTranslationService"

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    private fun getProxyEndpoint(): String = try {
        BuildConfig.GEMINI_API_ENDPOINT
    } catch (e: Throwable) {
        ""
    }.orEmpty()

    suspend fun translate(
        banglaText: String,
        targetLanguage: TargetLanguage,
        tone: Tone
    ): TranslationResult = withContext(Dispatchers.IO) {
        val trimmed = banglaText.trim()
        if (trimmed.isEmpty()) {
            return@withContext TranslationResult(
                originalBangla = "",
                translation = "",
                literal = "",
                targetLanguage = targetLanguage,
                tone = tone,
                notes = emptyList(),
                isGeminiPowered = false
            )
        }

        val proxyEndpoint = getProxyEndpoint()
        if (proxyEndpoint.isBlank()) {
            Log.d(TAG, "No cloud proxy endpoint configured. Using local linguistic engine.")
            val localResult = TranslationEngine.translate(trimmed, tone, targetLanguage)
            val updatedNotes = localResult.notes.toMutableList()
            updatedNotes.add("Cloud translation is disabled until a secure backend proxy is configured. Gemini keys must never be stored in the Android app.")
            return@withContext localResult.copy(
                notes = updatedNotes,
                isGeminiPowered = false
            )
        }

        try {
            val requestBodyJson = JSONObject().apply {
                put("banglaText", trimmed)
                put("targetLanguage", targetLanguage.name)
                put("tone", tone.name)
            }

            val request = Request.Builder()
                .url(proxyEndpoint)
                .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                Log.e(TAG, "Cloud proxy HTTP ${response.code}: $responseBody")
                return@withContext fallbackWithNotice(trimmed, tone, targetLanguage, "Cloud proxy returned status ${response.code}")
            }

            val jsonResponse = JSONObject(responseBody)
            if (jsonResponse.has("error")) {
                val errorMessage = jsonResponse.optString("error", "Cloud proxy returned an error")
                return@withContext fallbackWithNotice(trimmed, tone, targetLanguage, errorMessage)
            }

            val translation = jsonResponse.optString("translation", "")
            val literal = jsonResponse.optString("literal", "")
            val pronunciation = jsonResponse.optString("pronunciation", "")
            val detectedDialect = jsonResponse.optString("detectedDialect", "Standard Spoken Bangla")

            val notesArray = jsonResponse.optJSONArray("notes")
            val notes = mutableListOf<String>()
            if (notesArray != null) {
                for (i in 0 until notesArray.length()) {
                    notes.add(notesArray.getString(i))
                }
            }

            if (translation.isBlank() && literal.isBlank() && notes.isEmpty()) {
                return@withContext fallbackWithNotice(trimmed, tone, targetLanguage, "Cloud proxy returned empty payload")
            }

            TranslationResult(
                originalBangla = trimmed,
                translation = translation,
                literal = literal,
                targetLanguage = targetLanguage,
                tone = tone,
                pronunciation = if (pronunciation.isNotBlank()) pronunciation else null,
                notes = notes,
                isGeminiPowered = true,
                detectedDialect = detectedDialect
            )
        } catch (e: Exception) {
            Log.e(TAG, "Cloud proxy translation failed, falling back to local engine", e)
            fallbackWithNotice(trimmed, tone, targetLanguage, e.message ?: "Network error")
        }
    }

    private fun fallbackWithNotice(
        text: String,
        tone: Tone,
        targetLanguage: TargetLanguage,
        reason: String
    ): TranslationResult {
        val fallback = TranslationEngine.translate(text, tone, targetLanguage)
        val notes = fallback.notes.toMutableList()
        notes.add("Note: Cloud request failed ($reason). Results rendered via built-in linguistic engine.")
        return fallback.copy(
            notes = notes,
            isGeminiPowered = false
        )
    }
}
