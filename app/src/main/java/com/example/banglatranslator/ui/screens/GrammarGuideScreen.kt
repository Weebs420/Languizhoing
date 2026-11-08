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

    val uxCategories = listOf("All", "Human-Centered Design", "Predictive Model", "Cognitive Heuristic", "Gestalt Principle", "AI & Spatial UX", "Ethics & Autonomy")

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
