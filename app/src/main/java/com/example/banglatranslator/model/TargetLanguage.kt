package com.example.banglatranslator.model

import java.util.Locale

enum class TargetLanguage(
    val code: String,
    val displayName: String,
    val localizedName: String,
    val flagEmoji: String
) {
    AUTO_DETECT("auto", "Auto-Detect", "Detect language", "🌐"),
    BANGLA("bn", "Bangla", "বাংলা", "🇧🇩"),
    ENGLISH_US("en-US", "English (US)", "American English", "🇺🇸"),
    ENGLISH_UK("en-GB", "English (UK)", "British English", "🇬🇧"),
    SPANISH("es", "Spanish", "Español", "🇪🇸"),
    ARABIC("ar", "Arabic", "العربية", "🇸🇦"),
    HINDI("hi", "Hindi", "हिन्दी", "🇮🇳"),
    FRENCH("fr", "French", "Français", "🇫🇷"),
    GERMAN("de", "German", "Deutsch", "🇩🇪"),
    JAPANESE("ja", "Japanese", "日本語", "🇯🇵"),
    URDU("ur", "Urdu", "اردو", "🇵🇰"),
    CHINESE("zh", "Chinese", "中文", "🇨🇳"),
    KOREAN("ko", "Korean", "한국어", "🇰🇷"),
    PORTUGUESE("pt", "Portuguese", "Português", "🇧🇷"),
    ITALIAN("it", "Italian", "Italiano", "🇮🇹"),
    TURKISH("tr", "Turkish", "Türkçe", "🇹🇷"),
    RUSSIAN("ru", "Russian", "Русский", "🇷🇺");

    fun toLocale(): Locale = when (this) {
        BANGLA -> Locale("bn", "BD")
        ENGLISH_US -> Locale.US
        ENGLISH_UK -> Locale.UK
        SPANISH -> Locale("es", "ES")
        ARABIC -> Locale("ar")
        HINDI -> Locale("hi", "IN")
        FRENCH -> Locale.FRENCH
        GERMAN -> Locale.GERMAN
        JAPANESE -> Locale.JAPANESE
        URDU -> Locale("ur", "PK")
        CHINESE -> Locale.SIMPLIFIED_CHINESE
        KOREAN -> Locale.KOREAN
        PORTUGUESE -> Locale("pt", "BR")
        ITALIAN -> Locale.ITALIAN
        TURKISH -> Locale("tr", "TR")
        RUSSIAN -> Locale("ru", "RU")
        AUTO_DETECT -> Locale.US
    }

    companion object {
        val sourceLanguages: List<TargetLanguage> = entries
        val targetLanguages: List<TargetLanguage> = entries.filter { it != AUTO_DETECT }
        val quickLanguages: List<TargetLanguage> = listOf(
            BANGLA, ENGLISH_US, SPANISH, ARABIC, HINDI, FRENCH, JAPANESE
        )

        fun fromCodeOrName(value: String?): TargetLanguage? {
            if (value.isNullOrBlank()) return null
            return entries.firstOrNull {
                it.code.equals(value, ignoreCase = true) ||
                    it.displayName.equals(value, ignoreCase = true) ||
                    it.name.equals(value, ignoreCase = true)
            }
        }
    }
}
