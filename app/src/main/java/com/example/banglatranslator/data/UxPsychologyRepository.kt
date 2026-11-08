package com.example.banglatranslator.data

data class UxLawPrinciple(
    val id: String,
    val name: String,
    val type: String, // "Predictive Model", "Cognitive Heuristic", "Gestalt Principle", "AI & Spatial UX", "Ethics & Autonomy"
    val summary: String,
    val psychologicalBasis: String,
    val practicalApplication: String,
    val appImplementationExample: String,
    val keyTakeaways: List<String>
)

object UxPsychologyRepository {

    val principles: List<UxLawPrinciple> = listOf(
        UxLawPrinciple(
            id = "fitts_law",
            name = "Fitts's Law",
            type = "Predictive Model",
            summary = "The time required to rapidly move to a target area is a function of the ratio between the distance to the target and the width of the target.",
            psychologicalBasis = "Human motor control and visual feedback loops: smaller, distant targets require precise micro-corrections, increasing cognitive and physical effort.",
            practicalApplication = "Make high-frequency actions large (minimum 48dp x 48dp touch targets) and place them within the natural mobile 'thumb zone' at the bottom of the viewport.",
            appImplementationExample = "Applied in this app: The primary 'Translate' CTA spans full-width at 54dp height, and action icons (Copy, Listen, Share, Paste) enforce minimum 48dp touch targets.",
            keyTakeaways = listOf(
                "Touch targets must meet at least 48dp x 48dp for accessibility.",
                "Position frequent controls near screen edges or bottom corners where thumbs naturally rest.",
                "Provide ample spatial buffer between destructive and creative actions."
            )
        ),
        UxLawPrinciple(
            id = "jakobs_law",
            name = "Jakob's Law",
            type = "Predictive Model",
            summary = "Users spend most of their time on other apps. This means that users prefer your app to work the same way as all the other apps they already know.",
            psychologicalBasis = "Schema theory: humans construct cognitive mental models from past experiences. Violating schemas forces steep learning curves and cognitive friction.",
            practicalApplication = "Borrow familiar conventions for standard workflows (e.g. bilateral language exchange bar with swap icon, search bars with clear buttons, standard bottom navigation).",
            appImplementationExample = "Applied in this app: Standard bilateral language bar '[Bangla 🇧🇩] ⇄ [English 🇺🇸]' with an interactive swap button mirroring Google Translate and DeepL.",
            keyTakeaways = listOf(
                "Leverage existing mental models so users intuitively know how to operate without onboarding tutorials.",
                "Reserve novel UI patterns only for truly unique value propositions, not standard utilities.",
                "Standard icons (Speaker = Audio, Copy = Clipboard, Share = Export) maintain instant recognition."
            )
        ),
        UxLawPrinciple(
            id = "hicks_law",
            name = "Hick's Law (Hick-Hyman)",
            type = "Predictive Model",
            summary = "The time it takes to make a decision increases logarithmically with the number and complexity of choices available.",
            psychologicalBasis = "Working memory and cognitive processing limits: evaluating numerous alternatives leads to decision fatigue and choice paralysis.",
            practicalApplication = "Employ progressive disclosure. Highlight the 3–5 most popular options upfront and tuck granular choices into search-enabled menus or secondary views.",
            appImplementationExample = "Applied in this app: 3 clearly defined tone registers (Casual, Neutral, Formal) and quick chips for the top 5 languages, eliminating endless dropdown scrolling.",
            keyTakeaways = listOf(
                "Break complex choices into discrete, sequential steps.",
                "Use recommended presets or smart defaults to speed up routine workflows.",
                "Avoid presenting more than 5–7 options simultaneously without categorized grouping."
            )
        ),
        UxLawPrinciple(
            id = "millers_law",
            name = "Miller's Law & Cognitive Chunking",
            type = "Cognitive Heuristic",
            summary = "The average human working memory can only actively process 7 (± 2) discrete chunks of information at a time.",
            psychologicalBasis = "Working memory bottleneck: human brains naturally chunk disparate stimuli into meaningful thematic clusters to avoid overload.",
            practicalApplication = "Organize translation and learning content into visually separated cards and concise bulleted takeaways rather than dense paragraphs.",
            appImplementationExample = "Applied in this app: Results chunked into 4 distinct M3 cards: 1) Fluent Translation, 2) Phonetic Pronunciation, 3) Literal Word-by-Word, 4) Cultural Insights.",
            keyTakeaways = listOf(
                "Chunk complex data into grouped cards using visual containers and spacing.",
                "Keep lists of items to manageable 5–9 element viewports.",
                "Use scannable typography hierarchy (Bold Titles, Supporting Subtitles, Subtle Meta)."
            )
        ),
        UxLawPrinciple(
            id = "doherty_threshold",
            name = "Doherty Threshold",
            type = "Cognitive Heuristic",
            summary = "Productivity soars when a computer and its users interact at a pace (<400ms) that ensures neither has to wait on the other.",
            psychologicalBasis = "Short-term attention spans: response lags over 400ms break the illusion of direct physical manipulation, causing user anxiety and context switching.",
            practicalApplication = "Provide instantaneous visual feedback (optimistic UI, skeleton loaders, haptic ripples) within 100ms, and complete operations in under 400ms.",
            appImplementationExample = "Applied in this app: Real-time word counter, instant animated skeleton placeholder during Gemini inference, and sub-50ms offline linguistic fallback.",
            keyTakeaways = listOf(
                "Acknowledge every user tap with immediate visual/haptic feedback within 100ms.",
                "Use animated skeleton screens instead of blank spinners to reduce perceived wait times.",
                "Implement offline-first caching for instantaneous query responses."
            )
        ),
        UxLawPrinciple(
            id = "peak_end_rule",
            name = "Peak-End Rule",
            type = "Cognitive Heuristic",
            summary = "People judge an experience largely based on how they felt at its peak (the most intense point) and at its end, rather than the average of every moment.",
            psychologicalBasis = "Memory heuristics: the human brain stores experiences as snapshots of intense emotional inflection points and closing sensations.",
            practicalApplication = "Design satisfying micro-moments at the climax of user tasks (e.g. animated 'Copied!' pill, celebratory learning streak badges, progress milestone rings).",
            appImplementationExample = "Applied in this app: Animated checkmark feedback upon copying, plus inspiring streak and mistake-reduction metrics in the coaching tier.",
            keyTakeaways = listOf(
                "Celebrate successful user completions with positive sensory confirmation.",
                "Turn potential low points (e.g. 0 search results or network drop) into constructive, encouraging recovery flows.",
                "Ensure the final touchpoint of a workflow leaves a feeling of accomplishment."
            )
        ),
        UxLawPrinciple(
            id = "gestalt_proximity",
            name = "Gestalt Laws: Proximity & Common Region",
            type = "Gestalt Principle",
            summary = "Elements placed close together or enclosed within a visible boundary are perceived as sharing a common function or relationship.",
            psychologicalBasis = "Visual grouping mechanism in human perception: our visual cortex groups related optical signals before higher-order cognitive processing occurs.",
            practicalApplication = "Group input tools (Paste, Clear, Counter) inside the input card, and group output tools (Listen, Copy, Share, Dialect pill) directly adjacent to the translation text.",
            appImplementationExample = "Applied in this app: Discrete elevated cards with distinct border strokes encapsulate each functional region to prevent cognitive visual bleeding.",
            keyTakeaways = listOf(
                "Use borders and background tonal containers to establish clear visual boundaries.",
                "Maintain greater white space between unrelated components than between related items.",
                "Align controls directly beside the content they manipulate."
            )
        ),
        UxLawPrinciple(
            id = "von_restorff",
            name = "Von Restorff Effect (Isolation Effect)",
            type = "Cognitive Heuristic",
            summary = "When multiple similar objects are present, the one that differs from the rest is most likely to be remembered and clicked.",
            psychologicalBasis = "Novelty bias in perception: stimuli with distinctive visual attributes trigger elevated attention and preferential recall in memory.",
            practicalApplication = "Highlight the most critical action (primary CTA) or recommended option with contrasting color, elevated depth, or a distinct badge.",
            appImplementationExample = "Applied in this app: The 'Coach' plan in PricingScreen is isolated with a vibrant 'Most popular' badge, and the primary 'Translate' button stands out in deep teal.",
            keyTakeaways = listOf(
                "Reserve your brightest accent color strictly for primary actions.",
                "Avoid highlighting more than one element per screen to prevent visual competition.",
                "Use badges ('Most Popular', 'Uncensored') strategically to guide focus."
            )
        ),
        UxLawPrinciple(
            id = "ai_spatial_psychology",
            name = "Psychology of AI & Spatial Computing",
            type = "AI & Spatial UX",
            summary = "Users interact with generative AI and spatial systems through probabilistic expectations, requiring clear transparency, prompt affordances, and cognitive anchoring.",
            psychologicalBasis = "Theory of Mind & Mental Models of Non-Deterministic Agents: users easily anthropomorphize AI or suffer severe trust breakdown if hallucinations are hidden.",
            practicalApplication = "Clearly attribute AI generation (e.g. 'Powered by Gemini 3.8 Flash'), display confidence and dialect attribution, and provide easy verification tools.",
            appImplementationExample = "Applied in this app: Transparent model badge ('Gemini 3.8 Flash'), explicit offline fallback notifications, and word-by-word literal grounding to verify AI accuracy.",
            keyTakeaways = listOf(
                "Always disclose whether an output is AI-generated or rules-based.",
                "Mitigate over-reliance by providing literal word-by-word grounding and cultural notes.",
                "Prevent user panic by handling latency gracefully with transparent status indicators."
            )
        ),
        UxLawPrinciple(
            id = "ethical_psychology",
            name = "Ethical Implications & Dark Patterns",
            type = "Ethics & Autonomy",
            summary = "Behavioral psychology can empower users (nudges) or exploit them (sludge/dark patterns). Ethical design prioritizes user agency, honesty, and transparency.",
            psychologicalBasis = "Psychological reactance and cognitive vulnerability: manipulative tactics (hidden costs, forced loops, confirmshaming) erode long-term trust and loyalty.",
            practicalApplication = "Explicitly guarantee free core utilities, communicate pricing limits clearly, avoid fake urgency, and enable one-tap exit/cancellation.",
            appImplementationExample = "Applied in this app: 'Translation stays free forever. Sell progress, not unlimited human time.' Zero hidden fees, cancel anytime, and transparent uncensored linguistic fidelity.",
            keyTakeaways = listOf(
                "Never use cognitive scarcity or false countdown timers to coerce subscriptions.",
                "Ensure core utility (translation) remains unrestricted and accessible to all.",
                "Respect user autonomy by giving full control over copy, clear, and data privacy."
            )
        )
    )

    val frameworkSteps = listOf(
        "1. Map Mental Models (Jakob's Law)" to "Analyze how your target audience already solves this problem in everyday apps. Align language, layout, and iconography with established user schemas.",
        "2. Minimize Decision Pathways (Hick's Law)" to "Reduce choice count to 3–5 items per view. Group secondary options with progressive disclosure so users never experience choice paralysis.",
        "3. Ergonomic Touch Geometry (Fitts's Law)" to "Ensure every interactive component meets the 48dp x 48dp touch target threshold. Place primary navigation and high-frequency CTAs within the natural thumb zone.",
        "4. Temporal Response (<400ms Doherty Threshold)" to "Provide instantaneous visual feedback within 100ms. Use shimmer skeletons to manage perceived latency and provide offline fallbacks for reliability.",
        "5. Cognitive Chunking & Scannability (Miller's Law)" to "Structure complex information into 4–7 visually bounded cards. Use Gestalt Proximity to bind controls directly to their relevant content.",
        "6. Ethical Transparency & Autonomy Audit" to "Verify that psychological techniques serve to empower the user rather than coerce them. Eliminate deceptive dark patterns, confirmshaming, or hidden traps."
    )
}
