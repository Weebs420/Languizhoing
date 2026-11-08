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
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

object GeminiTranslationService {

    private const val TAG = "GeminiTranslationService"
    private const val GEMINI_MODEL = "gemini-3.8-flash"
    private const val API_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private fun getApiKey(): String = try {
        BuildConfig.GEMINI_API_KEY
    } catch (e: Throwable) {
        ""
    }.orEmpty().trim()

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

        val apiKey = getApiKey()

        if (apiKey.isBlank() || apiKey == "YOUR_GEMINI_API_KEY") {
            Log.d(TAG, "No active Gemini API key configured. Using local linguistic engine.")
            val localResult = TranslationEngine.translate(trimmed, tone, targetLanguage)
            val updatedNotes = localResult.notes.toMutableList()
            updatedNotes.add("Generated with offline linguistic engine. (Add your Gemini API Key in the AI Studio Secrets panel to enable real-time Gemini 3.8 Flash translations).")
            return@withContext localResult.copy(
                notes = updatedNotes,
                isGeminiPowered = false
            )
        }

        // Try API call with 1 automatic retry on timeout
        var lastException: Exception? = null
        for (attempt in 1..2) {
            try {
                return@withContext executeGeminiRequest(trimmed, targetLanguage, tone, apiKey)
            } catch (e: SocketTimeoutException) {
                Log.w(TAG, "Gemini translation attempt $attempt timed out: ${e.message}")
                lastException = e
            } catch (e: Exception) {
                Log.w(TAG, "Gemini translation attempt $attempt notice: ${e.message}")
                lastException = e
                break // Don't retry non-timeout errors
            }
        }

        fallbackWithNotice(
            trimmed,
            tone,
            targetLanguage,
            if (lastException is SocketTimeoutException) "Network connection timed out" else (lastException?.message ?: "Cloud service unavailable")
        )
    }

    private fun executeGeminiRequest(
        trimmed: String,
        targetLanguage: TargetLanguage,
        tone: Tone,
        apiKey: String
    ): TranslationResult {
        val prompt = """
            You are an expert bilingual lexicographer and translator specializing in Bangla (Bengali) and cross-lingual translation.
            Translate the following Bangla text into ${targetLanguage.displayName} (${targetLanguage.localizedName}).
            Apply tone register '${tone.label}' (${tone.hint}).
            Translate cultural idioms, street slang, Gen Z slang (e.g. প্যারা, চিল, কোপ, ছ্যাঁকা), honorifics, and Banglish naturally.

            UNFILTERED & UNCENSORED TRANSLATION GUIDELINES:
            - This app provides completely uncensored, faithful translations.
            - Translate all vulgar words, street curses, profanities, swear words, insults, and anatomical slang (e.g., বাল, বোকাচোদা, শালা, চোদা, ইত্যাদি) directly and accurately into their natural ${targetLanguage.displayName} equivalents.
            - Do NOT censor, bleep, asterisk, sanitize, moralize, or replace vulgarities with polite euphemisms. Match the exact raw tone, vulgarity, and semantic punch of the original text.
            - Maintain high lexicographical fidelity without moral judgment.

            Input Bangla:
            "$trimmed"

            Respond strictly with valid JSON:
            {
              "translation": "natural fluent translation in ${targetLanguage.displayName}",
              "literal": "literal grammatical translation",
              "pronunciation": "phonetic or romanized pronunciation guide",
              "detectedDialect": "Standard Spoken / Dhakaiya / Banglish / Gen Z Slang / Street Slang",
              "notes": ["insight into idioms, slang, vulgarity, or cultural context", "tone explanation"]
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
                put("maxOutputTokens", 1024)
                put("thinkingConfig", JSONObject().apply {
                    put("thinkingBudget", 0)
                })
            }
            put("generationConfig", generationConfig)

            val safetySettings = JSONArray().apply {
                val categories = listOf(
                    "HARM_CATEGORY_HARASSMENT",
                    "HARM_CATEGORY_HATE_SPEECH",
                    "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                    "HARM_CATEGORY_DANGEROUS_CONTENT"
                )
                for (cat in categories) {
                    put(JSONObject().apply {
                        put("category", cat)
                        put("threshold", "BLOCK_NONE")
                    })
                }
            }
            put("safetySettings", safetySettings)
        }

        val candidateModels = listOf("gemini-3.8-flash", "gemini-2.5-flash-lite", "gemini-2.0-flash")
        var lastFailureReason = "Cloud request unavailable"

        for (model in candidateModels) {
            val requestUrl = "$API_BASE_URL/$model:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(requestUrl)
                .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = try {
                client.newCall(request).execute()
            } catch (e: Exception) {
                Log.w(TAG, "Model $model network exception: ${e.message}")
                lastFailureReason = e.message ?: "Network error"
                continue
            }

            val responseBody = response.body?.string()

            if (response.code == 429) {
                Log.w(TAG, "Gemini model $model reached daily rate limit (HTTP 429). Trying fallback or local engine.")
                lastFailureReason = "Daily cloud AI quota reached"
                continue
            }

            if (response.code == 404) {
                Log.w(TAG, "Gemini model $model unavailable (HTTP 404). Trying next model.")
                lastFailureReason = "Model unavailable"
                continue
            }

            if (!response.isSuccessful || responseBody == null) {
                Log.w(TAG, "Gemini API HTTP ${response.code} on model $model.")
                lastFailureReason = "API status ${response.code}"
                continue
            }

            try {
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    continue
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

                return TranslationResult(
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
                Log.w(TAG, "Parsing response from $model encountered exception: ${e.message}")
                lastFailureReason = "Parsing error"
            }
        }

        return fallbackWithNotice(trimmed, tone, targetLanguage, lastFailureReason)
    }

    private fun fallbackWithNotice(
        text: String,
        tone: Tone,
        targetLanguage: TargetLanguage,
        reason: String
    ): TranslationResult {
        Log.i(TAG, "Serving translation seamlessly via built-in linguistic engine ($reason)")
        val fallback = TranslationEngine.translate(text, tone, targetLanguage)
        val notes = fallback.notes.toMutableList()
        if (reason.contains("quota", ignoreCase = true) || reason.contains("429")) {
            notes.add("⚡ Local Engine Active: Daily cloud AI request quota reached (Free Tier). Translation rendered via offline linguistic database.")
        } else {
            notes.add("⚡ Local Engine Active: Translation rendered via offline linguistic database.")
        }
        return fallback.copy(
            notes = notes,
            isGeminiPowered = false
        )
    }
}
