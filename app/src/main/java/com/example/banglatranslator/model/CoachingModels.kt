package com.example.banglatranslator.model

enum class CurrencyRegion {
    BANGLADESH,
    INTERNATIONAL
}

data class CoachingTier(
    val id: String,
    val name: String,
    val bdtPrice: String,
    val usdPrice: String,
    val period: String,
    val tagline: String,
    val isPopular: Boolean = false,
    val isFree: Boolean = false,
    val features: List<String>,
    val badge: String? = null
)

data class CoachProfile(
    val id: Int,
    val name: String,
    val title: String,
    val rating: Double,
    val reviewCount: Int,
    val specialty: String,
    val bdtHourly: Int,
    val usdHourly: Int,
    val bio: String
)

data class LearningMetric(
    val title: String,
    val value: String,
    val subtitle: String,
    val progress: Float
)
