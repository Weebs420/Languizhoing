package com.example.banglatranslator.data

data class UxLawPrinciple(
    val id: String,
    val name: String,
    val type: String, // "Human-Centered Design", "Predictive Model", "Cognitive Heuristic", "Gestalt Principle", "AI & Spatial UX", "Ethics & Autonomy"
    val summary: String,
    val psychologicalBasis: String,
    val practicalApplication: String,
    val appImplementationExample: String,
    val keyTakeaways: List<String>
)

object UxPsychologyRepository {

    val principles: List<UxLawPrinciple> = listOf(
        UxLawPrinciple(
            id = "norman_signifier",
            name = "The Signifier (Don Norman)",
            type = "Human-Centered Design",
            summary = "The signifier is an important communication device to the recipient, whether or not communication was intended. Affordances define what actions are possible; signifiers specify how people discover those possibilities and know what state the system is in.",
            psychologicalBasis = "Perceptual psychology and semiotics: humans do not perceive physical code or database states; they interpret visual marks, chevrons, tactile elevations, sounds, and text indicators to determine where to act and what the consequences will be.",
            practicalApplication = "Never leave interactability or state ambiguous. Use explicit signifiers: directional chevrons ('▾') on pickers, labeled buttons with icons ('🔊 Listen', '📋 Copy'), pulsating state dots ('🟢 Gemini 3.8 Active'), focus borders, and clear microcopy.",
            appImplementationExample = "Applied in this app: The target language pill displays an explicit 'TAP TO CHANGE ▾' signifier; the swap button features rotating arrows ('⇄'); the primary CTA has an arrow cue ('Translate →'); and the AI status is signified by a live green/amber beacon.",
            keyTakeaways = listOf(
                "Affordances are physical possibilities; signifiers are the perceptual clues that direct human attention.",
                "Accidental signifiers mislead users (e.g. static text styled like a clickable link); intentional signifiers prevent errors.",
                "A robust signifier answers three questions: Where do I tap? What will happen? What state is the system in?"
            )
        ),
        UxLawPrinciple(
            id = "gibson_affordance",
            name = "Affordances (Gibson & Norman)",
            type = "Human-Centered Design",
            summary = "Affordances are the possible interactions between people and the environment. Some affordances are perceivable; others are not. An affordance exists independently of whether the actor recognizes it.",
            psychologicalBasis = "Ecological psychology (J.J. Gibson): organisms perceive objects in terms of their affordances for physical action (surfaces afford support, apertures afford passage). In digital product design (Don Norman), flat touchscreen glass lacks mechanical tactile constraints, so designers must make virtual affordances perceivable while ruthlessly eliminating false affordances.",
            practicalApplication = "Audit interface elements across 4 states: 1) Perceivable (action possible and clear), 2) Hidden (action possible but invisible—e.g. unhinted swipe/long-press gestures), 3) False (looks clickable but inert—e.g. static badges looking like buttons), 4) Correct Rejection. Enable native OS text selection affordances and surface hidden gestures.",
            appImplementationExample = "Applied in this app: Wrapping output in SelectionContainer to afford native Android text selection and dragging; eliminating false affordances on disabled Translate CTA and static metadata badges; and surfacing hidden audio and copy actions with high-contrast tactile buttons.",
            keyTakeaways = listOf(
                "Affordances define what interactions are physically and logically possible.",
                "Perceivable affordances allow users to take action without instruction manuals or trial-and-error.",
                "False affordances (e.g. unclickable elements styled like buttons) cause severe user frustration and mistrust.",
                "Hidden affordances (e.g. gestural swipes or long-press shortcuts) require explicit signifiers to become discoverable."
            )
        ),
        UxLawPrinciple(
            id = "norman_mapping",
            name = "Mapping (Don Norman)",
            type = "Human-Centered Design",
            summary = "Mapping is a technical term, borrowed from mathematics, meaning the relationship between the elements of two sets of things—specifically between controls and their movements or results in the world. Natural mapping exploits spatial analogies, cultural standards, and physical layout so the relationship is immediately understood.",
            psychologicalBasis = "Spatial cognition, cognitive schemas, and stimulus-response compatibility (Fitts & Seeger): When controls mirror the physical layout or logical sequence of the objects they control (like light switches arranged matching ceiling lights, or stove knobs matching burner positions), cognitive load is minimal. Arbitrary or reverse mappings cause severe confusion, delayed reaction times, and user error.",
            practicalApplication = "1) Direct Spatial Collocation: Place manipulation controls directly on or beside the content they affect (e.g. Copy/Listen/Share inside the translated result card, Paste/Clear inside the input box). 2) Directional Alignment: Arrange Source → Arrow → Target in natural reading direction. 3) Semantic Ordering: Arrange tone chips in sequential order of formality (Casual → Neutral → Formal). 4) Interactive Model-to-Display congruence: Rotating the swap button inverts the bilateral language bar.",
            appImplementationExample = "Applied in this app: Direct spatial mapping places the pronunciation and clipboard tools directly inside the corresponding output card; the input toolbars are nested inside the input card; the bilateral language exchange bar flows Left-to-Right (Bangla 🇧🇩 ⇄ Target 🇺🇸); and tone registers are laid out horizontally from Casual to Formal.",
            keyTakeaways = listOf(
                "Natural mapping relies on spatial analogies and cultural conventions so controls feel intuitive without memory effort.",
                "Collocate controls directly with the objects they affect rather than in a distant disconnected toolbar.",
                "Directional controls must mirror the flow of data or movement (e.g. Left-to-Right translation direction).",
                "Poor mapping (like mismatched stove burners or misordered light switches) forces trial-and-error and creates anxiety."
            )
        ),
        UxLawPrinciple(
            id = "grayscale_first",
            name = "Design in Grayscale First (Refactoring UI)",
            type = "Visual Hierarchy",
            summary = "Even when you’re ready to refine an idea in higher fidelity, resist the temptation to introduce color right away. By designing in grayscale, you’re forced to use spacing, contrast, and size to do all of the heavy lifting. It’s a little more challenging, but you’ll end up with a clearer interface with a strong hierarchy that’s easy to enhance with color later.",
            psychologicalBasis = "Luminance and spatial frequency processing in human vision: The human visual system processes luminance (rods and magnocellular pathway) before chromatic information (parvocellular pathway). If an interface relies on color alone to communicate importance or separation, it collapses for color-blind users and creates visual clutter. Enforcing monochromatic hierarchy guarantees contrast, scannability, and structural clarity.",
            practicalApplication = "1) Size Hierarchy: Use distinct type scale steps (e.g. 20sp for primary translation, 14sp for body, 10sp for metadata). 2) Contrast via Tone: Differentiate secondary text with 60% opacity or slate tones rather than colored fonts. 3) Generous Spacing: Use 8dp/14dp/16dp spacing to establish Gestalt grouping. 4) Border Subtlety: Use 1dp light gray borders rather than loud colored outlines. 5) Accent as Enhancement: Add brand color (Teal) only after the monochrome layout works flawlessly on its own.",
            appImplementationExample = "Applied in this app: Every card and button uses sizing (54dp CTA vs 36dp utility), type hierarchy (Bold titles vs muted subtitles), and generous white space. You can toggle the live 'Grayscale Audit Mode' in the app to prove that the hierarchy remains 100% readable without color.",
            keyTakeaways = listOf(
                "Color is an enhancer, not a crutch: if the design doesn't work in grayscale, color won't save it.",
                "Use size, weight, tone, and spacing to establish the primary, secondary, and tertiary hierarchy.",
                "Monochromatic design inherently satisfies WCAG contrast and color-blind accessibility.",
                "Introducing color last ensures that accents immediately draw the eye to high-priority actions."
            )
        ),
        UxLawPrinciple(
            id = "visual_hierarchy_deemphasis",
            name = "Visual Hierarchy via De-Emphasis (Refactoring UI)",
            type = "Visual Hierarchy",
            summary = "When you deliberately de-emphasize secondary and tertiary information, and make an effort to highlight the elements that are most important, the result is immediately more pleasing, even though the color scheme, font choice, and layout haven't changed. When everything in an interface is competing for attention, it feels noisy and chaotic, like one big wall of content where it’s not clear what actually matters.",
            psychologicalBasis = "Attentional selectivity & cognitive bandwidth: Human visual perception operates on contrast gradients. If all elements have equal weight (all dark, all bold, equal size), the eye experiences visual friction and cannot establish a saccadic scan path. Deliberately suppressing secondary and tertiary information creates an effortless visual hierarchy that guides the user directly to what matters most.",
            practicalApplication = "1) Stick to 2 or 3 colors: A dark color for primary content (headline/main translation), a grey for secondary content (publish date/phonetics), and a lighter grey for tertiary content (footer/metadata). 2) Stick to 2 font weights: Normal (400 or 500) for body, heavier (600 or 700) for emphasis. 3) Stay away from font weights under 400 for UI work: use a lighter grey color or smaller size instead. 4) DON'T USE GREY ON COLORED SURFACES: Making text closer to the background color is what actually creates hierarchy, not making it light grey. 5) AVOID REDUCING WHITE OPACITY: While it reduces contrast, it makes text look dull, washed out, and disabled. 6) ELIMINATE IMAGE & PATTERN BLEED-THROUGH: Reduced opacity lets background textures bleed through letters. 7) THE HUE-MATCHING FORMULA: Choose a color with the same hue as the background, and adjust the saturation and lightness until it looks right to you. Hand-picking a color this way makes it easy to reduce the contrast without the text looking faded or washed out.",
            appImplementationExample = "Applied in this app: The translation card uses dark slate (#0F172A, Bold 700, 20sp), medium grey (#475569, Normal 400, 14sp), and lighter grey (#94A3B8, Normal 400, 11sp) on white. On colored surfaces and gradient headers, secondary subtitles use a hand-picked solid hue-matched mint tint (#CCFBF1 / #A7F3D0, sharing Teal's 174° hue with tailored 85% lightness and 65% saturation) rather than semi-transparent white, completely preventing pattern bleed-through while keeping text crisp, vibrant, and readable without looking faded.",
            keyTakeaways = listOf(
                "Not all information is created equal: classify every element into Primary, Secondary, or Tertiary.",
                "Stick to 2–3 colors: dark for primary, grey for secondary, lighter grey for tertiary on white.",
                "Stick to 2 font weights: normal (400/500) for body, heavier (600/700) for emphasis. Avoid <400 weights.",
                "Making text closer to the background color creates hierarchy, not making it light grey.",
                "AVOID white text with reduced opacity: it looks dull/disabled and lets background patterns bleed through.",
                "Choose a color with the same hue, and adjust saturation and lightness: reduces contrast without text looking faded."
            )
        ),
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
        "1. Audit & Map Affordances (Gibson & Norman)" to "Determine all possible interactions between the user and the digital environment: ensure critical actions are perceivable, eliminate false affordances (inert elements looking interactive), surface hidden affordances (native OS text selection handles, long-press gestures), and respect physical touch constraints (48dp+ targets).",
        "2. Specify Clear Signifiers (Norman's Principle)" to "Ensure every affordance is accompanied by an explicit perceptual signifier communicating WHERE to interact, WHAT will happen, and WHAT STATE the system is in through chevrons, labels, borders, and state beacons.",
        "3. Establish Natural Mappings (Don Norman)" to "Design intuitive spatial, directional, and conceptual relationships between controls and real-world results: collocate manipulation tools directly with the content they govern, mirror physical/reading layouts (Left-to-Right data flow), and order registers logically.",
        "4. Design in Grayscale First (Refactoring UI)" to "Resist adding color early. Use typography sizing, font weight, contrast shades (slate/charcoal/white), and generous 8dp-grid spacing to do all the heavy lifting before introducing color accents.",
        "5. De-Emphasize Secondary & Tertiary Data (Refactoring UI)" to "Stick to 2–3 text colors (dark for primary, grey for secondary, lighter grey for tertiary) and 2 font weights (normal 400/500 for body, bold 600/700 for headlines). Avoid font weights under 400 for UI work—use lighter color or smaller font size to de-emphasize instead.",
        "6. Contrast on Colored Surfaces: Hand-Picked Hues (Refactoring UI)" to "Making text closer to the background color is what creates hierarchy, not light grey. Don't use reduced white opacity (it looks faded/disabled and patterns bleed through). Instead, choose a color with the same hue as the background, and adjust saturation and lightness until it looks right: this reduces contrast cleanly without text looking faded.",
        "7. Align with Mental Models (Jakob's Law)" to "Analyze how your target audience already operates in everyday apps. Align language switching, layout, and iconography with established user schemas.",
        "8. Minimize Decision Pathways (Hick's Law)" to "Reduce choice count to 3–5 items per view. Group secondary options with progressive disclosure so users never experience choice paralysis.",
        "9. Ergonomic Touch Geometry (Fitts's Law)" to "Ensure every interactive component meets the 48dp x 48dp touch target threshold. Place primary navigation and high-frequency CTAs within the natural thumb zone.",
        "10. Temporal Response (<400ms Doherty Threshold)" to "Provide instantaneous visual feedback within 100ms. Use shimmer skeletons to manage perceived latency and provide offline fallbacks for reliability.",
        "11. Cognitive Chunking & Scannability (Miller's Law)" to "Structure complex information into 4–7 visually bounded cards. Use Gestalt Proximity to bind controls directly to their relevant content.",
        "12. Ethical Transparency & Autonomy Audit" to "Verify that psychological techniques serve to empower the user rather than coerce them. Eliminate deceptive dark patterns, confirmshaming, or hidden traps."
    )
}
