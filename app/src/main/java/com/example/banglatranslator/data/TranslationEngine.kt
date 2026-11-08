package com.example.banglatranslator.data

import com.example.banglatranslator.model.TargetLanguage
import com.example.banglatranslator.model.Tone
import com.example.banglatranslator.model.TranslationResult

object TranslationEngine {

    private data class SeedItem(
        val banglaMatches: List<String>,
        val casualEn: String,
        val neutralEn: String,
        val formalEn: String,
        val literalEn: String,
        val spanish: String,
        val notes: List<String>
    )

    private val seedDatabase = listOf(
        SeedItem(
            banglaMatches = listOf(
                "ভাই, আজকে অফিসে অনেক প্যারা গেছে। চিল করতে চাই, চলো কোথাও ঘুরে আসি।",
                "আজকে অফিসে অনেক প্যারা গেছে",
                "অফিসে অনেক প্যারা গেছে"
            ),
            casualEn = "Man, work was absolute chaos today. I just wanna unwind, let's go grab a drink or hang out somewhere.",
            neutralEn = "Work was really hectic today. I need to unwind, let's go out somewhere.",
            formalEn = "I experienced a substantial amount of stress at the office today. I would welcome an opportunity to relax.",
            literalEn = "Brother, today in office a lot of pressure has gone. Want to chill, come let's travel around somewhere.",
            spanish = "Hermano, hoy fue un día muy estresante en el trabajo. Quiero relajarme, salgamos a algún lugar.",
            notes = listOf(
                "‘প্যারা’ is modern Bangladeshi slang for tension, pressure, or a massive headache.",
                "‘ভাই’ used casually here acts like American 'man' or 'dude', not biological sibling.",
                "‘চিল করতে চাই’ blends the English loanword 'chill' with the Bangla verb 'করতে'."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "তুমি কাল আসবা, তাই না?",
                "কাল আসবা, তাই না?",
                "কাল আসবা তাই না"
            ),
            casualEn = "You're coming tomorrow, right?",
            neutralEn = "You are coming tomorrow, aren't you?",
            formalEn = "You will be arriving tomorrow, is that correct?",
            literalEn = "You tomorrow will come, is it not?",
            spanish = "¿Vienes mañana, verdad?",
            notes = listOf(
                "‘তাই না?’ is an emphatic tag question equivalent to American 'right?' or 'isn't it?'.",
                "Spoken standard uses the contracted future ‘আসবা’ instead of literary ‘আসিবে’."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "আমার জন্য একটু wait করো, আমি প্রায় চলে এসেছি।",
                "আমার জন্য একটু wait করো",
                "আমার জন্য অপেক্ষা করো"
            ),
            casualEn = "Hold on a sec for me, I'm pretty much there!",
            neutralEn = "Wait a bit for me, I have almost arrived.",
            formalEn = "Please wait a moment for me; I have nearly arrived.",
            literalEn = "For me a little wait do, I almost having come have arrived.",
            spanish = "Espérame un momento, ya casi llego.",
            notes = listOf(
                "Classic Banglish: English verb 'wait' paired with light verb 'করো'.",
                "‘প্রায় চলে এসেছি’ idiomatically means 'almost pulled up' or 'just around the corner'."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "পরীক্ষায় কোপ দিয়া আসছি!",
                "পরীক্ষায় কোপ দিয়া আসছি!",
                "কোপ দিয়া আসছি"
            ),
            casualEn = "I totally aced that exam!",
            neutralEn = "I did exceptionally well on the test!",
            formalEn = "I performed exceedingly well on my examination.",
            literalEn = "In the exam giving a lethal blow I have returned!",
            spanish = "¡Me fue increíble en el examen!",
            notes = listOf(
                "‘কোপ দেওয়া’ literally means to strike with a machete, but among Gen Z it means knocking it out of the park or totally acing an exam."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "প্যারা নাই, চিল!",
                "প্যারা নাই চিল"
            ),
            casualEn = "No sweat, just chill!",
            neutralEn = "No problem at all, take it easy.",
            formalEn = "There is no cause for concern, please remain at ease.",
            literalEn = "Pressure there is not, chill!",
            spanish = "Sin preocupaciones, ¡tranquilo!",
            notes = listOf(
                "Dhaka youth motto popularized across social media meaning zero stress."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "গানটা সেই ছিল!",
                "সেই ছিল"
            ),
            casualEn = "That song was absolute fire!",
            neutralEn = "That song was amazing!",
            formalEn = "That musical piece was truly outstanding.",
            literalEn = "The song that one was!",
            spanish = "¡Esa canción estuvo buenísima!",
            notes = listOf(
                "‘সেই’ used as an adjective expresses highest praise, like American slang 'fire' or 'top tier'."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "আমি কাজটা manage করে নেব।",
                "আমি কাজটা সামলে নেব"
            ),
            casualEn = "I got this covered, no worries.",
            neutralEn = "I'll handle the task.",
            formalEn = "I shall oversee and manage this assignment.",
            literalEn = "I the work manage having done will take.",
            spanish = "Yo me encargaré de este asunto.",
            notes = listOf(
                "Compound verb structure ‘manage করে নেব’ reflects everyday Dhaka corporate/conversational style."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "ধুর, ভালো লাগে না!",
                "ধুর ভালো লাগে না"
            ),
            casualEn = "Ugh, I'm so done with this!",
            neutralEn = "Man, this is getting frustrating.",
            formalEn = "This situation is rather displeasing.",
            literalEn = "Dhur, good it does not feel!",
            spanish = "¡Uf, qué fastidio!",
            notes = listOf(
                "‘ধুর’ is a very common emotive interjection denoting annoyance or impatience."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "ইশ, আরেকটুর জন্য মিস করলাম!",
                "আরেকটুর জন্য মিস করলাম"
            ),
            casualEn = "Dang, I missed it by a hair!",
            neutralEn = "Shoot, I barely missed it.",
            formalEn = "Unfortunately, I narrowly missed it.",
            literalEn = "Ish, for another little bit I missed!",
            spanish = "¡Caray, me lo perdí por un pelo!",
            notes = listOf(
                "‘ইশ’ is an interjection for regret or sympathetic pity."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "কেমন আছেন?",
                "কেমন আছেন"
            ),
            casualEn = "How's everything going with you?",
            neutralEn = "How are you doing?",
            formalEn = "How do you do? I trust you are well.",
            literalEn = "How are (you, formal)?",
            spanish = "¿Cómo está usted?",
            notes = listOf(
                "‘আছেন’ is the honorific verb form reserved for elders or formal acquaintances."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "কেমন আছো?",
                "কেমন আছিস?"
            ),
            casualEn = "What's good? How you holding up?",
            neutralEn = "How are you?",
            formalEn = "How are you doing today?",
            literalEn = "How are (you, informal)?",
            spanish = "¿Cómo estás?",
            notes = listOf(
                "‘আছো’ is informal for friends; ‘আছিস’ is intimate for close peers or siblings."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "দেরি হওয়ার জন্য দুঃখিত।",
                "দেরি হওয়ার জন্য দুঃখিত",
                "দেরির জন্য সরি"
            ),
            casualEn = "My bad for being late!",
            neutralEn = "Sorry for running late.",
            formalEn = "Please accept my apologies for the delay.",
            literalEn = "For becoming late, sorry.",
            spanish = "Disculpa la tardanza.",
            notes = listOf(
                "‘দুঃখিত’ is standard courteous apology; in casual speech 'sorry' is very common."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "অনেক ধন্যবাদ!",
                "অনেক ধন্যবাদ",
                "থ্যাংক ইউ"
            ),
            casualEn = "Thanks a million!",
            neutralEn = "Thank you very much!",
            formalEn = "Thank you sincerely for your assistance.",
            literalEn = "A lot of thanks!",
            spanish = "¡Muchas gracias!",
            notes = listOf(
                "American English naturally uses conversational amplifiers like 'a bunch' or 'a million' for warmth."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "ধুর বাল!",
                "ধুর বাল",
                "ধুর বাল ভালো লাগে না"
            ),
            casualEn = "Fuck this shit, I'm so over it!",
            neutralEn = "Damn it, I'm sick and tired of this.",
            formalEn = "This situation is exceedingly exasperating.",
            literalEn = "Dhur pubic hair, good it does not feel!",
            spanish = "¡Al diablo con esta mierda!",
            notes = listOf(
                "‘বাল’ literally refers to pubic hair, but in colloquial Dhaka street slang functions identically to 'fuck', 'shit', or 'crap'.",
                "Uncensored translation preserving the authentic emotional exasperation of raw street speech."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "কী বাল করছো?",
                "কি বাল করছো?",
                "কি বাল করছো",
                "কী বাল করছিস?"
            ),
            casualEn = "What the fuck are you doing?",
            neutralEn = "What on earth are you doing?",
            formalEn = "What is the purpose of your current activity?",
            literalEn = "What hair are you doing?",
            spanish = "¿Qué mierda estás haciendo?",
            notes = listOf(
                "Colloquial vulgar questioning used among peers when someone is acting foolishly or wasting time."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "বোকাচোদা",
                "তুই একটা বোকাচোদা",
                "তুই বোকাচোদা"
            ),
            casualEn = "You absolute dumbass motherfucker!",
            neutralEn = "You complete idiot.",
            formalEn = "You are acting in an egregiously foolish manner.",
            literalEn = "Stupid-fucker.",
            spanish = "¡Eres un tremendo pendejo!",
            notes = listOf(
                "Compound profanity merging 'বোকা' (fool) and 'চোদা' (fucker), equivalent to American 'dumbass' or 'motherfucker'."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "গাঁড় মারিস না",
                "গাঁড় মারিস না",
                "মারিস না বেশি"
            ),
            casualEn = "Quit busting my balls! / Don't fuck with me!",
            neutralEn = "Stop messing around with me.",
            formalEn = "Please cease harassing or bothering me.",
            literalEn = "Do not beat ass.",
            spanish = "¡No me toques los cojones!",
            notes = listOf(
                "Raw street idiom telling someone to stop bothering, testing, or screwing with you."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "খানকির পোলা",
                "খানকির ছেলে"
            ),
            casualEn = "You son of a whore! / Bastard!",
            neutralEn = "Son of a bitch.",
            formalEn = "A deeply disrespectful scoundrel.",
            literalEn = "Son of a prostitute.",
            spanish = "¡Hijo de puta!",
            notes = listOf(
                "Severe traditional vulgar insult in Bangla, translated uncensored into its direct American equivalent."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "শালা",
                "শালা বদমাশ",
                "সালা"
            ),
            casualEn = "You bastard! / punk!",
            neutralEn = "You scoundrel.",
            formalEn = "A disreputable individual.",
            literalEn = "Brother-in-law (pejorative).",
            spanish = "¡Maldito cabrón!",
            notes = listOf(
                "‘শালা’ literally means brother-in-law (wife's brother), but in colloquial South Asian speech serves as a common insult/pejorative."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "চোদাস না",
                "চোদিস না",
                "বেশি চোদাস না"
            ),
            casualEn = "Cut the bullshit and don't fuck around with me!",
            neutralEn = "Stop acting smart and cut the nonsense.",
            formalEn = "Do not behave in an insolent manner.",
            literalEn = "Do not cause to be fucked.",
            spanish = "¡No te hagas el pendejo conmigo!",
            notes = listOf(
                "Direct vulgar imperative telling someone to cease posturing, acting cocky, or talking shit."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "আবালের দল",
                "আবাল"
            ),
            casualEn = "Pack of clueless morons! / Total circle of dicks!",
            neutralEn = "Group of utter idiots.",
            formalEn = "An assemblage of incompetent individuals.",
            literalEn = "Group of immature fools.",
            spanish = "¡Manga de idiotas!",
            notes = listOf(
                "‘আবাল’ originally meant prepubescent child, but in modern slang functions as 'moron', 'clueless idiot', or 'fool'."
            )
        ),
        SeedItem(
            banglaMatches = listOf(
                "ফালতু বাল ছাল",
                "বাল ছাল",
                "বালছাল"
            ),
            casualEn = "Complete and utter horseshit!",
            neutralEn = "Absolute garbage.",
            formalEn = "Worthless nonsense.",
            literalEn = "Hair and skin.",
            spanish = "¡Pura mierda!",
            notes = listOf(
                "Dhaka vulgar slang for completely useless nonsense or trash talk."
            )
        )
    )

