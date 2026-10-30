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
    private const val GEMINI_MODEL = "gemini-2.5-flash"
    private const val API_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

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

        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isNullOrBlank() || apiKey == "YOUR_GEMINI_API_KEY") {
            Log.d(TAG, "No Gemini API key configured. Using local linguistic engine.")
            val localResult = TranslationEngine.translate(trimmed, tone, targetLanguage)
            val updatedNotes = localResult.notes.toMutableList()
            updatedNotes.add("Generated with offline linguistic engine. (Add your Gemini API Key in AI Studio Secrets panel to enable real-time Gemini API calls).")
            return@withContext localResult.copy(
                notes = updatedNotes,
                isGeminiPowered = false
            )
        }

        try {
            val prompt = """
                You are an expert bilingual lexicographer and translator specializing in Bangla (Bengali) and cross-lingual translation.
                Translate the following Bangla text into ${targetLanguage.displayName} (${targetLanguage.localizedName}).
                Apply tone register '${tone.label}' (${tone.hint}).
                Translate cultural idioms, Gen Z slang (e.g. প্যারা, চিল, কোপ, ছ্যাঁকা), honorifics, and Banglish naturally.

                Input Bangla:
                "$trimmed"

                Respond strictly with valid JSON:
                {
                  "translation": "natural fluent translation in ${targetLanguage.displayName}",
                  "literal": "literal grammatical translation",
                  "pronunciation": "phonetic or romanized pronunciation guide",
                  "detectedDialect": "Standard Spoken / Dhakaiya / Banglish / Gen Z Slang",
                  "notes": ["insight into idioms or cultural context", "tone explanation"]
                }
            """.trimIndent()

            val requestBodyJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        }
                        put("parts", parts)
                    })
                }
                put("contents", contents)

                val generationConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.3)
                }
                put("generationConfig", generationConfig)
            }

            val requestUrl = "$API_BASE_URL/$GEMINI_MODEL:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(requestUrl)
                .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                Log.e(TAG, "Gemini API HTTP ${response.code}: $responseBody")
                return@withContext fallbackWithNotice(trimmed, tone, targetLanguage, "Gemini API returned status ${response.code}")
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext fallbackWithNotice(trimmed, tone, targetLanguage, "No candidate returned from Gemini")
            }

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val rawText = parts.getJSONObject(0).getString("text")

            val parsedJson = JSONObject(rawText)
            val translation = parsedJson.optString("translation", "")
            val literal = parsedJson.optString("literal", "")
            val pronunciation = parsedJson.optString("pronunciation", "")
            val detectedDialect = parsedJson.optString("detectedDialect", "Standard Spoken Bangla")

            val notesArray = parsedJson.optJSONArray("notes")
            val notes = mutableListOf<String>()
            if (notesArray != null) {
                for (i in 0 until notesArray.length()) {
                    notes.add(notesArray.getString(i))
                }
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
            Log.e(TAG, "Gemini translation failed, falling back to local engine", e)
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
