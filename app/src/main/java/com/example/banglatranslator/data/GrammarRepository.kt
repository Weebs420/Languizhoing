package com.example.banglatranslator.data

import com.example.banglatranslator.model.GrammarComparison
import com.example.banglatranslator.model.GrammarTopic

object GrammarRepository {

    val allTopics: List<GrammarTopic> = listOf(
        GrammarTopic(
            title = "সর্বনাম ও ক্রিয়ার রূপান্তর (Honorifics & Verbs)",
            subtitle = "Formal vs Informal vs Intimate Registers",
            description = "Bangla changes pronouns and verb suffixes strictly based on respect, social distance, and intimacy.",
            comparisons = listOf(
                GrammarComparison(
                    category = "আনুষ্ঠানিক (Formal)",
                    item1Label = "Pronoun",
                    item1Value = "আপনি / আপনারা",
                    item2Label = "Verb (করা)",
                    item2Value = "করেন / করছেন",
                    note = "Used with elders, strangers, superiors, and in official business."
                ),
                GrammarComparison(
                    category = "সাধারণ (Informal)",
                    item1Label = "Pronoun",
                    item1Value = "তুমি / তোমরা",
                    item2Label = "Verb (করা)",
                    item2Value = "করো / করছো",
                    note = "Used with friends, peers, classmates, and familiar associates."
                ),
                GrammarComparison(
                    category = "ঘনিষ্ঠ (Intimate)",
                    item1Label = "Pronoun",
                    item1Value = "তুই / তোরা",
                    item2Label = "Verb (করা)",
                    item2Value = "করিস / করছিস",
                    note = "Reserved for closest childhood friends, younger siblings, or strong emotion."
                )
            ),
            tips = listOf(
                "Never use 'তুই' with anyone older than you or in public offices.",
                "In American English, these honorifics translate via tone and word choice (e.g. 'Would you mind' vs 'Can you' vs 'Do this')."
            )
        ),
        GrammarTopic(
            title = "সাধু বনাম চলিত ভাষা (Sadhu vs. Cholito)",
            subtitle = "Spoken Modern Bangla vs Archival Literary Style",
            description = "Native speakers always use চলিত (Cholito). সাধু (Sadhu) is archaic and only found in historical texts and old legal deeds.",
            comparisons = listOf(
                GrammarComparison(
                    category = "ক্রিয়া (Verbs)",
                    item1Label = "সাধু (Archival)",
                    item1Value = "খাইতেছি, গিয়াছে, করিব",
                    item2Label = "চলিত (Spoken)",
                    item2Value = "খাচ্ছি, গেছে, করব",
                    note = "Always use contracted spoken forms in conversations."
                ),
                GrammarComparison(
                    category = "সর্বনাম (Pronouns)",
                    item1Label = "সাধু (Archival)",
                    item1Value = "তাহারা, উহাদের, কাহাকে",
                    item2Label = "চলিত (Spoken)",
                    item2Value = "তারা, ওদের, কাকে",
                    note = "Simplified spoken pronouns prevent sounding robotic."
                ),
                GrammarComparison(
                    category = "বিশেষ্য (Nouns)",
                    item1Label = "সাধু (Tatsama)",
                    item1Value = "মস্তক, চক্ষু, হস্ত",
                    item2Label = "চলিত (Common)",
                    item2Value = "মাথা, চোখ, হাত",
                    note = "Common tadbhava nouns are standard in day-to-day life."
                )
            ),
            tips = listOf(
                "Mixing Sadhu and Cholito verbs in one sentence is called ‘গুরুচণ্ডালী দোষ’ (linguistic solecism)."
            )
        ),
        GrammarTopic(
            title = "বাংলাদেশি বনাম পশ্চিমবঙ্গীয় বাংলা (Dhaka vs Kolkata)",
            subtitle = "Key Vocabulary Differences Across Borders",
            description = "While mutually intelligible, daily spoken vocabulary differs distinctly between Bangladesh and West Bengal (Kolkata).",
            comparisons = listOf(
                GrammarComparison(
                    category = "Drinking Water",
                    item1Label = "Bangladesh",
                    item1Value = "পানি (Pani)",
                    item2Label = "Kolkata",
                    item2Value = "জল (Jol)",
                    note = "‘পানি’ is standard throughout Bangladesh."
                ),
                GrammarComparison(
                    category = "Bathing / Shower",
                    item1Label = "Bangladesh",
                    item1Value = "গোসল (Goshol)",
                    item2Label = "Kolkata",
                    item2Value = "স্নান (Snan)",
                    note = "Everyday term for taking a shower."
                ),
                GrammarComparison(
                    category = "Invitation",
                    item1Label = "Bangladesh",
                    item1Value = "দাওয়াত (Dawat)",
                    item2Label = "Kolkata",
                    item2Value = "নিমন্ত্রণ (Nimontron)",
                    note = "Standard for weddings, parties, or home feasts."
                ),
                GrammarComparison(
                    category = "Elder Sibling",
                    item1Label = "Bangladesh",
                    item1Value = "ভাইয়া / আপু",
                    item2Label = "Kolkata",
                    item2Value = "দাদা / দিদি",
                    note = "Universal respectful title across Bangladesh."
                ),
                GrammarComparison(
                    category = "Table Salt",
                    item1Label = "Bangladesh",
                    item1Value = "লবণ (Lobon)",
                    item2Label = "Kolkata",
                    item2Value = "নুন (Nun)",
                    note = "Used in markets and home dining."
                )
            ),
            tips = listOf(
                "Bangladeshis widely address shopkeepers and rickshaw drivers affectionately as ‘মামা’ (uncle/bro) or ‘বস’ (boss)."
            )
        ),
        GrammarTopic(
            title = "পদাশ্রিত নির্দেশক ও জোর দেওয়ার অব্যয় (Emphatics & Tags)",
            subtitle = "Definiteness and Nuanced Spoken Tags",
            description = "Native speech relies heavily on enclitic particles for definiteness and emotional weight.",
            comparisons = listOf(
                GrammarComparison(
                    category = "Definiteness (টা / টি)",
                    item1Label = "Indefinite",
                    item1Value = "বই দাও (Give a book)",
                    item2Label = "Definite",
                    item2Value = "বইটা দাও (Give the book)",
                    note = "‘-টা’ specifies a particular item or adds warmth."
                ),
                GrammarComparison(
                    category = "Exclusivity (-ই)",
                    item1Label = "General",
                    item1Value = "আমি যাব (I will go)",
                    item2Label = "Emphatic",
                    item2Value = "আমিই যাব (Only I will go)",
                    note = "‘-ই’ isolates the subject as the sole actor."
                ),
                GrammarComparison(
                    category = "Inclusion (-ও)",
                    item1Label = "General",
                    item1Value = "আমি যাব (I will go)",
                    item2Label = "Inclusive",
                    item2Value = "আমিও যাব (I will also go)",
                    note = "‘-ও’ acts like 'too', 'as well', or 'even'."
                )
            ),
            tips = listOf(
                "Tag questions like ‘তাই না?’ (right?), ‘বুঝলা?’ (got it?), and ‘নাকি?’ (or what?) keep casual conversations flowing naturally."
            )
        )
    )
}
