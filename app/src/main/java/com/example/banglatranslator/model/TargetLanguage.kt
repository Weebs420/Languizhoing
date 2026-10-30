package com.example.banglatranslator.model

enum class TargetLanguage(
    val code: String,
    val displayName: String,
    val localizedName: String,
    val flagEmoji: String
) {
    ENGLISH_US("en-US", "English (US)", "American English", "🇺🇸"),
    ENGLISH_UK("en-GB", "English (UK)", "British English", "🇬🇧"),
    SPANISH("es", "Spanish", "Español", "🇪🇸"),
    ARABIC("ar", "Arabic", "العربية", "🇸🇦"),
    HINDI("hi", "Hindi", "हिन्दी", "🇮🇳"),
    FRENCH("fr", "French", "Français", "🇫🇷"),
    GERMAN("de", "German", "Deutsch", "🇩🇪"),
    JAPANESE("ja", "Japanese", "日本語", "🇯🇵"),
    URDU("ur", "Urdu", "اردو", "🇵🇰"),
    CHINESE("zh", "Chinese", "中文", "🇨🇳");

    companion object {
        val quickLanguages = listOf(ENGLISH_US, SPANISH, ARABIC, HINDI, FRENCH, JAPANESE)
    }
}
