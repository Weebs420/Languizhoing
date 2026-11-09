package com.example.banglatranslator.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.banglatranslator.data.GrammarRepository
import com.example.banglatranslator.data.UxLawPrinciple
import com.example.banglatranslator.data.UxPsychologyRepository
import com.example.banglatranslator.ui.theme.TealDark
import com.example.banglatranslator.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrammarGuideScreen(modifier: Modifier = Modifier) {
    val topics = remember { GrammarRepository.allTopics }
    val uxPrinciples = remember { UxPsychologyRepository.principles }
    val frameworkSteps = remember { UxPsychologyRepository.frameworkSteps }

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedUxCategory by remember { mutableStateOf<String?>("All") }
    var expandedPrincipleId by remember { mutableStateOf<String?>("norman_signifier") }

    val uxCategories = listOf("All", "Human-Centered Design", "Visual Hierarchy", "Predictive Model", "Cognitive Heuristic", "Gestalt Principle", "AI & Spatial UX", "Ethics & Autonomy")

    val filteredPrinciples = remember(selectedUxCategory) {
        if (selectedUxCategory == null || selectedUxCategory == "All") {
            uxPrinciples
        } else {
            uxPrinciples.filter { it.type.equals(selectedUxCategory, ignoreCase = true) }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Gradient Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(TealDark, TealPrimary)
                    ),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (selectedTab == 0) "Spoken Bangla Guide" else "Laws of UX & Psychology",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (selectedTab == 0)
                        "Native speaker nuances, honorific registers & dialect differences"
                    else
                        "Using psychology, heuristics & predictive models to design better products",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
            }
        }

        // Tab Row: Spoken Bangla Guide vs Laws of UX & Psychology
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = TealPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text("Bangla Nuances", fontWeight = FontWeight.SemiBold)
                    }
                },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("tab_bangla_guide")
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text("Laws of UX", fontWeight = FontWeight.SemiBold)
                    }
                },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("tab_laws_of_ux")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedTab == 0) {
            // Tab 0: Spoken Bangla Nuances
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(topics) { topic ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("grammar_topic_${topic.title.take(10)}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = topic.title,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary
                            )

                            Text(
                                text = topic.subtitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = topic.description,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Comparisons Table
                            if (topic.comparisons.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        topic.comparisons.forEachIndexed { index, comp ->
                                            if (index > 0) {
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(vertical = 8.dp),
                                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                                )
                                            }

                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(
                                                    text = comp.category,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TealDark
                                                )

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = comp.item1Label,
                                                            fontSize = 10.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        Text(
                                                            text = comp.item1Value,
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }

                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = comp.item2Label,
                                                            fontSize = 10.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        Text(
                                                            text = comp.item2Value,
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = TealPrimary
                                                        )
                                                    }
                                                }

                                                comp.note?.let { note ->
                                                    Text(
                                                        text = "• $note",
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Tips
                            if (topic.tips.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = TealPrimary
                                    )
                                    Column {
                                        topic.tips.forEach { tip ->
                                            Text(
                                                text = tip,
                                                fontSize = 11.sp,
                                                lineHeight = 16.sp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Tab 1: LAWS OF UX & PSYCHOLOGY IN PRODUCT DESIGN
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Hero Card: Psychology Blueprint
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        ),
                        border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "Psychology as the Core Blueprint",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TealDark
                                )
                            }
                            Text(
                                text = "The most elegant interface will fail if it forces users to conform to the design rather than matching how human visual perception, motor control, and working memory operate. Learn how predictive models (Fitts's, Jakob's, Hick's) and heuristics shape this app.",
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Category Filter Pills
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "FILTER PRINCIPLES BY DOMAIN:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uxCategories.forEach { cat ->
                                val isSelected = selectedUxCategory == cat
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedUxCategory = cat },
                                    label = { Text(cat, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TealPrimary,
                                        selectedLabelColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                )
                            }
                        }
                    }
                }

                // Principles List
                items(filteredPrinciples) { principle ->
                    val isExpanded = expandedPrincipleId == principle.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                expandedPrincipleId = if (isExpanded) null else principle.id
                            }
                            .testTag("ux_card_${principle.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = if (isExpanded) BorderStroke(1.5.dp, TealPrimary) else CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = principle.type.uppercase(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = principle.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                                    tint = TealPrimary,
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                )
                            }

                            Text(
                                text = principle.summary,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                                    // Psychological Basis
                                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text(
                                            text = "🧠 PSYCHOLOGICAL BASIS",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TealDark
                                        )
                                        Text(
                                            text = principle.psychologicalBasis,
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    // Practical Application
                                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text(
                                            text = "🛠️ PRACTICAL UI APPLICATION",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TealDark
                                        )
                                        Text(
                                            text = principle.practicalApplication,
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    // If Affordance principle, render the Gibson-Norman 4-Quadrant Affordance Matrix
                                    if (principle.id == "gibson_affordance") {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.3f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Grid4x4,
                                                        contentDescription = null,
                                                        tint = TealDark,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Text(
                                                        text = "THE 4 AFFORDANCE STATES (NORMAN & GIBSON)",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TealDark
                                                    )
                                                }

                                                // 2x2 Grid Representation
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    // Quadrant 1: Perceivable
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = Color(0xFFE8F5E9),
                                                        border = BorderStroke(1.dp, Color(0xFF81C784)),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Column(modifier = Modifier.padding(8.dp)) {
                                                            Text("🟢 PERCEIVABLE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                                            Text("Action possible & clear signifier present (e.g. 48dp audio button, active CTA)", fontSize = 10.sp, lineHeight = 13.sp, color = Color(0xFF1B5E20))
                                                        }
                                                    }

                                                    // Quadrant 2: Hidden
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = Color(0xFFFFF8E1),
                                                        border = BorderStroke(1.dp, Color(0xFFFFD54F)),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Column(modifier = Modifier.padding(8.dp)) {
                                                            Text("🟡 HIDDEN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF57F17))
                                                            Text("Action possible but invisible (e.g. text selection handles, unhinted gestures)", fontSize = 10.sp, lineHeight = 13.sp, color = Color(0xFFE65100))
                                                        }
                                                    }
                                                }

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    // Quadrant 3: False
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = Color(0xFFFFEBEE),
                                                        border = BorderStroke(1.dp, Color(0xFFE57373)),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Column(modifier = Modifier.padding(8.dp)) {
                                                            Text("🔴 FALSE AFFORDANCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                                                            Text("Looks interactive but inert (e.g. elevated disabled button, clickable-styled badges)", fontSize = 10.sp, lineHeight = 13.sp, color = Color(0xFFB71C1C))
                                                        }
                                                    }

                                                    // Quadrant 4: Correct Rejection
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = MaterialTheme.colorScheme.surface,
                                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Column(modifier = Modifier.padding(8.dp)) {
                                                            Text("⚪ CORRECT REJECTION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                            Text("Not clickable & visibly flat/muted with 0dp elevation (e.g. flat dialect tags)", fontSize = 10.sp, lineHeight = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // If Mapping principle, render the Interactive Auditorium Lights & Switches Mapping Simulator
                                    if (principle.id == "norman_mapping") {
                                        AuditoriumMappingSimulator()
                                    }

                                    // If Grayscale First principle, render the Interactive Grayscale Hierarchy Inspector
                                    if (principle.id == "grayscale_first") {
                                        GrayscaleHierarchyInspector()
                                    }

                                    // If Visual Hierarchy De-Emphasis principle, render the Interactive De-Emphasis Hierarchy Simulator
                                    if (principle.id == "visual_hierarchy_deemphasis") {
                                        DeEmphasisHierarchySimulator()
                                    }

                                    // How It's Applied in this App
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                text = "📱 LIVE APPLICATION IN THIS APP",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = TealPrimary
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = principle.appImplementationExample,
                                                fontSize = 12.sp,
                                                lineHeight = 17.sp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }

                                    // Key Takeaways
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "KEY TAKEAWAYS FOR DESIGNERS:",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        principle.keyTakeaways.forEach { takeaway ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text("•", fontWeight = FontWeight.Bold, color = TealPrimary)
                                                Text(
                                                    text = takeaway,
                                                    fontSize = 12.sp,
                                                    lineHeight = 16.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 6-Step Practical Psychology Framework Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.5.dp, TealDark)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Handyman,
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "6-Step Framework: Applying Psychology in UX",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TealDark
                                )
                            }

                            Text(
                                text = "A systematic workflow to integrate cognitive psychology and predictive ergonomics into your product design pipeline:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            frameworkSteps.forEach { (stepTitle, stepDesc) ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = stepTitle,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TealPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = stepDesc,
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuditoriumMappingSimulator() {
    var isNaturalMapping by remember { mutableStateOf(true) }
    // 4 lights: 0: Top-Left, 1: Top-Right, 2: Bottom-Left, 3: Bottom-Right
    var lights by remember { mutableStateOf(listOf(false, false, false, false)) }
    var explanationText by remember { mutableStateOf("Tap any wall switch below to control the auditorium ceiling lights.") }

    fun toggleLight(lightIndex: Int, switchLabel: String) {
        val updated = lights.toMutableList()
        updated[lightIndex] = !updated[lightIndex]
        lights = updated
        val lightName = when (lightIndex) {
            0 -> "Top-Left (Fixture 1)"
            1 -> "Top-Right (Fixture 2)"
            2 -> "Bottom-Left (Fixture 3)"
            else -> "Bottom-Right (Fixture 4)"
        }
        val status = if (updated[lightIndex]) "illuminated" else "turned off"
        explanationText = if (isNaturalMapping) {
            "Natural Mapping: Switch '$switchLabel' directly mirrors $lightName -> $status with 0 cognitive overhead!"
        } else {
            "Arbitrary Mapping: Switch '$switchLabel' arbitrarily controls $lightName -> $status. Notice you had to guess or memorize!"
        }
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = TealDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "NORMAN'S AUDITORIUM LIGHTS & SWITCHES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TealDark
                    )
                }

                // Reset button
                TextButton(
                    onClick = {
                        lights = listOf(false, false, false, false)
                        explanationText = "All auditorium lights reset."
                    },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Reset", fontSize = 11.sp, color = TealPrimary)
                }
            }

            // Mode Toggle: Natural vs Arbitrary Mapping
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = isNaturalMapping,
                    onClick = {
                        isNaturalMapping = true
                        explanationText = "Switched to Natural Mapping: 2x2 switch layout matches 2x2 ceiling lights."
                    },
                    label = { Text("Natural Mapping (2x2 Grid)", fontSize = 11.sp, fontWeight = if (isNaturalMapping) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealPrimary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).minimumInteractiveComponentSize()
                )

                FilterChip(
                    selected = !isNaturalMapping,
                    onClick = {
                        isNaturalMapping = false
                        explanationText = "Switched to Arbitrary Mapping: 1x4 single row of switches with scrambled connections."
                    },
                    label = { Text("Arbitrary (1x4 Row)", fontSize = 11.sp, fontWeight = if (!isNaturalMapping) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.error,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).minimumInteractiveComponentSize()
                )
            }

            // 1. CEILING LIGHTS GRID (The controlled environment)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1E293B),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "CEILING OF AUDITORIUM (4 LIGHT FIXTURES)",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = Color(0xFF94A3B8)
                    )

                    // 2x2 Lights
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LightFixture(isOn = lights[0], label = "Light 1 (Top-Left)")
                        LightFixture(isOn = lights[1], label = "Light 2 (Top-Right)")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LightFixture(isOn = lights[2], label = "Light 3 (Bottom-Left)")
                        LightFixture(isOn = lights[3], label = "Light 4 (Bottom-Right)")
                    }
                }
            }

            // 2. WALL SWITCHES (The control mechanism)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isNaturalMapping)
                                "WALL SWITCHES: NATURAL 2x2 ISOMORPHIC MAPPING"
                            else
                                "WALL SWITCHES: ARBITRARY 1x4 ROW (CONFUSING)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isNaturalMapping) TealDark else MaterialTheme.colorScheme.error
                        )

                        Text(
                            text = if (isNaturalMapping) "Zero Guessing" else "High Error Rate",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isNaturalMapping) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }

                    if (isNaturalMapping) {
                        // Natural 2x2 layout matching ceiling
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                WallSwitchButton(
                                    label = "TL Switch",
                                    isOn = lights[0],
                                    onClick = { toggleLight(0, "TL Switch") },
                                    modifier = Modifier.weight(1f)
                                )
                                WallSwitchButton(
                                    label = "TR Switch",
                                    isOn = lights[1],
                                    onClick = { toggleLight(1, "TR Switch") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                WallSwitchButton(
                                    label = "BL Switch",
                                    isOn = lights[2],
                                    onClick = { toggleLight(2, "BL Switch") },
                                    modifier = Modifier.weight(1f)
                                )
                                WallSwitchButton(
                                    label = "BR Switch",
                                    isOn = lights[3],
                                    onClick = { toggleLight(3, "BR Switch") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    } else {
                        // Arbitrary single row with scrambled connections!
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Switch A controls Bottom-Right (index 3)
                            WallSwitchButton(
                                label = "Switch A",
                                isOn = lights[3],
                                onClick = { toggleLight(3, "Switch A") },
                                modifier = Modifier.weight(1f)
                            )
                            // Switch B controls Top-Left (index 0)
                            WallSwitchButton(
                                label = "Switch B",
                                isOn = lights[0],
                                onClick = { toggleLight(0, "Switch B") },
                                modifier = Modifier.weight(1f)
                            )
                            // Switch C controls Bottom-Left (index 2)
                            WallSwitchButton(
                                label = "Switch C",
                                isOn = lights[2],
                                onClick = { toggleLight(2, "Switch C") },
                                modifier = Modifier.weight(1f)
                            )
                            // Switch D controls Top-Right (index 1)
                            WallSwitchButton(
                                label = "Switch D",
                                isOn = lights[1],
                                onClick = { toggleLight(1, "Switch D") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Real-time Feedback & Cognitive Law Summary
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isNaturalMapping) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                border = BorderStroke(1.dp, if (isNaturalMapping) Color(0xFF81C784) else Color(0xFFFFB74D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (isNaturalMapping) Icons.Default.CheckCircle else Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = if (isNaturalMapping) Color(0xFF2E7D32) else Color(0xFFE65100),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = explanationText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 15.sp,
                        color = if (isNaturalMapping) Color(0xFF1B5E20) else Color(0xFFBF360C)
                    )
                }
            }
        }
    }
}

@Composable
fun LightFixture(isOn: Boolean, label: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isOn) Color(0xFFFEF08A) else Color(0xFF334155),
        border = BorderStroke(1.dp, if (isOn) Color(0xFFFACC15) else Color(0xFF475569)),
        modifier = Modifier.width(130.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = if (isOn) Color(0xFFCA8A04) else Color(0xFF64748B),
                modifier = Modifier.size(18.dp)
            )
            Column {
                Text(
                    text = label.substringBefore(" "),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOn) Color(0xFF713F12) else Color(0xFFCBD5E1)
                )
                Text(
                    text = if (isOn) "💡 GLOWING" else "OFF",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOn) Color(0xFF854D0E) else Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
fun WallSwitchButton(label: String, isOn: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isOn) TealPrimary else MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, if (isOn) TealDark else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isOn) Icons.Default.ToggleOn else Icons.Default.ToggleOff,
                contentDescription = "$label state",
                tint = if (isOn) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isOn) Color.White else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isOn) "ON" else "OFF",
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isOn) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun GrayscaleHierarchyInspector() {
    var isGrayscalePreview by remember { mutableStateOf(true) }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with Principle Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.InvertColors,
                        contentDescription = null,
                        tint = TealDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "REFACTORING UI: GRAYSCALE AUDIT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TealDark
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isGrayscalePreview) Color(0xFF334155) else TealPrimary
                ) {
                    Text(
                        text = if (isGrayscalePreview) "MONOCHROME" else "COLOR ENHANCED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Mode Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = isGrayscalePreview,
                    onClick = { isGrayscalePreview = true },
                    label = { Text("1. Grayscale Only (Heavy Lifting)", fontSize = 11.sp, fontWeight = if (isGrayscalePreview) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF1E293B),
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).minimumInteractiveComponentSize()
                )

                FilterChip(
                    selected = !isGrayscalePreview,
                    onClick = { isGrayscalePreview = false },
                    label = { Text("2. Color Enhanced", fontSize = 11.sp, fontWeight = if (!isGrayscalePreview) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealPrimary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).minimumInteractiveComponentSize()
                )
            }

            // Interactive Preview Card demonstrating Size, Contrast, and Spacing
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isGrayscalePreview) Color(0xFFF8FAFC) else Color(0xFFF0FDFA),
                border = BorderStroke(
                    1.5.dp,
                    if (isGrayscalePreview) Color(0xFFCBD5E1) else TealPrimary.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Top Bar: Hierarchy via Type Size & Contrast
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isGrayscalePreview) Color(0xFFE2E8F0) else TealPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "SIZE 1: 10sp METADATA BADGE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isGrayscalePreview) Color(0xFF475569) else TealDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "CONTRAST: 60% Muted",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }

                    // Primary Content: Size 20sp + Font Weight Bold doing the heavy lifting
                    Text(
                        text = "SIZE 2: 20sp Primary Action Text",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        lineHeight = 26.sp
                    )

                    // Secondary Content: Size 13sp + 70% contrast doing secondary lifting
                    Text(
                        text = "SIZE 3: 13sp supporting explanation. Notice how spacing (14dp padding, 10dp rhythm) and contrast separate this from the title without needing yellow or blue backgrounds.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFF475569)
                    )

                    // Primary CTA: Generous 48dp+ height & high-contrast fill
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isGrayscalePreview) Color(0xFF0F172A) else TealPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (isGrayscalePreview)
                                    "Primary Button (Contrast & Sizing alone command focus)"
                                else
                                    "Primary Button (Color enhances an already solid hierarchy)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // The 3 Heavy Lifters Breakdown
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "THE 3 HEAVY LIFTERS IN MONOCHROME:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealDark
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Text("📐 SPACING", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Text("8dp/14dp/16dp grid groups related items naturally", fontSize = 9.sp, lineHeight = 12.sp, color = Color(0xFF475569))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Text("🌓 CONTRAST", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Text("Charcoal vs Slate vs Subtle border tones", fontSize = 9.sp, lineHeight = 12.sp, color = Color(0xFF475569))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Text("🔤 SIZE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Text("20sp Header > 14sp Body > 10sp Meta scale", fontSize = 9.sp, lineHeight = 12.sp, color = Color(0xFF475569))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Interactive Visual Hierarchy & De-Emphasis Simulator (Refactoring UI Principles):
 * 1. Deliberately de-emphasize secondary and tertiary information.
 * 2. Stick to 2 or 3 colors: Dark for primary, Grey for secondary, Lighter grey for tertiary.
 * 3. Stick to 2 font weights: Normal (400/500) for body/support, Heavier (600/700) for emphasis.
 * 4. NEVER use font weights under 400 for UI work (use lighter grey color or smaller size instead).
 */
@Composable
fun DeEmphasisHierarchySimulator() {
    var isDeEmphasized by remember { mutableStateOf(true) }
    var selectedHighlightTier by remember { mutableIntStateOf(0) } // 0: All, 1: Primary, 2: Secondary, 3: Tertiary

    // Color tokens matching Refactoring UI's 3-Color Hierarchy
    val primaryDark = Color(0xFF0F172A)    // Dark color for primary content (headline / core translation)
    val secondaryGrey = Color(0xFF475569)  // Grey for secondary content (publish date / phonetics)
    val tertiaryLightGrey = Color(0xFF94A3B8) // Lighter grey for tertiary content (metadata / timestamp)

    // In the "Noisy / Equal Weight" anti-pattern: everything is heavy black and bold
    val noisyColor = Color(0xFF000000)

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(1.5.dp, if (isDeEmphasized) TealPrimary else Color(0xFFDC2626)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("deemphasis_hierarchy_simulator")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = null,
                        tint = if (isDeEmphasized) TealDark else Color(0xFFDC2626),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "VISUAL HIERARCHY SIMULATOR",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isDeEmphasized) TealDark else Color(0xFFDC2626)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isDeEmphasized) TealPrimary.copy(alpha = 0.15f) else Color(0xFFFFE4E6)
                ) {
                    Text(
                        text = if (isDeEmphasized) "✅ 3 SHADES + 2 WEIGHTS" else "❌ EQUAL WEIGHT (NOISY)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDeEmphasized) TealDark else Color(0xFFDC2626),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = "When everything in an interface is competing for attention, it feels noisy and chaotic, like one big wall of content. Deliberately de-emphasize secondary and tertiary info to immediately make the screen pleasing and effortless to scan.",
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Primary Toggle: Equal Weight vs Deliberate De-Emphasis
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { isDeEmphasized = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isDeEmphasized) Color(0xFFDC2626) else MaterialTheme.colorScheme.surface,
                        contentColor = if (!isDeEmphasized) Color.White else MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, if (!isDeEmphasized) Color(0xFFDC2626) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_mode_noisy_equal_weight")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("❌ All Equal Weight", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Noisy wall of content", fontSize = 9.sp)
                    }
                }

                Button(
                    onClick = { isDeEmphasized = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDeEmphasized) TealPrimary else MaterialTheme.colorScheme.surface,
                        contentColor = if (isDeEmphasized) Color.White else MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, if (isDeEmphasized) TealPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_mode_deemphasized_hierarchy")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✅ De-Emphasized", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("3 Shades + 2 Weights", fontSize = 9.sp)
                    }
                }
            }

            // Interactive Tier Highlight Filter
            if (isDeEmphasized) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "EXPLORE INDIVIDUAL TIERS:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val tierLabels = listOf(
                            0 to "Show All",
                            1 to "Tier 1: Primary (Dark & Bold)",
                            2 to "Tier 2: Secondary (Grey & 400)",
                            3 to "Tier 3: Tertiary (Light Grey & 400)"
                        )
                        tierLabels.forEach { (tier, label) ->
                            val isSelected = selectedHighlightTier == tier
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedHighlightTier = tier },
                                label = { Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (tier) {
                                        1 -> primaryDark
                                        2 -> secondaryGrey
                                        3 -> tertiaryLightGrey
                                        else -> TealPrimary
                                    },
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(6.dp)
                            )
                        }
                    }
                }
            }

            // THE SIMULATED TRANSLATION CARD
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(
                    1.dp,
                    if (isDeEmphasized) Color(0xFFE2E8F0) else Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Top Bar: Date & Category Tag
                    val isSecondaryDimmed = isDeEmphasized && (selectedHighlightTier == 1 || selectedHighlightTier == 3)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isDeEmphasized && selectedHighlightTier == 2) Color(0xFFFEF3C7) else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tag / Register
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (!isDeEmphasized) Color.Black else Color(0xFFF1F5F9),
                            border = if (!isDeEmphasized) BorderStroke(1.dp, Color.Black) else null
                        ) {
                            Text(
                                text = "SPOKEN COLLOQUIAL • DHAKAIYA",
                                fontSize = if (!isDeEmphasized) 14.sp else 10.sp,
                                fontWeight = if (!isDeEmphasized) FontWeight.Bold else FontWeight.Normal, // Normal 400
                                color = if (!isDeEmphasized) Color.White else if (isSecondaryDimmed) secondaryGrey.copy(alpha = 0.2f) else secondaryGrey,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        // Published Date
                        Text(
                            text = "Translated today at 10:45 AM",
                            fontSize = if (!isDeEmphasized) 15.sp else 12.sp,
                            fontWeight = if (!isDeEmphasized) FontWeight.Bold else FontWeight.Normal, // Normal 400
                            color = if (!isDeEmphasized) noisyColor else if (isSecondaryDimmed) secondaryGrey.copy(alpha = 0.2f) else secondaryGrey
                        )
                    }

                    // TIER 1: PRIMARY HEADLINE & TRANSLATION
                    val isPrimaryDimmed = isDeEmphasized && (selectedHighlightTier == 2 || selectedHighlightTier == 3)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isDeEmphasized && selectedHighlightTier == 1) Color(0xFFDCFCE7) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "আমি তোমাকে অনেক বেশি ভালোবাসি",
                            fontSize = if (!isDeEmphasized) 16.sp else 21.sp,
                            fontWeight = FontWeight.Bold, // Heavier 700
                            color = if (!isDeEmphasized) noisyColor else if (isPrimaryDimmed) primaryDark.copy(alpha = 0.2f) else primaryDark,
                            lineHeight = 28.sp
                        )

                        Text(
                            text = "I love you very much",
                            fontSize = if (!isDeEmphasized) 16.sp else 18.sp,
                            fontWeight = if (!isDeEmphasized) FontWeight.Bold else FontWeight.SemiBold, // Heavier 600
                            color = if (!isDeEmphasized) noisyColor else if (isPrimaryDimmed) primaryDark.copy(alpha = 0.2f) else primaryDark
                        )
                    }

                    // TIER 2: SECONDARY PHONETIC PRONUNCIATION & CONTEXT
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isDeEmphasized && selectedHighlightTier == 2) Color(0xFFFEF3C7) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Pronunciation: \"Aami tomake onek beshi bhalobashi\"",
                            fontSize = if (!isDeEmphasized) 15.sp else 13.sp,
                            fontWeight = if (!isDeEmphasized) FontWeight.Bold else FontWeight.Normal, // Normal 400
                            color = if (!isDeEmphasized) noisyColor else if (isSecondaryDimmed) secondaryGrey.copy(alpha = 0.2f) else secondaryGrey,
                            lineHeight = 18.sp
                        )
                        Text(
                            text = "Romantic & intimate register. Used between couples and close loved ones.",
                            fontSize = if (!isDeEmphasized) 15.sp else 12.sp,
                            fontWeight = if (!isDeEmphasized) FontWeight.Bold else FontWeight.Normal, // Normal 400
                            color = if (!isDeEmphasized) noisyColor else if (isSecondaryDimmed) secondaryGrey.copy(alpha = 0.2f) else secondaryGrey
                        )
                    }

                    // TIER 3: TERTIARY METADATA & SYSTEM STATS
                    val isTertiaryDimmed = isDeEmphasized && (selectedHighlightTier == 1 || selectedHighlightTier == 2)
                    HorizontalDivider(color = if (!isDeEmphasized) Color.Black else Color(0xFFF1F5F9))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isDeEmphasized && selectedHighlightTier == 3) Color(0xFFE0E7FF) else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Model: Gemini 3.8 Flash • Confidence: 99.4%",
                            fontSize = if (!isDeEmphasized) 14.sp else 11.sp,
                            fontWeight = if (!isDeEmphasized) FontWeight.Bold else FontWeight.Normal, // Normal 400 (never under 400!)
                            color = if (!isDeEmphasized) noisyColor else if (isTertiaryDimmed) tertiaryLightGrey.copy(alpha = 0.2f) else tertiaryLightGrey
                        )

                        Text(
                            text = "28 chars • 32ms",
                            fontSize = if (!isDeEmphasized) 14.sp else 11.sp,
                            fontWeight = if (!isDeEmphasized) FontWeight.Bold else FontWeight.Normal, // Normal 400
                            color = if (!isDeEmphasized) noisyColor else if (isTertiaryDimmed) tertiaryLightGrey.copy(alpha = 0.2f) else tertiaryLightGrey
                        )
                    }

                    // Action Buttons: Sizing & Weight Differentiation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // In noisy mode: 3 identical aggressive black/red buttons
                        // In de-emphasized mode: 1 Primary Filled + 1 Secondary Outlined + 1 Ghost text
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isDeEmphasized) Color.Black else TealPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.4f)
                        ) {
                            Text(
                                text = "🔊 Listen",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold // 700
                            )
                        }

                        OutlinedButton(
                            onClick = { },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (!isDeEmphasized) Color.Black else Color.Transparent,
                                contentColor = if (!isDeEmphasized) Color.White else primaryDark
                            ),
                            border = BorderStroke(1.dp, if (!isDeEmphasized) Color.Black else Color(0xFFCBD5E1)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.1f)
                        ) {
                            Text(
                                text = "📋 Copy",
                                fontSize = 12.sp,
                                fontWeight = if (!isDeEmphasized) FontWeight.Bold else FontWeight.Normal // 400
                            )
                        }

                        TextButton(
                            onClick = { },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = if (!isDeEmphasized) Color.Black else secondaryGrey
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(0.9f)
                        ) {
                            Text(
                                text = "Details",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal // 400
                            )
                        }
                    }
                }
            }

            // CRITICAL LESSONS CARD: 3 COLORS + 2 WEIGHTS + NEVER UNDER 400
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "THE REFACTORING UI FORMULA:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealDark
                    )

                    // 1. Three Colors Breakdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = primaryDark,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Text("1. DARK COLOR", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("#0F172A Primary Headline & Translation", fontSize = 8.sp, color = Color.White.copy(alpha = 0.85f))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = secondaryGrey,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Text("2. GREY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("#475569 Secondary Phonetics & Details", fontSize = 8.sp, color = Color.White.copy(alpha = 0.85f))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = tertiaryLightGrey,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Text("3. LIGHT GREY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Text("#94A3B8 Tertiary Stats & Timestamps", fontSize = 8.sp, color = Color(0xFF1E293B))
                            }
                        }
                    }

                    // 2. Two Font Weights Breakdown + Under 400 Prohibition
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFFFBEB),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFB45309),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "TWO FONT WEIGHTS & THE <400 PROHIBITION",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                            }
                            Text(
                                text = "• Normal weight (400 or 500) for most body text\n• Heavier weight (600 or 700) for text to emphasize\n• Stay away from font weights under 400 for UI work: they can work for huge headings, but are too hard to read at smaller sizes! If considering a lighter weight to de-emphasize, use a lighter grey color or smaller font size instead.",
                                fontSize = 10.sp,
                                lineHeight = 14.sp,
                                color = Color(0xFF78350F)
                            )
                        }
                    }

                    // 3. Colored Background Contrast Rule: Don't Use Grey on Color!
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = Color(0xFF15803D),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "DON'T USE GREY TEXT ON COLORED BACKGROUNDS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534)
                                )
                            }

                            Text(
                                text = "Making text closer to the background color is what actually creates hierarchy, not making it light grey. You might think the easiest way is to use white text and reduce opacity—while that does reduce contrast, it often looks dull, washed out, and even disabled! The real solution is to hand-pick a color sharing the background hue.",
                                fontSize = 10.sp,
                                lineHeight = 15.sp,
                                color = Color(0xFF14532D)
                            )

                            // 3-way visual comparison on Deep Teal background
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                // 1. ❌ Muddy: Neutral grey on teal
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = TealDark,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFCA5A5)) {
                                            Text("❌ NEUTRAL GREY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7F1D1D), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                        Column {
                                            Text("Article Headline / Main Translation", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text("Secondary text in neutral grey (#94A3B8) looks dirty, muddy, and clashes with teal.", fontSize = 10.sp, fontWeight = FontWeight.Normal, color = Color(0xFF94A3B8))
                                        }
                                    }
                                }

                                // 2. ⚠️ Dull: White with reduced opacity
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = TealDark,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFDE68A)) {
                                            Text("⚠️ WHITE OPACITY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78350F), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                        Column {
                                            Text("Article Headline / Main Translation", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text("White with 50% opacity reduces contrast, but looks dull, washed out, and disabled.", fontSize = 10.sp, fontWeight = FontWeight.Normal, color = Color.White.copy(alpha = 0.50f))
                                        }
                                    }
                                }

                                // 3. ✅ Crisp: Hand-picked hue-matched tint
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = TealDark,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF86EFAC)) {
                                            Text("✅ HAND-PICKED HUE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF14532D), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                        Column {
                                            Text("Article Headline / Main Translation", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text("Pale mint (#A7F3D0) shares teal's hue: vibrant, intentional, crisp, and high-quality!", fontSize = 10.sp, fontWeight = FontWeight.Normal, color = Color(0xFFA7F3D0))
                                        }
                                    }
                                }
                            }

                            // 4. Image / Pattern Bleed-Through Demonstration
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFEFF6FF),
                                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Texture,
                                            contentDescription = null,
                                            tint = Color(0xFF1D4ED8),
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = "PATTERN BLEED-THROUGH ON IMAGES & GRADIENTS",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E40AF)
                                        )
                                    }

                                    Text(
                                        text = "Even worse: using reduced opacity on top of an image or patterned hero banner means the background texture literally bleeds through the letterforms! A hand-picked solid opaque color blocks the background texture, keeping glyphs sharp.",
                                        fontSize = 10.sp,
                                        lineHeight = 14.sp,
                                        color = Color(0xFF1E3A8A)
                                    )

                                    // Side-by-side gradient tile
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // ❌ Reduced Opacity with pattern bleed-through
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    Brush.linearGradient(
                                                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF047857))
                                                    )
                                                )
                                                .padding(8.dp)
                                        ) {
                                            Column {
                                                Text("❌ 50% Opacity", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCA5A5))
                                                Text("Headline Text", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                Text("Texture bleeds through letters, making text noisy.", fontSize = 9.sp, fontWeight = FontWeight.Normal, color = Color.White.copy(alpha = 0.50f))
                                            }
                                        }

                                        // ✅ Hand-Picked Solid Opaque Color
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    Brush.linearGradient(
                                                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF047857))
                                                    )
                                                )
                                                .padding(8.dp)
                                        ) {
                                            Column {
                                                Text("✅ Solid Hand-Picked", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF86EFAC))
                                                Text("Headline Text", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                Text("100% solid color blocks pattern: 0% bleed, crisp glyphs!", fontSize = 9.sp, fontWeight = FontWeight.Normal, color = Color(0xFFA7F3D0))
                                            }
                                        }
                                    }
                                }
                            }

                            // 5. Interactive Hue-Matching Playground (HSL Tuning)
                            HueTuningPlayground()
                        }
                    }
                }
            }
        }
    }
}

