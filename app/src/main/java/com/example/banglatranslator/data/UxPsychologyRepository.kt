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

data class UxValidationPreamble(
    val title: String,
    val dilemma: String,
    val scientificSolution: String,
    val stakeholderPersuasion: String,
    val referenceProblem: String,
    val natureOfLaws: String,
    val takeaways: List<String>
)

object UxPsychologyRepository {

    val validationPreamble = UxValidationPreamble(
        title = "Validating Initial Designs Without Prior Proof",
        dilemma = "How do you validate initial designs without any proof that there is a need to change the existing designs to begin with? When launching a new interface or redesigning an existing one, teams rarely possess retroactive telemetry, analytics, or behavioral proof justifying a change.",
        scientificSolution = "Psychology, which provides a deeper understanding of the human mind, is invaluable in these circumstances. Behavioral and cognitive psychology provide an empirical, scientific lens into how human perception, attention, memory, and motor control naturally operate.",
        stakeholderPersuasion = "Useful in convincing project stakeholders to move in the proposed design direction. Grounding design proposals in psychological laws replaces subjective aesthetic arguments ('I prefer this layout') with objective science ('Miller's Law limits working memory to 4-7 chunks; Hick's Law shows decision time increases logarithmically with options').",
        referenceProblem = "There was only one problem: finding good reference material online quickly turned into an exhausting task. Academic psychology papers are dense, theoretical, and disconnected from modern mobile software engineering. The intersection of psychology and UX design bridges this chasm by translating scientific literature into actionable interface patterns.",
        natureOfLaws = "They are not actual laws intended to be strictly followed—rather, it’s helpful to think of them as guidelines that help inform design decisions based on patterns of human behavior that’ve been observed time and time again.",
        takeaways = listOf(
            "Use behavioral and cognitive psychology to validate design decisions before telemetry or analytics exist.",
            "Cite empirical psychological principles to build consensus and convince project stakeholders objectively.",
            "Treat UX laws as heuristic guidelines based on observed human behavior patterns, not rigid inflexible dogma.",
            "Translate academic cognitive science into concrete, engineerable interaction patterns."
        )
    )