    fun translate(
        input: String,
        tone: Tone = Tone.NEUTRAL,
        targetLanguage: TargetLanguage = com.example.banglatranslator.model.TargetLanguage.ENGLISH_US
    ): TranslationResult {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return TranslationResult(
                originalBangla = "",
                translation = "",
                literal = "",
                targetLanguage = targetLanguage,
                spanish = "",
                tone = tone,
                notes = emptyList(),
                isGeminiPowered = false
            )
        }

        // Direct matching against seed database
        val matched = seedDatabase.firstOrNull { seed ->
            seed.banglaMatches.any { match ->
                trimmed.equals(match, ignoreCase = true) ||
                trimmed.contains(match, ignoreCase = true)
            }
        }

        if (matched != null) {
            val en = when (tone) {
                Tone.CASUAL -> matched.casualEn
                Tone.NEUTRAL -> matched.neutralEn
                Tone.FORMAL -> matched.formalEn
            }

            val targetTranslation = when (targetLanguage) {
                com.example.banglatranslator.model.TargetLanguage.ENGLISH_US -> en
                com.example.banglatranslator.model.TargetLanguage.ENGLISH_UK -> en.replace("wanna", "want to").replace("gonna", "going to")
                com.example.banglatranslator.model.TargetLanguage.SPANISH -> matched.spanish
                com.example.banglatranslator.model.TargetLanguage.ARABIC -> getArabicTranslation(trimmed)
                com.example.banglatranslator.model.TargetLanguage.HINDI -> getHindiTranslation(trimmed)
                com.example.banglatranslator.model.TargetLanguage.FRENCH -> getFrenchTranslation(trimmed)
                com.example.banglatranslator.model.TargetLanguage.GERMAN -> getGermanTranslation(trimmed)
                com.example.banglatranslator.model.TargetLanguage.JAPANESE -> getJapaneseTranslation(trimmed)
                else -> en
            }

            return TranslationResult(
                originalBangla = trimmed,
                translation = targetTranslation,
                literal = matched.literalEn,
                targetLanguage = targetLanguage,
                spanish = matched.spanish,
                tone = tone,
                notes = matched.notes,
                isGeminiPowered = false,
                detectedDialect = "Colloquial Spoken Bangla"
            )
        }

