package com.example.banglatranslator.model

enum class Tone(val id: String, val label: String, val hint: String) {
    CASUAL("casual", "Casual", "Friend mode"),
    NEUTRAL("neutral", "Neutral", "Everyday"),
    FORMAL("formal", "Formal", "Polished")
}

data class TranslationResult(
    val originalBangla: String,
    val translation: String,
    val literal: String,
    val spanish: String,
    val tone: Tone,
    val notes: List<String>
)

data class PhrasebookEntry(
    val id: Int,
    val bangla: String,
    val english: String,
    val category: String,
    val example: String? = null,
    val note: String? = null
)

data class VocabularyEntry(
    val id: Int,
    val word: String,
    val pos: String,
    val cefr: String,
    val inOxford3000: Boolean,
    val inOxford5000: Boolean,
    val banglaMeaning: String,
    val exampleUs: String? = null,
    val audioUs: String? = null,
    val audioUk: String? = null
)

data class NewsItem(
    val id: Int,
    val sourceUrl: String,
    val sourceName: String,
    val titleBn: String,
    val titleEn: String,
    val summaryBn: String? = null,
    val summaryEn: String? = null,
    val publishedTimeAgo: String
)

data class GrammarComparison(
    val category: String,
    val item1Label: String,
    val item1Value: String,
    val item2Label: String,
    val item2Value: String,
    val note: String? = null
)

data class GrammarTopic(
    val title: String,
    val subtitle: String,
    val description: String,
    val comparisons: List<GrammarComparison> = emptyList(),
    val tips: List<String> = emptyList()
)
