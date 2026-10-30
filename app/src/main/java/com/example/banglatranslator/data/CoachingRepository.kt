package com.example.banglatranslator.data

import com.example.banglatranslator.model.CoachProfile
import com.example.banglatranslator.model.CoachingTier
import com.example.banglatranslator.model.LearningMetric

object CoachingRepository {

    val tiers: List<CoachingTier> = listOf(
        CoachingTier(
            id = "free",
            name = "Free",
            bdtPrice = "0",
            usdPrice = "0",
            period = "forever",
            tagline = "Useful language tools for everyone.",
            isFree = true,
            features = listOf(
                "Translation and explanations",
                "Basic vocabulary",
                "Basic practice"
            )
        ),
        CoachingTier(
            id = "learner",
            name = "Learner",
            bdtPrice = "199",
            usdPrice = "2.99",
            period = "/month",
            tagline = "Build a consistent learning habit.",
            features = listOf(
                "Personal learning path",
                "Daily lessons",
                "Progress tracking"
            )
        ),
        CoachingTier(
            id = "coach",
            name = "Coach",
            bdtPrice = "399",
            usdPrice = "5.99",
            period = "/month",
            tagline = "Add feedback and community support.",
            isPopular = true,
            features = listOf(
                "Everything in Learner",
                "Limited coach feedback",
                "Group sessions"
            )
        ),
        CoachingTier(
            id = "pro",
            name = "Pro",
            bdtPrice = "699",
            usdPrice = "9.99",
            period = "/month",
            tagline = "Get more guidance as your goals grow.",
            badge = "Pro is your next step",
            features = listOf(
                "Everything in Coach",
                "Higher-touch coaching",
                "Priority feedback"
            )
        ),
        CoachingTier(
            id = "premium",
            name = "Premium",
            bdtPrice = "1,499+",
            usdPrice = "19.99+",
            period = "/month",
            tagline = "For learners who want the highest-touch path.",
            features = listOf(
                "Everything in Pro",
                "Individual study plan",
                "Premium coaching access"
            )
        )
    )

    val progressMetrics: List<LearningMetric> = listOf(
        LearningMetric(
            title = "Level Progression",
            value = "A2 → B1",
            subtitle = "CEFR Conversational track (68% complete)",
            progress = 0.68f
        ),
        LearningMetric(
            title = "Vocabulary Growth",
            value = "342 Words",
            subtitle = "Oxford 3000 lemmas retained (+48 this month)",
            progress = 0.55f
        ),
        LearningMetric(
            title = "Mistake Reduction",
            value = "42% Fewer Slips",
            subtitle = "Honorific agreement & tense consistency",
            progress = 0.74f
        ),
        LearningMetric(
            title = "Speaking Improvement",
            value = "86% Accuracy",
            subtitle = "Native cadence, stress & contraction flow",
            progress = 0.86f
        )
    )

    val coaches: List<CoachProfile> = listOf(
        CoachProfile(
            id = 1,
            name = "Tahmidur Rahman",
            title = "Spoken Fluency & American Idioms",
            rating = 4.95,
            reviewCount = 124,
            specialty = "Gen Z Slang & Workplace Banter",
            bdtHourly = 450,
            usdHourly = 12,
            bio = "Dhaka native & NYC-based linguistic mentor helping professionals navigate natural conversational American English."
        ),
        CoachProfile(
            id = 2,
            name = "Farhana Yasmin",
            title = "CEFR B1-C1 Prep & Accent Coaching",
            rating = 4.98,
            reviewCount = 210,
            specialty = "Pronunciation & Phonetics",
            bdtHourly = 550,
            usdHourly = 15,
            bio = "Certified TESOL instructor with 8+ years guiding Bangladeshi students to master fluid American rhythms and reduce vocal hesitancy."
        ),
        CoachProfile(
            id = 3,
            name = "Samiul Alam",
            title = "Academic & Corporate Presentation",
            rating = 4.90,
            reviewCount = 88,
            specialty = "Formal Registers & Writing",
            bdtHourly = 400,
            usdHourly = 10,
            bio = "Specialist in transforming stiff textbook translations into crisp, persuasive English for global tech and academic careers."
        )
    )
}
