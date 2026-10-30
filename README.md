# Bangla Translator (Languizhoing)

A modern Android application built with Kotlin, Jetpack Compose, and Material Design 3 that provides native-sounding Bangla to American English translations, idiom lookups, Oxford vocabulary, and bilingual news.

## Features

- **Conversational Translator**: Bangla → American English translation matching three authentic speech registers:
  - **Casual** ("Friend mode"): Informal contractions, friendly banter, and slang.
  - **Neutral** ("Everyday"): Clear conversational English for daily use.
  - **Formal** ("Polished"): Professional and polite phrasing.
- **Trilingual Dictionary Preview**: Side-by-side display of the Bangla input, natural American English translation, and Latin American Spanish equivalent with one-tap copy functionality.
- **Literal Translation & Cultural Notes**: Linguistic and cultural context for idioms and slang terms (e.g., *প্যারা*, *চিল*, *কোপ*, *ছ্যাঁকা*).
- **Phrasebook**: 24+ curated Bangla idioms, slang phrases, Gen Z lingo, Banglish expressions, regional dialects (Dhakaiya, Chittagonian, Sylheti), fillers, and interjections with instant search and category filtering.
- **Oxford Vocabulary**: 20 core Oxford 3000 & 5000 lemmas with CEFR level indicators (A1–C1), parts of speech, Bangla definitions, example sentences, and audio pronunciation triggers.
- **Bilingual News Feed**: Pre-translated headlines from leading Bangladeshi news outlets (Prothom Alo, BDNews24, Dhaka Tribune, Banglanews24) with an interactive "Show Bangla" toggle and external source links.
- **Spoken Grammar Guide**: Interactive reference tables covering Sadhu vs. Cholito forms, honorific pronouns and verb agreement (Formal *আপনি*, Informal *তুমি*, Intimate *তুই*), enclitic particles (*-টা*, *-টি*, *-ই*, *-ও*), and Dhaka vs. Kolkata vocabulary distinctions (*পানি* vs. *জল*, *গোসল* vs. *স্নান*).

## Tech Stack

- **Platform**: Android (minSdk 26, targetSdk 36)
- **UI Framework**: Jetpack Compose with Material 3 (M3)
- **Language**: Kotlin 2.2.10
- **Build System**: Gradle 9.3.1 with Android Gradle Plugin (AGP) 9.1.1
- **Architecture**: Single-Activity, clean modular UI with edge-to-edge system insets handling
