# Bangla Translator (Languizhoing) — Android Implementation Plan

Rewrite of the Languizhoing / Bangla Translator application into a native Android app built with Kotlin, Jetpack Compose, and Material Design 3, delivering natural-sounding spoken Bangla to American English translations with tone control, trilingual dictionary previews, curated idiom phrasebooks, Oxford vocabulary, and bilingual news.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural decisions were confirmed based on your input:
> - **Translation Strategy**: Hybrid engine — an ultra-responsive offline linguistic and idiom database combined with contextual translation rules and trilingual outputs (Bangla, American English, Spanish).
> - **Feature Scope**: Comprehensive 5-tab experience featuring **Translate**, **Phrasebook**, **Vocab**, **News**, and **Spoken Grammar Guide**.
> - **Translation Direction**: Primary focus on spoken/conversational Bangla → natural American English with reverse reference and Spanish equivalents.

---

## 1. Overview & Core Concept

- **What It Does**: Bridges spoken, conversational Bangla (incorporating Gen Z slang like *প্যারা*, *চিল*, *কোপ*, regional dialects, and Banglish) to natural-sounding American English across three registers: **Casual** ("Friend mode"), **Neutral** ("Everyday"), and **Formal** ("Polished").
- **Target Audience**: Bilingual speakers, expats, English language learners in Bangladesh, travelers, and linguists wanting authentic conversational fluency rather than stiff textbook literalism.
- **Key Value**: Provides cultural context, literal vs. idiomatic comparisons, translator's linguistic notes, trilingual dictionary previews, and curated learning resources (Phrasebook, Oxford 3000/5000, and pre-translated headlines).

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Translation Flow**:
   - User types or taps a quick sample sentence (e.g., *"ভাই, আজকে অফিসে অনেক প্যারা গেছে। চিল করতে চাই..."*).
   - Selects tone chip: Casual, Neutral, or Formal.
   - Taps **Translate** or updates on the fly.
   - Views the **Trilingual Dictionary Preview** (Bangla, American English, Spanish) with 1-tap copy buttons.
   - Views the **Main American English Card** formatted in large typography with tone tag.
   - Reviews **Literal Translation** and **Translator's Cultural Notes** explaining slang origins.
2. **Phrasebook Browsing**:
   - Filter by categories: *All*, *Slang*, *Gen Z*, *Idiom*, *Banglish*, *Regional*, *Fillers*, *Interjections*.
   - Instant search across Bangla script, English meaning, and examples.
   - 1-tap copy with toast confirmation and visual checkmark.
3. **Vocabulary Building**:
   - Browse Oxford 3000/5000 lemmas with CEFR level indicators (A1 to C1).
   - Filter by level and list; tap to view Bangla meanings and trigger audio pronunciation links.
4. **Bilingual News Digest**:
   - Read headlines from top publications (Prothom Alo, BDNews24, Dhaka Tribune).
   - Tap "Show Bangla" to expand side-by-side original text or tap "Source" to open external news.
5. **Grammar & Cultural Guide**:
   - Compare Sadhu vs. Cholito, honorific pronouns & verbs (আপনি/তুমি/তুই), and Bangladeshi vs. Kolkata vocabulary (*পানি* vs. *জল*, *গোসল* vs. *স্নান*).

### Visual Identity & Theme
- **Theme Palette**: Deep Teal (`#0F766E`) primary, Light Teal (`#14B8A6`), Mint Soft container (`#F0FDFA`), Charcoal Slate text (`#0F172A`).
- **Brand Identity**: Custom adaptive launcher icon featuring the unified "বE" monogram.
- **Design System**: Material 3 edge-to-edge layout, rounded cards (16dp), tactile filter chips, and smooth state animations.

---

## 3. Key Product Decisions & Trade-Offs (Devil's Advocate Analysis)

### Decision 1: Hybrid Translation vs. Remote-Only API
- **Chosen Approach**: Self-contained high-accuracy linguistic database & rule engine that translates common idioms, slang, and phrases instantly offline without network latency, while structuring data for seamless cloud extension.
- **Devil's Advocate Perspective**: Pure cloud APIs (like Anthropic or Gemini) can hallucinate or fail on flaky mobile connections, adding cost and network delay. A local-first engine guarantees instant zero-latency responses for the core conversational corpus, with zero dead ends.

### Decision 2: Comprehensive 5-Tab Scope vs. Single Translator Screen
- **Chosen Approach**: 5 tabs (`Translate`, `Phrasebook`, `Vocab`, `News`, `Guide`).
- **Devil's Advocate Perspective**: An app with only a single translation box can feel like a generic utility. Preserving the original repository's rich curated phrasebook, Oxford vocabulary, and bilingual news turns the app into an engaging language companion.

### Decision 3: Trilingual Dictionary Output (Bangla + American English + Spanish)
- **Chosen Approach**: Every translation provides both the idiomatic American English and a neutral Spanish rendering alongside the literal translation.
- **Devil's Advocate Perspective**: Users specifically learning English or comparing global languages get 3-in-1 value without cluttering the screen, thanks to the stacked preview card layout.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│                      MainActivity                      │
│                  (enableEdgeToEdge)                    │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                        MainApp                         │
│        (M3 Scaffold + NavigationBar + BackHandler)      │
└─────┬──────────────┬──────────────┬─────────────┬──────┘
      │              │              │             │
┌─────▼──────┐ ┌─────▼──────┐ ┌─────▼─────┐ ┌─────▼─────┐
│ Translator │ │ Phrasebook │ │Vocabulary │ │   News    │
│   Screen   │ │   Screen   │ │  Screen   │ │  Screen   │
└─────┬──────┘ └─────┬──────┘ └─────┬─────┘ └─────┬─────┘
      │              │              │             │
┌─────▼──────┐ ┌─────▼──────┐ ┌─────▼─────┐ ┌─────▼─────┐
│Translation │ │ Phrasebook │ │Vocabulary │ │   News    │
│   Engine   │ │ Repository │ │Repository │ │Repository │
└────────────┘ └────────────┘ └───────────┘ └───────────┘
```

### Component & State Mapping
- `TranslatorScreen`: Tracks input text, selected `Tone` (`CASUAL`, `NEUTRAL`, `FORMAL`), and `TranslationResult`.
- `PhrasebookScreen`: Reactive search filter and category chip selection over 25+ curated idiom records.
- `VocabularyScreen`: Multi-criteria filter (`CEFR` levels A1-C1, Oxford 3000/5000 lists, keyword query).
- `NewsScreen`: Reactive map of expanded bilingual cards with Android intent launchers for original sources.
- `GrammarGuideScreen`: Structured comparative data tables highlighting spoken registers and regional differences.
