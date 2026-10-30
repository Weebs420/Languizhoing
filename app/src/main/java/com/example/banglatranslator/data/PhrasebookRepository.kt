package com.example.banglatranslator.data

import com.example.banglatranslator.model.PhrasebookEntry

object PhrasebookRepository {

    val allEntries: List<PhrasebookEntry> = listOf(
        PhrasebookEntry(
            id = 1,
            bangla = "প্যারা নাই, চিল!",
            english = "No stress, just chill!",
            category = "genz",
            example = "দোস্ত, রেজাল্ট যাই হোক, প্যারা নাই, চিল!",
            note = "Quintessential Dhaka Gen Z catchphrase."
        ),
        PhrasebookEntry(
            id = 2,
            bangla = "পরীক্ষায় কোপ দিয়া আসছি!",
            english = "I totally aced that exam!",
            category = "genz",
            example = "ভাই প্রশ্ন এত সহজ ছিল, একবারে কোপ দিয়া আসছি।",
            note = "Metaphorical use of 'কোপ' meaning killing it or crushing it."
        ),
        PhrasebookEntry(
            id = 3,
            bangla = "গানটা সেই ছিল!",
            english = "That song was absolute fire!",
            category = "genz",
            example = "কনসার্টে শেষ ট্র্যাকটা পুরা সেই ছিল রে ভাই।",
            note = "‘সেই’ expresses superlative slang praise."
        ),
        PhrasebookEntry(
            id = 4,
            bangla = "ছ্যাঁকা খাওয়া",
            english = "To get dumped / heartbreak",
            category = "slang",
            example = "ফার্স্ট ইয়ারে ও একটা মারাত্মক ছ্যাঁকা খেয়েছিল।",
            note = "Literally getting scorched; idiom for rejection in love."
        ),
        PhrasebookEntry(
            id = 5,
            bangla = "অস্থির পরিবেশ",
            english = "Incredible vibes / awesome atmosphere",
            category = "slang",
            example = "আজকের ক্যাফেটার পরিবেশটা সত্যি অস্থির।",
            note = "‘অস্থির’ literally means restless, but colloquially means electrifying/awesome."
        ),
        PhrasebookEntry(
            id = 6,
            bangla = "ঘোল খাওয়ানো",
            english = "To take someone for a ride / bamboozle",
            category = "idiom",
            example = "দালালটা ওকে ভালোমতো ঘোল খাইয়ে টাকা নিয়ে গেল।",
            note = "Traditional idiom for deceiving or running circles around someone."
        ),
        PhrasebookEntry(
            id = 7,
            bangla = "আকাশ কুসুম কল্পনা",
            english = "Building castles in the air / pipe dream",
            category = "idiom",
            example = "কোনো কাজ না করে লটারি জেতার আশা আকাশ কুসুম কল্পনা।",
            note = "Literally 'sky flower' fantasy."
        ),
        PhrasebookEntry(
            id = 8,
            bangla = "ডুমুরের ফুল",
            english = "Rare as hen's teeth / once in a blue moon",
            category = "idiom",
            example = "তুমি তো ডুমুরের ফুল হয়ে গেলে, দেখাই মেলে না!",
            note = "Figs flower internally, so a visible fig flower is an impossibility."
        ),
        PhrasebookEntry(
            id = 9,
            bangla = "কাজটা manage করে নেব",
            english = "I'll handle it / take care of it",
            category = "banglish",
            example = "কোনো সমস্যা নেই, বস, আমি প্রেজেন্টেশনটা manage করে নেব।",
            note = "English loan verb 'manage' seamlessly combined with 'করে নেওয়া'."
        ),
        PhrasebookEntry(
            id = 10,
            bangla = "আমার জন্য একটু wait করো",
            english = "Hold on a sec for me",
            category = "banglish",
            example = "আমি ট্রাফিকে আটকা, আমার জন্য পাঁচ মিনিট wait করো।",
            note = "Everyday spoken Banglish."
        ),
        PhrasebookEntry(
            id = 11,
            bangla = "ব্যাপারটা খুব confusing",
            english = "The whole thing is super confusing",
            category = "banglish",
            example = "নতুন সফটওয়্যারের ইন্টারফেসটা আসলেই খুব confusing।",
            note = "Adjective loanword common in youth dialogue."
        ),
        PhrasebookEntry(
            id = 12,
            bangla = "খাইসো নি?",
            english = "Did you eat? (Dhakaiya)",
            category = "regional",
            example = "মামা, দুপুরের খানা খাইসো নি?",
            note = "Old Dhaka regional dialect question for 'খেয়েছো?'"
        ),
        PhrasebookEntry(
            id = 13,
            bangla = "ক্যান আছুন?",
            english = "How are you? (Chatgaiya)",
            category = "regional",
            example = "ও ভাই, অনারা বেগ্গুন ক্যান আছুন?",
            note = "Chittagong regional dialect greeting."
        ),
        PhrasebookEntry(
            id = 14,
            bangla = "বালা নি?",
            english = "Are you doing well? (Sylheti)",
            category = "regional",
            example = "কিতা খবর ভাই, বালা নি?",
            note = "Sylhet regional greeting equivalent to 'ভালো আছেন?'"
        ),
        PhrasebookEntry(
            id = 15,
            bangla = "মানে...",
            english = "I mean... / Like...",
            category = "filler",
            example = "মানে, আমি বলতে চাচ্ছিলাম যে কালকে যাব।",
            note = "Conversational pause particle to organize thoughts."
        ),
        PhrasebookEntry(
            id = 16,
            bangla = "আসলে...",
            english = "Actually... / The truth is...",
            category = "filler",
            example = "আসলে আমি মিটিংয়ের সময়টা ভুলে গিয়েছিলাম।",
            note = "Soft opener before explaining a situation."
        ),
        PhrasebookEntry(
            id = 17,
            bangla = "ধুর!",
            english = "Ugh! / Shoot!",
            category = "interjection",
            example = "ধুর! বাসটা আবার মিস হয়ে গেল!",
            note = "Spoken interjection expressing mild annoyance."
        ),
        PhrasebookEntry(
            id = 18,
            bangla = "ইশ!",
            english = "Ouch! / Dang it!",
            category = "interjection",
            example = "ইশ! গরম চায়ে জিহ্বা পুড়ে গেল!",
            note = "Interjection for sharp pain or empathy."
        ),
        PhrasebookEntry(
            id = 19,
            bangla = "আরে!",
            english = "Whoa! / Hey there!",
            category = "interjection",
            example = "আরে! তুমি হঠাৎ এখানে কোথা থেকে?",
            note = "Surprise or delighted greeting."
        ),
        PhrasebookEntry(
            id = 20,
            bangla = "চোখের বালি",
            english = "An eyesore / thorn in the flesh",
            category = "idiom",
            example = "ওর অহংকারের জন্য ও পুরো ক্লাসের চোখের বালি হয়ে দাঁড়িয়েছে।",
            note = "Classic idiom immortalized by Rabindranath Tagore."
        ),
        PhrasebookEntry(
            id = 21,
            bangla = "তেল মারা",
            english = "To butter someone up / kiss up",
            category = "slang",
            example = "বসকে তেল মেরে প্রমোশন পাওয়ার চেষ্টা করো না।",
            note = "Literally applying oil; slang for flattering superiors."
        ),
        PhrasebookEntry(
            id = 22,
            bangla = "মাথায় হাত দেওয়া",
            english = "At one's wit's end / devastated",
            category = "idiom",
            example = "সব সঞ্চয় হারিয়ে ব্যবসায়ী মাথায় হাত দিয়ে বসে পড়লেন।",
            note = "Gesture of placing hand on forehead in despair."
        ),
        PhrasebookEntry(
            id = 23,
            bangla = "প্যাঁচ লাগানো",
            english = "To stir the pot / overcomplicate things",
            category = "slang",
            example = "সোজা কথায় প্যাঁচ না লাগিয়ে আসল ঘটনা বলো।",
            note = "Twisting matters or creating misunderstandings."
        ),
        PhrasebookEntry(
            id = 24,
            bangla = "হাঁটুজল",
            english = "Knee-deep water",
            category = "regional",
            example = "বর্ষায় ঢাকার রাস্তায় হাঁটুজল জমে যায়।",
            note = "Descriptive compound for monsoon waterlogging."
        )
    )

    fun filter(category: String?, query: String): List<PhrasebookEntry> {
        val q = query.trim().lowercase()
        return allEntries.filter { entry ->
            val matchCategory = category == null || category.equals("all", ignoreCase = true) || entry.category.equals(category, ignoreCase = true)
            val matchQuery = q.isEmpty() ||
                entry.bangla.lowercase().contains(q) ||
                entry.english.lowercase().contains(q) ||
                (entry.example?.lowercase()?.contains(q) == true) ||
                (entry.note?.lowercase()?.contains(q) == true)
            matchCategory && matchQuery
        }
    }
}
