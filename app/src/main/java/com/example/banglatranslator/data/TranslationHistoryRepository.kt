package com.example.banglatranslator.data

import android.content.Context
import android.content.SharedPreferences
import com.example.banglatranslator.model.TargetLanguage
import com.example.banglatranslator.model.Tone
import com.example.banglatranslator.model.TranslationHistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

object TranslationHistoryRepository {
    private const val PREFS_NAME = "bangla_translator_history_prefs"
    private const val KEY_HISTORY = "history_entries_json"
    private const val MAX_HISTORY_SIZE = 100

    private var sharedPreferences: SharedPreferences? = null
    private val _history = MutableStateFlow<List<TranslationHistoryItem>>(emptyList())
    val history: StateFlow<List<TranslationHistoryItem>> = _history.asStateFlow()

    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized && sharedPreferences != null) return
        val appContext = context.applicationContext
        sharedPreferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isInitialized = true
        loadHistoryFromPrefs()
    }

    private fun loadHistoryFromPrefs() {
        val prefs = sharedPreferences ?: return
        val rawJson = prefs.getString(KEY_HISTORY, null)

        if (rawJson.isNullOrBlank()) {
            // Seed with initial realistic translation history items
            val seeded = listOf(
                TranslationHistoryItem(
                    id = "seed_1",
                    sourceText = "ভাই, আজকে অফিসে অনেক প্যারা গেছে। চিল করতে চাই, চলো কোথাও ঘুরে আসি।",
                    translatedText = "Bro, work was super stressful today. I just wanna chill, let's go hang out somewhere.",
                    targetLanguage = TargetLanguage.ENGLISH_US,
                    tone = Tone.CASUAL,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 15,
                    isFavorite = true,
                    detectedDialect = "Dhaka Youth Slang",
                    literalMeaning = "Brother, today office lot hassle went. Chill do want, let's somewhere tour come."
                ),
                TranslationHistoryItem(
                    id = "seed_2",
                    sourceText = "আপনি কেমন আছেন? অনেক দিন পর দেখা হয়ে খুব ভালো লাগলো।",
                    translatedText = "How have you been doing? It was truly wonderful seeing you after such a long time.",
                    targetLanguage = TargetLanguage.ENGLISH_US,
                    tone = Tone.FORMAL,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2,
                    isFavorite = false,
                    detectedDialect = "Standard Cholito",
                    literalMeaning = "You (formal) how are? Many day after see having very good felt."
                ),
                TranslationHistoryItem(
                    id = "seed_3",
                    sourceText = "প্যারা নাই চিল, সব ঠিক হয়ে যাবে। বিশ্বাস রাখো।",
                    translatedText = "No worries at all, stay calm, everything will turn out fine. Keep faith.",
                    targetLanguage = TargetLanguage.ENGLISH_US,
                    tone = Tone.CASUAL,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 5,
                    isFavorite = true,
                    detectedDialect = "Dhaka Street Colloquial",
                    literalMeaning = "Hassle none chill, all right become will. Belief keep."
                )
            )
            _history.value = seeded
            saveHistoryToPrefs(seeded)
            return
        }

        try {
            val jsonArray = JSONArray(rawJson)
            val list = mutableListOf<TranslationHistoryItem>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val targetLangCode = obj.optString("targetLanguage", TargetLanguage.ENGLISH_US.code)
                val targetLang = TargetLanguage.values().find { it.code == targetLangCode } ?: TargetLanguage.ENGLISH_US
                val toneId = obj.optString("tone", Tone.NEUTRAL.id)
                val tone = Tone.values().find { it.id == toneId } ?: Tone.NEUTRAL

                list.add(
                    TranslationHistoryItem(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        sourceText = obj.optString("sourceText", ""),
                        translatedText = obj.optString("translatedText", ""),
                        sourceLanguage = obj.optString("sourceLanguage", "Bangla"),
                        targetLanguage = targetLang,
                        tone = tone,
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        isFavorite = obj.optBoolean("isFavorite", false),
                        detectedDialect = if (obj.has("detectedDialect") && !obj.isNull("detectedDialect")) obj.optString("detectedDialect") else null,
                        isGeminiPowered = obj.optBoolean("isGeminiPowered", true),
                        literalMeaning = obj.optString("literalMeaning", ""),
                        pronunciation = if (obj.has("pronunciation") && !obj.isNull("pronunciation")) obj.optString("pronunciation") else null
                    )
                )
            }
            _history.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveHistoryToPrefs(list: List<TranslationHistoryItem>) {
        val prefs = sharedPreferences ?: return
        try {
            val jsonArray = JSONArray()
            for (item in list) {
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("sourceText", item.sourceText)
                    put("translatedText", item.translatedText)
                    put("sourceLanguage", item.sourceLanguage)
                    put("targetLanguage", item.targetLanguage.code)
                    put("tone", item.tone.id)
                    put("timestamp", item.timestamp)
                    put("isFavorite", item.isFavorite)
                    put("detectedDialect", item.detectedDialect)
                    put("isGeminiPowered", item.isGeminiPowered)
                    put("literalMeaning", item.literalMeaning)
                    put("pronunciation", item.pronunciation)
                }
                jsonArray.put(obj)
            }
            prefs.edit().putString(KEY_HISTORY, jsonArray.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addEntry(item: TranslationHistoryItem) {
        val current = _history.value.toMutableList()
        // Deduplicate if identical to the most recent entry
        if (current.isNotEmpty()) {
            val first = current.first()
            if (first.sourceText.trim() == item.sourceText.trim() &&
                first.targetLanguage == item.targetLanguage &&
                first.tone == item.tone
            ) {
                // Update timestamp of latest instead of duplicating
                current[0] = first.copy(
                    translatedText = item.translatedText,
                    timestamp = System.currentTimeMillis(),
                    detectedDialect = item.detectedDialect ?: first.detectedDialect,
                    literalMeaning = item.literalMeaning.ifBlank { first.literalMeaning }
                )
                _history.value = current
                saveHistoryToPrefs(current)
                return
            }
        }

        current.add(0, item)
        val trimmed = if (current.size > MAX_HISTORY_SIZE) current.take(MAX_HISTORY_SIZE) else current
        _history.value = trimmed
        saveHistoryToPrefs(trimmed)
    }

    fun toggleFavorite(id: String) {
        val updated = _history.value.map {
            if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
        }
        _history.value = updated
        saveHistoryToPrefs(updated)
    }

    fun deleteEntry(id: String) {
        val updated = _history.value.filter { it.id != id }
        _history.value = updated
        saveHistoryToPrefs(updated)
    }

    fun clearHistory(keepFavorites: Boolean = false) {
        val updated = if (keepFavorites) {
            _history.value.filter { it.isFavorite }
        } else {
            emptyList()
        }
        _history.value = updated
        saveHistoryToPrefs(updated)
    }
}
