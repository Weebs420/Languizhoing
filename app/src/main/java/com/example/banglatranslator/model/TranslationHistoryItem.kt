package com.example.banglatranslator.model

import java.util.UUID

data class TranslationHistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val sourceText: String,
    val translatedText: String,
    val sourceLanguage: String = "Bangla",
    val sourceLangCode: String = TargetLanguage.BANGLA.code,
    val targetLanguage: TargetLanguage = TargetLanguage.ENGLISH_US,
    val tone: Tone = Tone.NEUTRAL,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val detectedDialect: String? = null,
    val isGeminiPowered: Boolean = true,
    val literalMeaning: String = "",
    val pronunciation: String? = null,
    val isBanglaToTarget: Boolean = true
) {
    fun resolvedSourceLanguage(): TargetLanguage {
        return TargetLanguage.fromCodeOrName(sourceLangCode)
            ?: TargetLanguage.fromCodeOrName(sourceLanguage)
            ?: if (isBanglaToTarget) TargetLanguage.BANGLA else targetLanguage
    }
}
