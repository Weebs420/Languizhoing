package com.example.banglatranslator.data

import com.example.banglatranslator.model.VocabularyEntry

object VocabularyRepository {

    val allEntries: List<VocabularyEntry> = listOf(
        VocabularyEntry(
            id = 1,
            word = "achieve",
            pos = "verb",
            cefr = "A2",
            inOxford3000 = true,
            inOxford5000 = true,
            banglaMeaning = "অর্জন করা, সম্পন্ন করা",
            exampleUs = "She worked hard to achieve her lifelong dream.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/a/ach/achie/achieve__us_1.mp3"
        ),
        VocabularyEntry(
            id = 2,
            word = "clarify",
            pos = "verb",
            cefr = "B1",
            inOxford3000 = true,
            inOxford5000 = true,
            banglaMeaning = "স্পষ্ট করা, পরিষ্কার করে ব্যাখ্যা করা",
            exampleUs = "Could you please clarify your last point?",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/c/cla/clari/clarify__us_1.mp3"
        ),
        VocabularyEntry(
            id = 3,
            word = "collaborate",
            pos = "verb",
            cefr = "B2",
            inOxford3000 = false,
            inOxford5000 = true,
            banglaMeaning = "একত্রে কাজ করা, যৌথভাবে সহযোগিতা করা",
            exampleUs = "The two research teams decided to collaborate on the project.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/c/col/colla/collaborate__us_1.mp3"
        ),
        VocabularyEntry(
            id = 4,
            word = "diligent",
            pos = "adjective",
            cefr = "C1",
            inOxford3000 = false,
            inOxford5000 = true,
            banglaMeaning = "অধ্যবসায়ী, পরিশ্রমী ও যত্নশীল",
            exampleUs = "His diligent efforts finally paid off with high honors.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/d/dil/dilig/diligent__us_1.mp3"
        ),
        VocabularyEntry(
            id = 5,
            word = "everyday",
            pos = "adjective",
            cefr = "A1",
            inOxford3000 = true,
            inOxford5000 = true,
            banglaMeaning = "দৈনন্দিন, নিত্যদিনের",
            exampleUs = "Traffic jams are an everyday nuisance in the city.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/e/eve/every/everyday__us_1.mp3"
        ),
        VocabularyEntry(
            id = 6,
            word = "fluently",
            pos = "adverb",
            cefr = "B1",
            inOxford3000 = true,
            inOxford5000 = true,
            banglaMeaning = "সাবলীলভাবে, অনর্গলভাবে",
            exampleUs = "He speaks both Bangla and English fluently.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/f/flu/fluen/fluently__us_1.mp3"
        ),
        VocabularyEntry(
            id = 7,
            word = "genuine",
            pos = "adjective",
            cefr = "B2",
            inOxford3000 = true,
            inOxford5000 = true,
            banglaMeaning = "খাঁটি, আসল, অকপট",
            exampleUs = "She showed genuine concern for the students.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/g/gen/genui/genuine__us_1.mp3"
        ),
        VocabularyEntry(
            id = 8,
            word = "hesitate",
            pos = "verb",
            cefr = "B1",
            inOxford3000 = true,
            inOxford5000 = true,
            banglaMeaning = "দ্বিধা করা, ইতস্তত করা",
            exampleUs = "Do not hesitate to reach out if you need assistance.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/h/hes/hesit/hesitate__us_1.mp3"
        ),
        VocabularyEntry(
            id = 9,
            word = "illustrate",
            pos = "verb",
            cefr = "B2",
            inOxford3000 = true,
            inOxford5000 = true,
            banglaMeaning = "চিত্রিত করা, দৃষ্টান্ত দিয়ে ব্যাখ্যা করা",
            exampleUs = "Let me use an example to illustrate how this rule works.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/i/ill/illus/illustrate__us_1.mp3"
        ),
        VocabularyEntry(
            id = 10,
            word = "journey",
            pos = "noun",
            cefr = "A2",
            inOxford3000 = true,
            inOxford5000 = true,
            banglaMeaning = "ভ্রমণ, যাত্রা",
            exampleUs = "They embarked on a memorable road journey across the country.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/j/jou/journ/journey__us_1.mp3"
        ),
        VocabularyEntry(
            id = 11,
            word = "knowledge",
            pos = "noun",
            cefr = "A2",
            inOxford3000 = true,
            inOxford5000 = true,
            banglaMeaning = "জ্ঞান, অবগতি, পাণ্ডিত্য",
            exampleUs = "His broad knowledge of linguistics impressed everyone.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/k/kno/knowl/knowledge__us_1.mp3"
        ),
        VocabularyEntry(
            id = 12,
            word = "lucid",
            pos = "adjective",
            cefr = "C1",
            inOxford3000 = false,
            inOxford5000 = true,
            banglaMeaning = "সুস্পষ্ট, সহজে বোধগম্য",
            exampleUs = "The author gave a remarkably lucid explanation of the theory.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/l/luc/lucid/lucid__us_1.mp3"
        ),
        VocabularyEntry(
            id = 13,
            word = "maintain",
            pos = "verb",
            cefr = "B1",
            inOxford3000 = true,
            inOxford5000 = true,
            banglaMeaning = "বজায় রাখা, রক্ষণাবেক্ষণ করা",
            exampleUs = "It is important to maintain consistent daily practice.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/m/mai/maint/maintain__us_1.mp3"
        ),
        VocabularyEntry(
            id = 14,
            word = "nuance",
            pos = "noun",
            cefr = "C1",
            inOxford3000 = false,
            inOxford5000 = true,
            banglaMeaning = "সূক্ষ্ম পার্থক্য, শব্দের ভিন্ন অর্থছায়া",
            exampleUs = "He picked up the subtle nuances of spoken slang very quickly.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/n/nua/nuanc/nuance__us_1.mp3"
        ),
        VocabularyEntry(
            id = 15,
            word = "opportunity",
            pos = "noun",
            cefr = "A2",
            inOxford3000 = true,
            inOxford5000 = true,
            banglaMeaning = "সুযোগ, অনুকূল পরিস্থিতি",
            exampleUs = "Studying abroad presented an invaluable learning opportunity.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/o/opp/oppor/opportunity__us_1.mp3"
        ),
        VocabularyEntry(
            id = 16,
            word = "polite",
            pos = "adjective",
            cefr = "A1",
            inOxford3000 = true,
            inOxford5000 = true,
            banglaMeaning = "ভদ্র, মার্জিত, শিষ্ট",
            exampleUs = "Always maintain a polite tone in formal business communications.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/p/pol/polit/polite__us_1.mp3"
        ),
        VocabularyEntry(
            id = 17,
            word = "qualify",
            pos = "verb",
            cefr = "B1",
            inOxford3000 = true,
            inOxford5000 = true,
            banglaMeaning = "যোগ্যতা অর্জন করা, মানানসই হওয়া",
            exampleUs = "She successfully qualified for the international fellowship.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/q/qua/quali/qualify__us_1.mp3"
        ),
        VocabularyEntry(
            id = 18,
            word = "resilient",
            pos = "adjective",
            cefr = "B2",
            inOxford3000 = false,
            inOxford5000 = true,
            banglaMeaning = "স্থিতিস্থাপক, ঘুরে দাঁড়াতে সক্ষম",
            exampleUs = "The resilient community rebuilt quickly after the monsoon.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/r/res/resil/resilient__us_1.mp3"
        ),
        VocabularyEntry(
            id = 19,
            word = "spontaneous",
            pos = "adjective",
            cefr = "B2",
            inOxford3000 = false,
            inOxford5000 = true,
            banglaMeaning = "স্বতঃস্ফূর্ত, স্বাভাবিক",
            exampleUs = "His spontaneous humor broke the tension in the room.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/s/spo/spont/spontaneous__us_1.mp3"
        ),
        VocabularyEntry(
            id = 20,
            word = "transform",
            pos = "verb",
            cefr = "B1",
            inOxford3000 = true,
            inOxford5000 = true,
            banglaMeaning = "রূপান্তর করা, আমূল পরিবর্তন আনা",
            exampleUs = "Digital tools have transformed how languages are mastered.",
            audioUs = "https://www.oxfordlearnersdictionaries.com/media/english/us_pron/t/tra/trans/transform__us_1.mp3"
        )
    )

    fun filter(cefr: String?, listFilter: String?, search: String): List<VocabularyEntry> {
        val q = search.trim().lowercase()
        return allEntries.filter { entry ->
            val matchCefr = cefr == null || cefr.equals("all", ignoreCase = true) || entry.cefr.equals(cefr, ignoreCase = true)
            val matchList = when (listFilter) {
                "3000" -> entry.inOxford3000
                "5000" -> entry.inOxford5000
                else -> true
            }
            val matchQuery = q.isEmpty() ||
                entry.word.lowercase().contains(q) ||
                entry.banglaMeaning.lowercase().contains(q) ||
                (entry.exampleUs?.lowercase()?.contains(q) == true)
            matchCefr && matchList && matchQuery
        }
    }
}
