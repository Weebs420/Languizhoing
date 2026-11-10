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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.banglatranslator.data.GrammarRepository
import com.example.banglatranslator.model.GrammarTopic
import com.example.banglatranslator.ui.theme.TealDark
import com.example.banglatranslator.ui.theme.TealPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GrammarGuideScreen(modifier: Modifier = Modifier) {
    val allTopics = remember { GrammarRepository.allTopics }
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var expandedTopicTitle by remember { mutableStateOf<String?>(allTopics.firstOrNull()?.title) }
    var copiedNotice by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "All",
        "Honorifics (আপনি/তুমি/তুই)",
        "Sadhu vs Cholito",
        "Dhaka vs Kolkata",
        "Emphatics & Tags",
        "Slang & Idioms",
        "Sentence Structure"
    )

    // Filter topics based on search query and category
    val filteredTopics = remember(searchQuery, selectedCategory) {
        allTopics.filter { topic ->
            val matchesCategory = when (selectedCategory) {
                "All" -> true
                "Honorifics (আপনি/তুমি/তুই)" -> topic.title.contains("সর্বনাম", ignoreCase = true) || topic.subtitle.contains("Honorifics", ignoreCase = true)
                "Sadhu vs Cholito" -> topic.title.contains("সাধু", ignoreCase = true) || topic.subtitle.contains("Sadhu", ignoreCase = true)
                "Dhaka vs Kolkata" -> topic.title.contains("পশ্চিমবঙ্গ", ignoreCase = true) || topic.subtitle.contains("Dhaka", ignoreCase = true)
                "Emphatics & Tags" -> topic.title.contains("নির্দেশক", ignoreCase = true) || topic.subtitle.contains("Emphatics", ignoreCase = true)
                "Slang & Idioms" -> topic.title.contains("স্ল্যাং", ignoreCase = true) || topic.subtitle.contains("Slang", ignoreCase = true)
                "Sentence Structure" -> topic.title.contains("বাক্য গঠন", ignoreCase = true) || topic.subtitle.contains("Structure", ignoreCase = true)
                else -> true
            }

            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                topic.title.contains(searchQuery, ignoreCase = true) ||
                topic.subtitle.contains(searchQuery, ignoreCase = true) ||
                topic.description.contains(searchQuery, ignoreCase = true) ||
                topic.comparisons.any { comp ->
                    comp.category.contains(searchQuery, ignoreCase = true) ||
                    comp.item1Value.contains(searchQuery, ignoreCase = true) ||
                    comp.item2Value.contains(searchQuery, ignoreCase = true) ||
                    (comp.note?.contains(searchQuery, ignoreCase = true) == true)
                }
            }

            matchesCategory && matchesSearch
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Gradient Hero Header
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Spoken Bangla Guide",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Native speaker nuances, 3-tier honorifics, colloquial slang & dialects",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("grammar_search_input"),
            placeholder = { Text("Search rules, idioms, dialects...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TealPrimary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { searchQuery = "" },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TealPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = {
                        Text(
                            text = cat,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealPrimary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.minimumInteractiveComponentSize()
                )
            }
        }

        // Copied Feedback Notification
        AnimatedVisibility(visible = copiedNotice != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE8F5E9),
                border = BorderStroke(1.dp, Color(0xFF81C784)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Copied to clipboard: \"${copiedNotice ?: ""}\"",
                        fontSize = 11.sp,
                        color = Color(0xFF1B5E20),
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Topics List
        if (filteredTopics.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No grammar topics found",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Try searching with a different keyword or select 'All'.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(
                        onClick = {
                            searchQuery = ""
                            selectedCategory = "All"
                        },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Text("Reset Filters", color = TealPrimary)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredTopics) { topic ->
                    val isExpanded = expandedTopicTitle == topic.title
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                expandedTopicTitle = if (isExpanded) null else topic.title
                            }
                            .testTag("grammar_topic_${topic.title.take(10)}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = if (isExpanded) BorderStroke(1.5.dp, TealPrimary) else CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Title Row with Expand Icon
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = topic.title,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isExpanded) TealDark else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = topic.subtitle,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TealPrimary
                                    )
                                }
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                                    tint = TealPrimary,
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                )
                            }

                            // Description
                            Text(
                                text = topic.description,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Expandable Content: Comparisons Table & Tips
                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    // Comparisons Table
                                    if (topic.comparisons.isNotEmpty()) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                topic.comparisons.forEachIndexed { index, comp ->
                                                    if (index > 0) {
                                                        HorizontalDivider(
                                                            modifier = Modifier.padding(vertical = 10.dp),
                                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                                        )
                                                    }

                                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = comp.category,
                                                                fontSize = 12.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = TealDark
                                                            )

                                                            // Quick Copy Pill
                                                            Surface(
                                                                shape = RoundedCornerShape(4.dp),
                                                                color = TealPrimary.copy(alpha = 0.1f),
                                                                modifier = Modifier
                                                                    .clip(RoundedCornerShape(4.dp))
                                                                    .clickable {
                                                                        val textToCopy = "${comp.item1Value} vs ${comp.item2Value}"
                                                                        clipboardManager.setText(AnnotatedString(textToCopy))
                                                                        copiedNotice = textToCopy
                                                                        coroutineScope.launch {
                                                                            delay(2000)
                                                                            copiedNotice = null
                                                                        }
                                                                    }
                                                            ) {
                                                                Text(
                                                                    text = "Copy Example",
                                                                    fontSize = 9.sp,
                                                                    fontWeight = FontWeight.SemiBold,
                                                                    color = TealDark,
                                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                                )
                                                            }
                                                        }

                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                        ) {
                                                            // Item 1
                                                            Surface(
                                                                shape = RoundedCornerShape(6.dp),
                                                                color = MaterialTheme.colorScheme.surface,
                                                                modifier = Modifier.weight(1f)
                                                            ) {
                                                                Column(modifier = Modifier.padding(6.dp)) {
                                                                    Text(
                                                                        text = comp.item1Label,
                                                                        fontSize = 10.sp,
                                                                        fontWeight = FontWeight.SemiBold,
                                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                                    )
                                                                    Text(
                                                                        text = comp.item1Value,
                                                                        fontSize = 13.sp,
                                                                        fontWeight = FontWeight.Bold,
                                                                        color = MaterialTheme.colorScheme.onSurface
                                                                    )
                                                                }
                                                            }

                                                            // Item 2
                                                            Surface(
                                                                shape = RoundedCornerShape(6.dp),
                                                                color = MaterialTheme.colorScheme.surface,
                                                                modifier = Modifier.weight(1f)
                                                            ) {
                                                                Column(modifier = Modifier.padding(6.dp)) {
                                                                    Text(
                                                                        text = comp.item2Label,
                                                                        fontSize = 10.sp,
                                                                        fontWeight = FontWeight.SemiBold,
                                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                                    )
                                                                    Text(
                                                                        text = comp.item2Value,
                                                                        fontSize = 13.sp,
                                                                        fontWeight = FontWeight.Bold,
                                                                        color = TealDark
                                                                    )
                                                                }
                                                            }
                                                        }

                                                        if (!comp.note.isNullOrEmpty()) {
                                                            Text(
                                                                text = "• ${comp.note}",
                                                                fontSize = 11.sp,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                lineHeight = 15.sp,
                                                                modifier = Modifier.padding(top = 2.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Practical Tips Callout
                                    if (topic.tips.isNotEmpty()) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                            border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.3f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Lightbulb,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = TealPrimary
                                                )
                                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                    Text(
                                                        text = "CONVERSATIONAL USAGE TIP",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TealDark
                                                    )
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
                    }
                }
            }
        }
    }
}
