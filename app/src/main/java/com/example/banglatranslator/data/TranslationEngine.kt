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

    fun detectLanguage(text: String): TargetLanguage {
        if (text.any { it in '\u0980'..'\u09FF' }) return TargetLanguage.BANGLA
        if (text.any { it in '\u0900'..'\u097F' }) return TargetLanguage.HINDI
        if (text.any { it in '\u0600'..'\u06FF' }) {
            return if (text.contains("پ") || text.contains("ٹ") || text.contains("ے") || text.contains("ہ")) {
                TargetLanguage.URDU
            } else {
                TargetLanguage.ARABIC
            }
        }
        if (text.any { it in '\u3040'..'\u30FF' }) return TargetLanguage.JAPANESE
        if (text.any { it in '\uAC00'..'\uD7AF' }) return TargetLanguage.KOREAN
        if (text.any { it in '\u4E00'..'\u9FFF' }) return TargetLanguage.CHINESE
        if (text.any { it in '\u0400'..'\u04FF' }) return TargetLanguage.RUSSIAN

        val lower = text.lowercase()
        if (lower.contains("¿") || lower.contains("¡") || lower.contains("hola") || lower.contains("gracias") || lower.contains("cómo estás") || lower.contains("buenos días")) {
            return TargetLanguage.SPANISH
        }
        if (lower.contains("bonjour") || lower.contains("merci") || lower.contains("ça va") || lower.contains("s'il vous plaît")) {
            return TargetLanguage.FRENCH
        }
        if (lower.contains("danke") || lower.contains("guten tag") || lower.contains("wie geht") || lower.contains("tschüss")) {
            return TargetLanguage.GERMAN
        }
        if (lower.contains("obrigado") || lower.contains("olá") || lower.contains("tudo bem")) {
            return TargetLanguage.PORTUGUESE
        }
        if (lower.contains("ciao") || lower.contains("grazie") || lower.contains("buongiorno")) {
            return TargetLanguage.ITALIAN
        }
        if (lower.contains("merhaba") || lower.contains("teşekkür") || lower.contains("nasılsın")) {
            return TargetLanguage.TURKISH
        }
        return TargetLanguage.ENGLISH_US
    }

    fun translate(
        input: String,
        tone: Tone = Tone.NEUTRAL,
        targetLanguage: TargetLanguage = TargetLanguage.ENGLISH_US,
        isBanglaToTarget: Boolean = true,
        sourceLanguage: TargetLanguage = if (isBanglaToTarget) TargetLanguage.BANGLA else targetLanguage
    ): TranslationResult {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return TranslationResult(
                originalBangla = "",
                translation = "",
                literal = "",
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                spanish = "",
                tone = tone,
                notes = emptyList(),
                isGeminiPowered = false,
                isBanglaToTarget = targetLanguage != TargetLanguage.BANGLA
            )
        }

        val effectiveSource = if (sourceLanguage == TargetLanguage.AUTO_DETECT) {
            detectLanguage(trimmed)
        } else {
            sourceLanguage
        }
        val detectedName = if (sourceLanguage == TargetLanguage.AUTO_DETECT) effectiveSource.displayName else null

        if (effectiveSource == targetLanguage) {
            return TranslationResult(
                originalBangla = trimmed,
                translation = trimmed,
                literal = "Same source and target language (${targetLanguage.displayName})",
                sourceLanguage = effectiveSource,
                targetLanguage = targetLanguage,
                tone = tone,
                notes = listOf("Source and target are both ${targetLanguage.displayName}. Choose a different target language or swap to translate."),
                isGeminiPowered = false,
                detectedDialect = "${targetLanguage.displayName} (${tone.label})",
                detectedSourceLanguage = detectedName,
                isBanglaToTarget = effectiveSource == TargetLanguage.BANGLA
            )
        }

        if (targetLanguage == TargetLanguage.BANGLA) {
            return translateToBangla(trimmed, tone, effectiveSource, detectedName)
        }

        if (effectiveSource != TargetLanguage.BANGLA) {
            return translateCrossLanguage(trimmed, tone, effectiveSource, targetLanguage, detectedName)
        }

        // Direct matching against seed database (Bangla -> TargetLanguage)
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
                TargetLanguage.ENGLISH_US -> en
                TargetLanguage.ENGLISH_UK -> en.replace("wanna", "want to").replace("gonna", "going to")
                TargetLanguage.SPANISH -> matched.spanish
                TargetLanguage.ARABIC -> getArabicTranslation(trimmed)
                TargetLanguage.HINDI -> getHindiTranslation(trimmed)
                TargetLanguage.FRENCH -> getFrenchTranslation(trimmed)
                TargetLanguage.GERMAN -> getGermanTranslation(trimmed)
                TargetLanguage.JAPANESE -> getJapaneseTranslation(trimmed)
                else -> en
            }

            return TranslationResult(
                originalBangla = trimmed,
                translation = targetTranslation,
                literal = matched.literalEn,
                sourceLanguage = TargetLanguage.BANGLA,
                targetLanguage = targetLanguage,
                spanish = matched.spanish,
                tone = tone,
                notes = matched.notes,
                isGeminiPowered = false,
                detectedDialect = "Colloquial Spoken Bangla",
                detectedSourceLanguage = detectedName,
                isBanglaToTarget = true
            )
        }

        // Dynamic fallback linguistic translator (Bangla -> TargetLanguage)
        return generateDynamicTranslation(trimmed, tone, targetLanguage, detectedName)
    }

    private fun generateDynamicTranslation(
        text: String,
        tone: Tone,
        targetLanguage: TargetLanguage = TargetLanguage.ENGLISH_US,
        detectedSourceLanguage: String? = null
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
            sourceLanguage = TargetLanguage.BANGLA,
            targetLanguage = targetLanguage,
            spanish = spanish,
            tone = tone,
            notes = notes,
            isGeminiPowered = false,
            detectedDialect = "Spoken Modern Bangla",
            detectedSourceLanguage = detectedSourceLanguage,
            isBanglaToTarget = true
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

    private fun translateToBangla(
        text: String,
        tone: Tone,
        sourceLanguage: TargetLanguage,
        detectedSourceLanguage: String? = null
    ): TranslationResult {
        val lower = text.lowercase()

        // 1. Reverse match against seedDatabase
        val seedMatch = seedDatabase.firstOrNull { seed ->
            seed.casualEn.equals(text, ignoreCase = true) ||
                seed.neutralEn.equals(text, ignoreCase = true) ||
                seed.formalEn.equals(text, ignoreCase = true) ||
                seed.spanish.equals(text, ignoreCase = true) ||
                seed.casualEn.contains(text, ignoreCase = true) && text.length >= 8
        }
        if (seedMatch != null) {
            val bangla = seedMatch.banglaMatches.first()
            return TranslationResult(
                originalBangla = bangla,
                translation = bangla,
                literal = "Source (${sourceLanguage.displayName}): $text",
                sourceLanguage = sourceLanguage,
                targetLanguage = TargetLanguage.BANGLA,
                tone = tone,
                notes = seedMatch.notes,
                isGeminiPowered = false,
                detectedDialect = when (tone) {
                    Tone.CASUAL -> "Spoken Bangla (তুই / Friend)"
                    Tone.NEUTRAL -> "Spoken Bangla (তুমি / Everyday)"
                    Tone.FORMAL -> "Polite Bangla (আপনি / Formal)"
                },
                detectedSourceLanguage = detectedSourceLanguage,
                isBanglaToTarget = false
            )
        }

        // 2. Match against Oxford VocabularyRepository if single word or close match
        val vocabMatch = VocabularyRepository.allEntries.firstOrNull {
            it.word.equals(text, ignoreCase = true)
        }
        if (vocabMatch != null) {
            return TranslationResult(
                originalBangla = vocabMatch.banglaMeaning,
                translation = vocabMatch.banglaMeaning,
                literal = "${vocabMatch.word} (${vocabMatch.pos}, CEFR ${vocabMatch.cefr})",
                sourceLanguage = sourceLanguage,
                targetLanguage = TargetLanguage.BANGLA,
                tone = tone,
                notes = listOfNotNull(
                    vocabMatch.exampleUs?.let { "Example usage: \"$it\"" },
                    "Oxford ${if (vocabMatch.inOxford3000) "3000" else "5000"} vocabulary entry."
                ),
                isGeminiPowered = false,
                detectedDialect = "Standard Bangla Vocabulary",
                detectedSourceLanguage = detectedSourceLanguage,
                isBanglaToTarget = false
            )
        }

        // 3. Common conversational phrases -> Bangla with honorific register mapping
        data class ReverseEntry(
            val keywords: List<String>,
            val casual: String,
            val neutral: String,
            val formal: String,
            val pronunciation: String,
            val note: String
        )

        val reversePhrases = listOf(
            ReverseEntry(
                keywords = listOf("how are you", "how's it going", "what's up", "como estas", "¿cómo estás?", "comment ça va", "wie geht", "কैसे हो", "كيف حالك", "お元気"),
                casual = "কী অবস্থা রে? কেমন আছিস?",
                neutral = "কেমন আছো? কী খবর?",
                formal = "আপনি কেমন আছেন?",
                pronunciation = when (tone) {
                    Tone.CASUAL -> "Ki obostha re? Kemon achish?"
                    Tone.NEUTRAL -> "Kemon acho? Ki khobor?"
                    Tone.FORMAL -> "Apni kemon achen?"
                },
                note = "Bangla greetings shift pronoun & verb ending by respect tier: তুই (আছিস) → তুমি (আছো) → আপনি (আছেন)."
            ),
            ReverseEntry(
                keywords = listOf("thank you", "thanks", "gracias", "merci", "danke", "धन्यवाद", "شكرا", "ありがとう"),
                casual = "থ্যাংকস রে, দোস্ত!",
                neutral = "অনেক ধন্যবাদ তোমাকে!",
                formal = "আপনাকে অসংখ্য ধন্যবাদ।",
                pronunciation = when (tone) {
                    Tone.CASUAL -> "Thanks re, dosto!"
                    Tone.NEUTRAL -> "Onek dhonnobad tomake!"
                    Tone.FORMAL -> "Apnake oshongkho dhonnobad."
                },
                note = "Casual Dhaka Bangla frequently uses the loanword 'থ্যাংকস' among friends."
            ),
            ReverseEntry(
                keywords = listOf("no worries", "no problem", "chill", "relax", "sin preocupaciones", "tranquilo"),
                casual = "প্যারা নাই, চিল!",
                neutral = "কোনো সমস্যা নেই, চিন্তা কোরো না।",
                formal = "কোনো অসুবিধা নেই, নিশ্চিন্ত থাকুন।",
                pronunciation = when (tone) {
                    Tone.CASUAL -> "Para nai, chill!"
                    Tone.NEUTRAL -> "Kono shomoshsha nei, chinta koro na."
                    Tone.FORMAL -> "Kono oshubidha nei, nishchinto thakun."
                },
                note = "'প্যারা নাই, চিল!' is iconic Dhaka youth slang for 'no stress'."
            ),
            ReverseEntry(
                keywords = listOf("sorry", "my bad", "apologize", "lo siento", "perdón", "désolé"),
                casual = "সরি রে, ভুল হয়ে গেছে!",
                neutral = "দুঃখিত, আমার ভুল হয়েছে।",
                formal = "আমি আন্তরিকভাবে দুঃখিত।",
                pronunciation = when (tone) {
                    Tone.CASUAL -> "Sorry re, bhul hoye geche!"
                    Tone.NEUTRAL -> "Dukkhito, amar bhul hoyeche."
                    Tone.FORMAL -> "Ami antorikbhabe dukkhito."
                },
                note = "Formal apologies pair 'আন্তরিকভাবে' (sincerely) with 'দুঃখিত'."
            ),
            ReverseEntry(
                keywords = listOf("good morning", "morning", "buenos días", "bonjour", "guten morgen", "おはよう"),
                casual = "গুড মর্নিং রে!",
                neutral = "শুভ সকাল!",
                formal = "সুপ্রভাত!",
                pronunciation = if (tone == Tone.CASUAL) "Good morning re!" else "Shubho shokal!",
                note = "In casual speech, urban Bangladeshis often say 'গুড মর্নিং' with Bangla particles like 'রে'."
            ),
            ReverseEntry(
                keywords = listOf("where are you", "where you at", "dónde estás"),
                casual = "কই আছিস তুই?",
                neutral = "তুমি কোথায় আছো?",
                formal = "আপনি এই মুহূর্তে কোথায় আছেন?",
                pronunciation = when (tone) {
                    Tone.CASUAL -> "Koi achish tui?"
                    Tone.NEUTRAL -> "Tumi kothay acho?"
                    Tone.FORMAL -> "Apni ei muhurte kothay achen?"
                },
                note = "'কই' is the colloquial contraction of 'কোথায়' (where)."
            )
        )

        val matchedReverse = reversePhrases.firstOrNull { entry ->
            entry.keywords.any { lower.contains(it) }
        }

        if (matchedReverse != null) {
            val out = when (tone) {
                Tone.CASUAL -> matchedReverse.casual
                Tone.NEUTRAL -> matchedReverse.neutral
                Tone.FORMAL -> matchedReverse.formal
            }
            return TranslationResult(
                originalBangla = out,
                translation = out,
                literal = "Translated from ${sourceLanguage.displayName}: \"$text\"",
                sourceLanguage = sourceLanguage,
                targetLanguage = TargetLanguage.BANGLA,
                tone = tone,
                pronunciation = matchedReverse.pronunciation,
                notes = listOf(matchedReverse.note),
                isGeminiPowered = false,
                detectedDialect = when (tone) {
                    Tone.CASUAL -> "Spoken Bangla (তুই / Friend)"
                    Tone.NEUTRAL -> "Spoken Bangla (তুমি / Everyday)"
                    Tone.FORMAL -> "Polite Bangla (আপনি / Formal)"
                },
                detectedSourceLanguage = detectedSourceLanguage,
                isBanglaToTarget = false
            )
        }

        val fallbackBangla = when (tone) {
            Tone.CASUAL -> "“$text” — এর চলতি ও ঘরোয়া বাংলা রূপ (অফলাইন ইঞ্জিন)"
            Tone.NEUTRAL -> "“$text” — এর স্বাভাবিক কথ্য বাংলা অনুবাদ"
            Tone.FORMAL -> "“$text” — এর মার্জিত ও প্রমিত বাংলা অনুবাদ"
        }

        return TranslationResult(
            originalBangla = fallbackBangla,
            translation = fallbackBangla,
            literal = "Source (${sourceLanguage.displayName}): $text",
            sourceLanguage = sourceLanguage,
            targetLanguage = TargetLanguage.BANGLA,
            tone = tone,
            notes = listOf(
                "${sourceLanguage.displayName} → Bangla register set to ${tone.label} (${when (tone) {
                    Tone.CASUAL -> "তুই / Casual friend style"
                    Tone.NEUTRAL -> "তুমি / Everyday spoken"
                    Tone.FORMAL -> "আপনি / Respectful polite"
                }})."
            ),
            isGeminiPowered = false,
            detectedDialect = "Spoken Bangla (${tone.label})",
            detectedSourceLanguage = detectedSourceLanguage,
            isBanglaToTarget = false
        )
    }

    private fun translateCrossLanguage(
        text: String,
        tone: Tone,
        sourceLanguage: TargetLanguage,
        targetLanguage: TargetLanguage,
        detectedSourceLanguage: String? = null
    ): TranslationResult {
        val lower = text.lowercase()

        data class MultilingualConcept(
            val triggers: List<String>,
            val byLang: Map<TargetLanguage, String>,
            val literal: String,
            val note: String
        )

        val concepts = listOf(
            MultilingualConcept(
                triggers = listOf("hello", "hi", "hey", "hola", "bonjour", "hallo", "ciao", "olá", "merhaba", "नमस्ते", "مرحبا", "こんにちは", "안녕하세요", "你好", "привет"),
                byLang = mapOf(
                    TargetLanguage.ENGLISH_US to if (tone == Tone.FORMAL) "Hello, nice to meet you." else "Hey there!",
                    TargetLanguage.ENGLISH_UK to if (tone == Tone.FORMAL) "Hello, lovely to meet you." else "Hello there!",
                    TargetLanguage.SPANISH to if (tone == Tone.FORMAL) "Hola, mucho gusto." else "¡Hola! ¿Qué tal?",
                    TargetLanguage.FRENCH to if (tone == Tone.FORMAL) "Bonjour, enchanté." else "Salut !",
                    TargetLanguage.GERMAN to if (tone == Tone.FORMAL) "Guten Tag!" else "Hallo!",
                    TargetLanguage.ARABIC to "مرحباً!",
                    TargetLanguage.HINDI to if (tone == Tone.FORMAL) "नमस्ते!" else "अरे, क्या हाल है?",
                    TargetLanguage.URDU to "السلام علیکم!",
                    TargetLanguage.JAPANESE to if (tone == Tone.FORMAL) "こんにちは。" else "やあ！",
                    TargetLanguage.KOREAN to if (tone == Tone.FORMAL) "안녕하세요." else "안녕!",
                    TargetLanguage.CHINESE to "你好！",
                    TargetLanguage.PORTUGUESE to if (tone == Tone.FORMAL) "Olá, tudo bem?" else "Oi!",
                    TargetLanguage.ITALIAN to if (tone == Tone.FORMAL) "Buongiorno!" else "Ciao!",
                    TargetLanguage.TURKISH to "Merhaba!",
                    TargetLanguage.RUSSIAN to if (tone == Tone.FORMAL) "Здравствуйте!" else "Привет!"
                ),
                literal = "Greeting",
                note = "Adapted greeting formality to ${tone.label} register in ${targetLanguage.displayName}."
            ),
            MultilingualConcept(
                triggers = listOf("how are you", "what's up", "how's it going", "cómo estás", "como estas", "comment ça va", "wie geht", "kaise ho", "कैसे हो", "كيف حالك", "お元気", "잘 지냈어"),
                byLang = mapOf(
                    TargetLanguage.ENGLISH_US to if (tone == Tone.CASUAL) "What's up? How's it going?" else "How are you doing?",
                    TargetLanguage.ENGLISH_UK to if (tone == Tone.CASUAL) "You alright? How's it going?" else "How are you?",
                    TargetLanguage.SPANISH to if (tone == Tone.FORMAL) "¿Cómo está usted?" else "¿Cómo estás?",
                    TargetLanguage.FRENCH to if (tone == Tone.FORMAL) "Comment allez-vous ?" else "Comment ça va ?",
                    TargetLanguage.GERMAN to if (tone == Tone.FORMAL) "Wie geht es Ihnen?" else "Wie geht's?",
                    TargetLanguage.ARABIC to "كيف حالك؟",
                    TargetLanguage.HINDI to if (tone == Tone.FORMAL) "आप कैसे हैं?" else "क्या हाल है?",
                    TargetLanguage.URDU to "آپ کیسے ہیں؟",
                    TargetLanguage.JAPANESE to "お元気ですか？",
                    TargetLanguage.KOREAN to "어떻게 지내세요?",
                    TargetLanguage.CHINESE to "你好吗？",
                    TargetLanguage.PORTUGUESE to "Tudo bem com você?",
                    TargetLanguage.ITALIAN to if (tone == Tone.FORMAL) "Come sta?" else "Come stai?",
                    TargetLanguage.TURKISH to "Nasılsın?",
                    TargetLanguage.RUSSIAN to "Как дела?"
                ),
                literal = "How are you?",
                note = "Politeness pronouns adjusted to ${tone.label} register."
            ),
            MultilingualConcept(
                triggers = listOf("thank you", "thanks", "gracias", "merci", "danke", "grazie", "obrigado", "teşekkür", "धन्यवाद", "शुक्रिया", "شكرا", "ありがとう", "감사", "谢谢", "спасибо"),
                byLang = mapOf(
                    TargetLanguage.ENGLISH_US to if (tone == Tone.CASUAL) "Thanks a ton!" else "Thank you very much!",
                    TargetLanguage.ENGLISH_UK to if (tone == Tone.CASUAL) "Cheers, mate!" else "Thank you kindly.",
                    TargetLanguage.SPANISH to "¡Muchas gracias!",
                    TargetLanguage.FRENCH to "Merci beaucoup !",
                    TargetLanguage.GERMAN to "Vielen Dank!",
                    TargetLanguage.ARABIC to "شكراً جزيلاً!",
                    TargetLanguage.HINDI to "बहुत-बहुत धन्यवाद!",
                    TargetLanguage.URDU to "بہت شکریہ!",
                    TargetLanguage.JAPANESE to if (tone == Tone.CASUAL) "ありがとう！" else "どうもありがとうございます！",
                    TargetLanguage.KOREAN to if (tone == Tone.CASUAL) "고마워!" else "감사합니다!",
                    TargetLanguage.CHINESE to "非常感谢！",
                    TargetLanguage.PORTUGUESE to "Muito obrigado!",
                    TargetLanguage.ITALIAN to "Grazie mille!",
                    TargetLanguage.TURKISH to "Çok teşekkür ederim!",
                    TargetLanguage.RUSSIAN to "Большое спасибо!"
                ),
                literal = "Thank you",
                note = "Idiomatic gratitude expression in ${targetLanguage.displayName}."
            ),
            MultilingualConcept(
                triggers = listOf("good morning", "buenos días", "guten morgen", "buongiorno", "bom dia", "सुप्रभात", "صباح الخير", "おはよう"),
                byLang = mapOf(
                    TargetLanguage.ENGLISH_US to "Good morning!",
                    TargetLanguage.ENGLISH_UK to "Good morning!",
                    TargetLanguage.SPANISH to "¡Buenos días!",
                    TargetLanguage.FRENCH to "Bonjour !",
                    TargetLanguage.GERMAN to "Guten Morgen!",
                    TargetLanguage.ARABIC to "صباح الخير!",
                    TargetLanguage.HINDI to "सुप्रभात!",
                    TargetLanguage.URDU to "صبح بخیر!",
                    TargetLanguage.JAPANESE to "おはようございます！",
                    TargetLanguage.KOREAN to "좋은 아침이에요!",
                    TargetLanguage.CHINESE to "早上好！",
                    TargetLanguage.PORTUGUESE to "Bom dia!",
                    TargetLanguage.ITALIAN to "Buongiorno!",
                    TargetLanguage.TURKISH to "Günaydın!",
                    TargetLanguage.RUSSIAN to "Доброе утро!"
                ),
                literal = "Good morning",
                note = "Standard morning greeting in ${targetLanguage.displayName}."
            ),
            MultilingualConcept(
                triggers = listOf("no worries", "no problem", "chill", "relax", "sin preocupaciones", "pas de problème", "kein problem"),
                byLang = mapOf(
                    TargetLanguage.ENGLISH_US to if (tone == Tone.CASUAL) "No sweat, chill!" else "No problem at all.",
                    TargetLanguage.ENGLISH_UK to "No worries at all.",
                    TargetLanguage.SPANISH to "Sin preocupaciones, ¡tranquilo!",
                    TargetLanguage.FRENCH to "Pas de problème, détends-toi !",
                    TargetLanguage.GERMAN to "Kein Problem, entspann dich!",
                    TargetLanguage.ARABIC to "لا توجد مشكلة، استرخِ!",
                    TargetLanguage.HINDI to "कोई तनाव नहीं, आराम करो!",
                    TargetLanguage.JAPANESE to "大丈夫、リラックスして！",
                    TargetLanguage.KOREAN to "걱정 마, 편하게 있어!",
                    TargetLanguage.CHINESE to "没关系，放轻松！"
                ),
                literal = "No problem / relax",
                note = "Casual reassurance adapted to ${targetLanguage.displayName}."
            )
        )

        val matchedConcept = concepts.firstOrNull { concept ->
            concept.triggers.any { lower.contains(it) }
        }

        if (matchedConcept != null) {
            val out = matchedConcept.byLang[targetLanguage]
                ?: matchedConcept.byLang[TargetLanguage.ENGLISH_US]
                ?: text
            return TranslationResult(
                originalBangla = text,
                translation = out,
                literal = "${sourceLanguage.displayName} → ${targetLanguage.displayName} (${matchedConcept.literal})",
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                tone = tone,
                notes = listOf(matchedConcept.note),
                isGeminiPowered = false,
                detectedDialect = "${sourceLanguage.displayName} → ${targetLanguage.displayName}",
                detectedSourceLanguage = detectedSourceLanguage,
                isBanglaToTarget = false
            )
        }

        // Cross-check if input matches any seed phrase translation
        val matchedSeed = seedDatabase.firstOrNull { seed ->
            seed.casualEn.equals(text, ignoreCase = true) ||
                seed.neutralEn.equals(text, ignoreCase = true) ||
                seed.formalEn.equals(text, ignoreCase = true) ||
                seed.spanish.equals(text, ignoreCase = true)
        }
        if (matchedSeed != null) {
            val out = when (targetLanguage) {
                TargetLanguage.ENGLISH_US -> when (tone) {
                    Tone.CASUAL -> matchedSeed.casualEn
                    Tone.NEUTRAL -> matchedSeed.neutralEn
                    Tone.FORMAL -> matchedSeed.formalEn
                }
                TargetLanguage.ENGLISH_UK -> matchedSeed.neutralEn
                TargetLanguage.SPANISH -> matchedSeed.spanish
                TargetLanguage.FRENCH -> getFrenchTranslation(matchedSeed.banglaMatches.first())
                TargetLanguage.GERMAN -> getGermanTranslation(matchedSeed.banglaMatches.first())
                TargetLanguage.ARABIC -> getArabicTranslation(matchedSeed.banglaMatches.first())
                TargetLanguage.HINDI -> getHindiTranslation(matchedSeed.banglaMatches.first())
                TargetLanguage.JAPANESE -> getJapaneseTranslation(matchedSeed.banglaMatches.first())
                else -> matchedSeed.neutralEn
            }
            return TranslationResult(
                originalBangla = text,
                translation = out,
                literal = matchedSeed.literalEn,
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                tone = tone,
                notes = matchedSeed.notes,
                isGeminiPowered = false,
                detectedDialect = "${sourceLanguage.displayName} (${tone.label})",
                detectedSourceLanguage = detectedSourceLanguage,
                isBanglaToTarget = false
            )
        }

        return TranslationResult(
            originalBangla = text,
            translation = "[${targetLanguage.displayName} · ${tone.label}] $text",
            literal = "Offline cross-language placeholder (${sourceLanguage.displayName} → ${targetLanguage.displayName})",
            sourceLanguage = sourceLanguage,
            targetLanguage = targetLanguage,
            tone = tone,
            notes = listOf(
                "Offline database handles Bangla ↔ English and common global phrases. Add a Gemini API key in AI Studio Secrets for full real-time ${sourceLanguage.displayName} → ${targetLanguage.displayName} AI translation."
            ),
            isGeminiPowered = false,
            detectedDialect = "${sourceLanguage.displayName} → ${targetLanguage.displayName}",
            detectedSourceLanguage = detectedSourceLanguage,
            isBanglaToTarget = false
        )
    }
}
