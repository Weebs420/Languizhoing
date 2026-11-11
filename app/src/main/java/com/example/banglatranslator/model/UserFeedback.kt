package com.example.banglatranslator.model

import java.util.UUID

enum class FeedbackCategory(val displayName: String, val iconName: String) {
    GENERAL("General", "ChatBubble"),
    TRANSLATION_QUALITY("Translation Accuracy", "Translate"),
    SLANG_DIALECT("Slang & Dialect", "Bolt"),
    BUG_REPORT("Bug / Glitch", "BugReport"),
    FEATURE_REQUEST("Feature Request", "Lightbulb")
}

data class UserFeedback(
    val id: String = UUID.randomUUID().toString(),
    val rating: Int = 5, // 1 to 5 stars
    val category: FeedbackCategory = FeedbackCategory.GENERAL,
    val feedbackText: String,
    val sourceContext: String? = null,
    val translationContext: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