    val principles: List<UxLawPrinciple> = listOf(
        UxLawPrinciple(
            id = "design_validation_psychology",
            name = "Design Validation via Psychology (Laws as Guidelines)",
            type = "Design Methodology",
            summary = "How do you validate initial designs without any proof that there is a need to change the existing designs to begin with? Psychology, which provides a deeper understanding of the human mind, is invaluable in these circumstances. Behavioral and cognitive psychology are useful in convincing project stakeholders to move in the proposed design direction. They are not actual laws intended to be strictly followed—rather, it’s helpful to think of them as guidelines that help inform design decisions based on patterns of human behavior that’ve been observed time and time again.",
            psychologicalBasis = "Empirical cognitive science and behavioral observation: Human sensory perception, working memory limits, saccadic eye movements, and motor response arcs are biological constants. When historical analytics or usage proof are absent, these cognitive baselines serve as an objective benchmark to predict user friction, error rates, and task completion speed before a single line of code is shipped.",
            practicalApplication = "1) Early Validation: Evaluate wireframes against known heuristics (Fitts's, Miller's, Jakob's, Norman's Signifiers) to eliminate friction proactively. 2) Stakeholder Persuasion: Frame design critiques and proposals around documented psychological limits rather than personal taste. 3) Flexible Heuristics: Treat each law as a contextual guidepost to inform tradeoffs rather than an inflexible rule. 4) Synthesis: Combine visual hierarchy (Refactoring UI), human-centered signifiers (Norman), and predictive speed models (Fitts & Doherty) into a coherent, defensible design system.",
            appImplementationExample = "Applied in this app: Every architectural decision—from 48dp touch targets (Fitts's Law), to collocated input/output tools (Gestalt Proximity), to Grayscale-first contrast and 2-color text hierarchies—was validated against cognitive psychology principles before launch, ensuring immediate usability without requiring post-hoc trial-and-error.",
            keyTakeaways = listOf(
                "Validate initial designs using cognitive and behavioral psychology when historical proof or analytics are unavailable.",
                "Convince project stakeholders by framing proposals around scientific human behavior patterns rather than personal opinion.",
                "Think of UX laws as flexible guidelines informed by observed human patterns, not rigid dogma.",
                "The intersection of psychology and UX design makes dense academic research actionable for digital product engineering."
            )
        ),
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
            name = "Mapping, Spatial Correspondence & Cultural Models (Don Norman)",
            type = "Human-Centered Design",
            summary = "A device is easy to use when the set of possible actions is visible, when the controls and displays exploit natural mappings. The principles are simple but rarely incorporated into design. Good design takes care, planning, thought, and an understanding of how people behave. When the mapping uses spatial correspondence between the layout of the controls and the devices being controlled, it is easy to determine how to use them. Note that other choices could have been made. All of these mappings are for the control of translational work because each has a compelling conceptual model of how the operation of the control affects the translation. It doesn’t matter whether these conceptual models are accurate: what matters is that they provide a clear way of remembering and understanding the mappings. Note that there are many mappings that feel “natural” but in fact are specific to a particular culture: what is natural for one culture is not necessarily natural for another.",
            psychologicalBasis = "Spatial cognition, cultural anthropology, and perceptual stimulus-response compatibility: The relationship between a control and its results is easiest to learn wherever there is an understandable mapping between the controls, the actions, and the intended result. Natural mapping, taking advantage of spatial analogies, leads to immediate understanding. Some natural mappings are biological (universal standard that moving the hand up signifies more, moving it down signifies less, using vertical position for intensity/amount). Other mappings are purely cultural conventions: for instance, light switch standards vary across nations (flipping UP turns power ON in North America, while flipping DOWN turns power ON in the UK, Commonwealth, and South Asia); reading directions govern mental progress (Left-to-Right in Western/Bengali scripts vs Right-to-Left in Arabic/Urdu); and color symbology shifts (Red signifies financial loss in the West, but celebration, luck, and prosperity in Asian cultures). Other natural mappings flow from Gestalt principles of grouping and proximity: related controls should be grouped, and controls should be close to the item being controlled.",
            practicalApplication = "1) Make Possible Actions Visible: Never hide available interactions behind unprompted gestures or mysterious menus. 2) Exploit Natural Mappings: Align spatial layout to physical and logical flow (Source on Left, Target on Right). 3) Cultural Audit: Never assume domestic mappings are universal—verify switch directions, reading flows, color meanings, and honorific hierarchies for your target audience. 4) Translational Conceptual Models: Ground translations in clear, intuitive models (e.g. Left-to-Right flow, bilateral swap). 5) Biological Standards: Use vertical position for intensity or amount. 6) Gestalt Proximity: Place controls close to the item being controlled and cluster related actions. 7) Deliberate Craftsmanship: Good design takes care, planning, thought, and a deep understanding of human behavior.",
            appImplementationExample = "Applied in this app: Every possible action is made immediately visible with high-contrast tactile affordances (Clear, Paste, Real-time word counter inside Input; TTS Audio, Copy, Share, Dialect pill inside Output). The bilateral exchange bar strictly mirrors Left-to-Right flow, and the vertical formality register (আপনি / তুমি / তুই) aligns with the biological standard of vertical intensity while honoring Bengali cultural honorific conventions.",
            keyTakeaways = listOf(
                "A device is easy to use when the set of possible actions is visible, and controls exploit natural mappings.",
                "The principles are simple but rarely incorporated into design: good design takes care, planning, thought, and behavioral understanding.",
                "When mapping uses spatial correspondence between controls and devices being controlled, operation is immediately intuitive.",
                "Many mappings that feel 'natural' are in fact specific to a particular culture: what is natural for one culture is not necessarily natural for another.",
                "Universal biological standards: moving UP signifies more, moving DOWN signifies less (vertical position = intensity/amount).",
                "Gestalt principles: related controls should be grouped, and controls should be close to the item being controlled."
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
            name = "Gestalt Laws: Proximity & Grouping (Norman & Gestalt Psychology)",
            type = "Gestalt Principle",
            summary = "Groupings and proximity are important principles from Gestalt psychology that can be used to map controls to function: related controls should be grouped, and controls should be close to the item being controlled. Elements placed close together or enclosed within a visible boundary are perceived as sharing a common function or relationship.",
            psychologicalBasis = "Visual grouping and perceptual patterning in human cognition: Other natural mappings flow from the principles of perception and allow for the natural grouping or patterning of controls and feedback. When related controls are clustered and physically collocated with the data they govern, the visual cortex processes them as a cohesive unit before deliberate conscious scanning is needed, eliminating spatial confusion.",
            practicalApplication = "1) Close Proximity: Controls must be placed directly adjacent to or inside the item being controlled (e.g. Paste, Clear, Word count enclosed directly inside the input container). 2) Related Grouping: Manipulation tools are grouped with manipulation tools, while output feedback tools (Listen, Copy, Share, Dialect badge) are grouped within the translation result container. 3) Distinct Boundaries: Use contrasting borders and tonal surfaces to prevent visual bleeding across distinct operational zones.",
            appImplementationExample = "Applied in this app: The input card encloses the full input manipulation cluster (Clear, Paste, Real-time Word/Char counter); the output translation card encloses the result action cluster (TTS Listen, Copy, Share, Dialect pill, Tone formality indicator). Unrelated tools are never mixed into a detached generic bar.",
            keyTakeaways = listOf(
                "Related controls should be grouped: cluster controls that perform related tasks together.",
                "Controls should be close to the item being controlled: minimize physical and visual distance.",
                "Enclose functional clusters within distinct tonal containers (Gestalt Common Region).",
                "Maintain wider gutters between unrelated operational areas than between related controls."
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
        "0. Validate Initial Designs via Cognitive Psychology" to "When historical proof or analytics are unavailable, validate initial designs against behavioral and cognitive psychology. Use empirical human behavior patterns to convince project stakeholders and team members objectively, treating UX laws as heuristic guidelines rather than rigid dogma.",
        "1. Audit & Map Affordances (Gibson & Norman)" to "Determine all possible interactions between the user and the digital environment: ensure critical actions are perceivable, eliminate false affordances (inert elements looking interactive), surface hidden affordances (native OS text selection handles, long-press gestures), and respect physical touch constraints (48dp+ targets).",
        "2. Specify Clear Signifiers (Norman's Principle)" to "Ensure every affordance is accompanied by an explicit perceptual signifier communicating WHERE to interact, WHAT will happen, and WHAT STATE the system is in through chevrons, labels, borders, and state beacons.",
        "3. Establish Natural Mappings & Audit Cultural Conventions (Don Norman)" to "When mapping uses spatial correspondence between the layout of controls and devices controlled, it is easy to determine how to use them. Note that many mappings that feel 'natural' are in fact specific to a particular culture: what is natural for one culture is not necessarily natural for another. Audit switch conventions, reading directions (LTR vs RTL), color meanings, and honorific hierarchies for your audience. For translational work, establish compelling conceptual models of how operations affect translation.",
        "4. Design in Grayscale First (Refactoring UI)" to "Resist adding color early. Use typography sizing, font weight, contrast shades (slate/charcoal/white), and generous 8dp-grid spacing to do all the heavy lifting before introducing color accents.",
        "5. De-Emphasize Secondary & Tertiary Data (Refactoring UI)" to "Stick to 2–3 text colors (dark for primary, grey for secondary, lighter grey for tertiary) and 2 font weights (normal 400/500 for body, bold 600/700 for headlines). Avoid font weights under 400 for UI work—use lighter color or smaller font size to de-emphasize instead.",
        "6. Contrast on Colored Surfaces: Hand-Picked Hues (Refactoring UI)" to "Making text closer to the background color is what creates hierarchy, not light grey. Don't use reduced white opacity (it looks faded/disabled and patterns bleed through). Instead, choose a color with the same hue as the background, and adjust saturation and lightness until it looks right: this reduces contrast cleanly without text looking faded.",
        "7. Align with Mental Models (Jakob's Law)" to "Analyze how your target audience already operates in everyday apps. Align language switching, layout, and iconography with established user schemas.",
        "8. Minimize Decision Pathways (Hick's Law)" to "Reduce choice count to 3–5 items per view. Group secondary options with progressive disclosure so users never experience choice paralysis.",
        "9. Ergonomic Touch Geometry (Fitts's Law)" to "Ensure every interactive component meets the 48dp x 48dp touch target threshold. Place primary navigation and high-frequency CTAs within the natural thumb zone.",
        "10. Temporal Response (<400ms Doherty Threshold)" to "Provide instantaneous visual feedback within 100ms. Use shimmer skeletons to manage perceived latency and provide offline fallbacks for reliability.",
        "11. Gestalt Grouping & Spatial Proximity (Gestalt Psychology)" to "Related controls should be grouped, and controls should be close to the item being controlled. Structure complex interfaces into visually bounded cards with collocated tools (e.g. input tools nested inside the input card, translation actions nested inside the output card).",
        "12. Ethical Transparency & Autonomy Audit" to "Verify that psychological techniques serve to empower the user rather than coerce them. Eliminate deceptive dark patterns, confirmshaming, or hidden traps.",
        "13. Visibility & Natural Mappings Synthesis (Don Norman)" to "A device is easy to use when the set of possible actions is visible, when the controls and displays exploit natural mappings. The principles are simple but rarely incorporated into design. Good design takes care, planning, thought, and an understanding of how people behave."
    )
}