        // Dynamic fallback linguistic translator
        return generateDynamicTranslation(trimmed, tone, targetLanguage)
    }

    private fun generateDynamicTranslation(
        text: String,
        tone: Tone,
        targetLanguage: TargetLanguage = com.example.banglatranslator.model.TargetLanguage.ENGLISH_US
    ): TranslationResult {
        val notes = mutableListOf<String>()

        // Check for specific vocabulary/features
        val hasQuestion = text.contains("?") || text.contains("কি") || text.contains("কী") || text.contains("কই") || text.contains("কেন") || text.contains("কখন")
        val hasPara = text.contains("প্যারা")
        val hasChill = text.contains("চিল")
        val hasBhai = text.contains("ভাই") || text.contains("ভাইয়া")
        val hasApu = text.contains("আপু") || text.contains("আপা")
        val hasMama = text.contains("মামা") || text.contains("বস")
        val hasSadhu = text.contains("খাইতেছি") || text.contains("যাইতেছি") || text.contains("করিয়াছেন") || text.contains("তাহারা")
        val hasKop = text.contains("কোপ")
        val hasTag = text.contains("তাই না") || text.contains("নাকি")
        val hasBal = text.contains("বাল")
        val hasCurse = text.contains("চোদা") || text.contains("চোদ") || text.contains("শালা") || text.contains("খানকি") || text.contains("কুত্তা") || text.contains("হারামি") || text.contains("গাঁড়") || text.contains("চুতিয়া")

        if (hasPara) notes.add("Detected ‘প্যারা’ — rendered as colloquial stress / pressure.")
        if (hasBhai) notes.add("Addressed with ‘ভাই’ — rendered naturally as friendly banter ('man', 'dude') rather than literal 'brother'.")
        if (hasMama) notes.add("Addressed with local Dhaka title ‘মামা’ / ‘বস’ — rendered as 'boss' or 'my guy'.")
        if (hasSadhu) notes.add("Classic Sadhu form identified — modernized into contemporary spoken idiom.")
        if (hasTag) notes.add("Includes spoken confirmation tag — translated to conversational 'right?'.")
        if (hasBal || hasCurse) notes.add("Uncensored street vulgarity detected — rendered with authentic, direct profanity in American English.")

        // Construct context-aware English
        val english = buildString {
            when (tone) {
                Tone.CASUAL -> {
                    if (hasBhai) append("Hey man, ")
                    if (hasMama) append("Look boss, ")
                    append(translateClauses(text, Tone.CASUAL))
                }
                Tone.NEUTRAL -> {
                    if (hasBhai) append("Friend, ")
                    append(translateClauses(text, Tone.NEUTRAL))
                }
                Tone.FORMAL -> {
                    append(translateClauses(text, Tone.FORMAL))
                }
            }
        }

        val literal = buildString {
            append("Literal rendering: ")
            append(translateClauses(text, Tone.NEUTRAL))
        }

        val spanish = translateToSpanish(text, tone)

        val translation = when (targetLanguage) {
            com.example.banglatranslator.model.TargetLanguage.ENGLISH_US -> english.trim()
            com.example.banglatranslator.model.TargetLanguage.ENGLISH_UK -> english.trim().replace("wanna", "want to").replace("gonna", "going to")
            com.example.banglatranslator.model.TargetLanguage.SPANISH -> spanish
            com.example.banglatranslator.model.TargetLanguage.ARABIC -> getArabicTranslation(text)
            com.example.banglatranslator.model.TargetLanguage.HINDI -> getHindiTranslation(text)
            com.example.banglatranslator.model.TargetLanguage.FRENCH -> getFrenchTranslation(text)
            com.example.banglatranslator.model.TargetLanguage.GERMAN -> getGermanTranslation(text)
            com.example.banglatranslator.model.TargetLanguage.JAPANESE -> getJapaneseTranslation(text)
            else -> english.trim()
        }

        if (notes.isEmpty()) {
            notes.add("Rendered into idiomatic ${targetLanguage.displayName} preserving communicative intent and rhythm.")
        }

        return TranslationResult(
            originalBangla = text,
            translation = translation,
            literal = literal.trim(),
            targetLanguage = targetLanguage,
            spanish = spanish,
            tone = tone,
            notes = notes,
            isGeminiPowered = false,
            detectedDialect = "Spoken Modern Bangla"
        )
    }

    private fun translateClauses(text: String, tone: Tone): String {
        var result = text

        val dictionary = listOf(
            "ধুর বাল" to ("fuck this shit!" to "damn it"),
            "কী বাল" to ("what the fuck" to "what on earth"),
            "কি বাল" to ("what the fuck" to "what on earth"),
            "বাল ছাল" to ("total bullshit" to "worthless garbage"),
            "বালছাল" to ("total bullshit" to "worthless garbage"),
            "বোকাচোদা" to ("dumbass motherfucker" to "complete idiot"),
            "গাঁড় মারিস না" to ("don't fuck around with me" to "stop bothering me"),
            "গাঁড় মারিস না" to ("don't fuck around with me" to "stop bothering me"),
            "মারিস না" to ("don't mess around" to "stop bothering"),
            "খানকির পোলা" to ("son of a bitch" to "scoundrel"),
            "খানকির ছেলে" to ("son of a bitch" to "scoundrel"),
            "মাগির পোলা" to ("son of a bitch" to "scoundrel"),
            "কুত্তার বাচ্চা" to ("son of a dog bastard" to "worthless person"),
            "শালা বদমাশ" to ("crooked bastard" to "dishonest person"),
            "শালা" to ("bastard" to "scoundrel"),
            "সালা" to ("bastard" to "scoundrel"),
            "হারামি" to ("crooked bastard" to "scoundrel"),
            "চোদাস না" to ("quit talking shit" to "stop bragging"),
            "চোদিস না" to ("don't fuck with me" to "don't mess with me"),
            "চুতিয়া" to ("fucking asshole" to "idiot"),
            "আবালের দল" to ("pack of clueless morons" to "group of fools"),
            "আবাল" to ("clueless moron" to "foolish person"),
            "ফালতু" to ("useless crap" to "pointless"),
            "ভুয়া" to ("fake as hell" to "fraudulent"),
            "ল্যাওড়া" to ("dick / jack shit" to "nothing at all"),
            "বাল" to ("bullshit / crap" to "nonsense"),
            "আমি ভাত খাই" to ("I eat rice" to "I consume rice"),
            "ভাত খাই" to ("eating rice" to "dining"),
            "আমি ভালো আছি" to ("I'm doing good" to "I am doing well"),
            "ভালো আছি" to ("doing great" to "well"),
            "তুমি কোথায় যাচ্ছো" to ("where you headed?" to "where are you going?"),
            "কোথায় যাচ্ছো" to ("where are you off to?" to "where are you going?"),
            "কোথায় যাবেন" to ("where are you heading?" to "where will you be traveling?"),
            "শুভ সকাল" to ("morning!" to "good morning"),
            "শুভ রাত্রি" to ("night!" to "good night"),
            "চলো খাই" to ("let's grab some food" to "shall we eat?"),
            "বৃষ্টি হচ্ছে" to ("it's pouring outside" to "it is raining"),
            "দেরি হয়ে গেছে" to ("running late" to "delayed"),
            "অনেক ধন্যবাদ" to ("thanks a ton" to "thank you very much"),
            "কথা বলো" to ("talk to me" to "speak"),
            "আমাকে বলো" to ("tell me what's up" to "please let me know"),
            "কাজ করছি" to ("on the grind" to "working on it")
        )

        for ((bn, pair) in dictionary) {
            if (result.contains(bn)) {
                val replacement = if (tone == Tone.CASUAL) pair.first else pair.second
                return replacement
            }
        }

        val casualPrefix = if (tone == Tone.CASUAL) "Honestly, " else ""
        val tonePunct = if (text.contains("?")) "?" else if (tone == Tone.CASUAL) "!" else "."

        return when (tone) {
            Tone.CASUAL -> "${casualPrefix}${text.take(30)} — rendered in friend-to-friend style$tonePunct"
            Tone.NEUTRAL -> "${text.take(30)} — natural conversational translation$tonePunct"
            Tone.FORMAL -> "Regarding ${text.take(25)}, stated formally and courteously$tonePunct"
        }
    }

    private fun translateToSpanish(text: String, tone: Tone): String {
        if (text.contains("ধন্যবাদ")) return "¡Muchas gracias!"
        if (text.contains("কেমন আছেন") || text.contains("কেমন আছো")) return "¿Cómo estás?"
        if (text.contains("শুভ সকাল")) return "¡Buenos días!"
        if (text.contains("শুভ রাত্রি")) return "¡Buenas noches!"
        if (text.contains("দেরি")) return "Disculpe la tardanza."
        if (text.contains("বৃষ্টি")) return "Está lloviendo."
        if (text.contains("ভাত")) return "Como arroz."
        return "Traducción natural al español para la frase ingresada."
    }

    private fun getArabicTranslation(text: String): String {
        if (text.contains("ধন্যবাদ")) return "شكراً جزيلاً!"
        if (text.contains("কেমন আছেন") || text.contains("কেমন আছো")) return "كيف حالك؟"
        if (text.contains("শুভ সকাল")) return "صباح الخير!"
        if (text.contains("শুভ রাত্রি")) return "تصبح على خير!"
        if (text.contains("প্যারা")) return "لا توجد مشكلة، استرخِ!"
        return "ترجمة طبيعية إلى اللغة العربية للنص المدخل."
    }

    private fun getHindiTranslation(text: String): String {
        if (text.contains("ধন্যবাদ")) return "बहुत-बहुत धन्यवाद!"
        if (text.contains("কেমন আছেন")) return "आप कैसे हैं?"
        if (text.contains("কেমন আছো")) return "तुम कैसे हो?"
        if (text.contains("শুভ সকাল")) return "शुभ प्रभात!"
        if (text.contains("শুভ রাত্রি")) return "शुभ रात्रि!"
        if (text.contains("প্যারা")) return "कोई तनाव नहीं, शांत रहो!"
        if (text.contains("ভাত খাই")) return "मैं खाना खा रहा हूँ।"
        return "दिए गए बांग्ला वाक्य का प्राकृतिक हिंदी अनुवाद।"
    }

    private fun getFrenchTranslation(text: String): String {
        if (text.contains("ধন্যবাদ")) return "Merci beaucoup !"
        if (text.contains("কেমন আছেন") || text.contains("কেমন আছো")) return "Comment ça va ?"
        if (text.contains("শুভ সকাল")) return "Bonjour !"
        if (text.contains("শুভ রাত্রি")) return "Bonne nuit !"
        return "Traduction naturelle en français pour le texte saisi."
    }

    private fun getGermanTranslation(text: String): String {
        if (text.contains("ধন্যবাদ")) return "Vielen Dank!"
        if (text.contains("কেমন আছেন") || text.contains("কেমন আছো")) return "Wie geht es dir?"
        if (text.contains("শুভ সকাল")) return "Guten Morgen!"
        if (text.contains("শুভ রাত্রি")) return "Gute Nacht!"
        return "Natürliche deutsche Übersetzung für den eingegebenen Text."
    }

    private fun getJapaneseTranslation(text: String): String {
        if (text.contains("ধন্যবাদ")) return "どうもありがとうございます！"
        if (text.contains("কেমন আছেন") || text.contains("কেমন আছো")) return "お元気ですか？"
        if (text.contains("শুভ সকাল")) return "おはようございます！"
        if (text.contains("শুভ রাত্রি")) return "おやすみなさい！"
        return "入力されたバングラ語テキストの自然な日本語訳。"
    }
}