/**
 * Interactive HSL Hue-Matching Playground (Refactoring UI):
 * "Choose a color with the same hue, and adjust the saturation and lightness until it looks right to you:
 * Hand-picking a color this way makes it easy to reduce the contrast without the text looking faded."
 */
@Composable
fun HueTuningPlayground() {
    var lightness by remember { mutableFloatStateOf(0.82f) }
    var saturation by remember { mutableFloatStateOf(0.65f) }

    // Hue is locked to background Teal (174°)
    val tealHue = 174f

    fun hslToColor(hue: Float, sat: Float, light: Float): Color {
        val hsv = FloatArray(3)
        val v = light + sat * minOf(light, 1f - light)
        val s = if (v == 0f) 0f else 2f * (1f - light / v)
        hsv[0] = hue
        hsv[1] = s.coerceIn(0f, 1f)
        hsv[2] = v.coerceIn(0f, 1f)
        return Color(android.graphics.Color.HSVToColor(hsv))
    }

    val tunedColor = remember(lightness, saturation) {
        hslToColor(tealHue, saturation, lightness)
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "LIVE HUE-MATCHING TUNING TOOL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealDark
                )
            }

            Text(
                text = "Choose a color with the same hue (174° Teal), and adjust saturation and lightness until it looks right to you. Notice how the text de-emphasizes smoothly against the white headline without ever looking faded or milky:",
                fontSize = 10.sp,
                lineHeight = 14.sp,
                color = Color(0xFF475569)
            )

            // Live Preview Card (Deep Teal Background)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = TealDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Primary Headline (White 100% Emphasis)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "Secondary text tuned in real-time. Notice the rich saturation and crisp contrast!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = tunedColor,
                        lineHeight = 16.sp
                    )

                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = RoundedCornerShape(4.dp), color = tunedColor) {
                            Box(modifier = Modifier.size(14.dp))
                        }
                        Text(
                            text = "Hue: 174° • Sat: ${(saturation * 100).toInt()}% • Light: ${(lightness * 100).toInt()}% (100% Opaque)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Quick Preset Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = (lightness == 0.82f && saturation == 0.65f),
                    onClick = { lightness = 0.82f; saturation = 0.65f },
                    label = { Text("Balanced Mint (#A7F3D0)", fontSize = 10.sp) },
                    shape = RoundedCornerShape(6.dp)
                )
                FilterChip(
                    selected = (lightness == 0.70f && saturation == 0.85f),
                    onClick = { lightness = 0.70f; saturation = 0.85f },
                    label = { Text("Vibrant Teal (#5EEAD4)", fontSize = 10.sp) },
                    shape = RoundedCornerShape(6.dp)
                )
                FilterChip(
                    selected = (lightness == 0.90f && saturation == 0.50f),
                    onClick = { lightness = 0.90f; saturation = 0.50f },
                    label = { Text("Soft Pale (#CCFBF1)", fontSize = 10.sp) },
                    shape = RoundedCornerShape(6.dp)
                )
            }

            // Sliders for Lightness and Saturation
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Lightness (${(lightness * 100).toInt()}%)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                    Text("Controls brightness", fontSize = 9.sp, color = Color(0xFF64748B))
                }
                Slider(
                    value = lightness,
                    onValueChange = { lightness = it },
                    valueRange = 0.60f..0.96f,
                    colors = SliderDefaults.colors(thumbColor = TealPrimary, activeTrackColor = TealPrimary)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Saturation (${(saturation * 100).toInt()}%)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                    Text("Prevents washed-out faded look", fontSize = 9.sp, color = Color(0xFF64748B))
                }
                Slider(
                    value = saturation,
                    onValueChange = { saturation = it },
                    valueRange = 0.20f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = TealPrimary, activeTrackColor = TealPrimary)
                )
            }
        }
    }
}
