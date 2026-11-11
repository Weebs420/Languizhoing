package com.example.banglatranslator.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.banglatranslator.model.FeedbackCategory
import com.example.banglatranslator.model.UserFeedback

/**
 * App Feedback Modal Dialog / Bottom Sheet
 * Built with the Laws of UX feedback principles:
 * - Immediate: Immediate visual acknowledgment when rating or category changes
 * - Informative: Clear instructions and optional context preview
 * - Unobtrusive: Non-disruptive confirmation with auto-dismiss
 * - Prioritized: Structured categorization for actionable input
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackDialog(
    initialSourceContext: String? = null,
    initialTranslationContext: String? = null,
    onDismiss: () -> Unit,
    onSubmit: (UserFeedback) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var selectedCategory by remember { mutableStateOf(FeedbackCategory.GENERAL) }
    var feedbackText by remember { mutableStateOf("") }
    var includeContext by remember { mutableStateOf(!initialTranslationContext.isNullOrBlank()) }
    var isSubmitted by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        modifier = Modifier.testTag("feedback_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.RateReview,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Share Feedback",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Help us improve translation quality & UX",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("feedback_close_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close feedback",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedContent(
                targetState = isSubmitted,
                label = "feedback_transition"
            ) { submitted ->
                if (submitted) {
                    // Success state with immediate informative feedback
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Feedback Received!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Thank you! Your input directly trains and improves our Bangla slang & colloquial model.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Rating Selector (1 to 5 Stars with immediate feedback)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "How was your experience?",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    (1..5).forEach { starIndex ->
                                        IconButton(
                                            onClick = { rating = starIndex },
                                            modifier = Modifier
                                                .size(44.dp)
                                                .testTag("feedback_star_$starIndex")
                                        ) {
                                            Icon(
                                                imageVector = if (starIndex <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                                contentDescription = "$starIndex stars",
                                                tint = if (starIndex <= rating) Color(0xFFF59E0B) else MaterialTheme.colorScheme.outline,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = when (rating) {
                                        5 -> "Excellent • Natural & accurate translation"
                                        4 -> "Good • Minor slang/context nuance needed"
                                        3 -> "Average • Understandable but can improve"
                                        2 -> "Poor • Missed colloquial idiom or tone"
                                        else -> "Needs Work • Inaccurate or inappropriate"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Category Chips
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Category",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FeedbackCategory.values().forEach { cat ->
                                    val selected = selectedCategory == cat
                                    FilterChip(
                                        selected = selected,
                                        onClick = { selectedCategory = cat },
                                        label = { Text(cat.displayName) },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("feedback_cat_${cat.name.lowercase()}")
                                    )
                                }
                            }
                        }

                        // Translation Context Card (if available)
                        if (!initialTranslationContext.isNullOrBlank()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "ATTACH CURRENT TRANSLATION",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Checkbox(
                                            checked = includeContext,
                                            onCheckedChange = { includeContext = it },
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    if (includeContext) {
                                        Text(
                                            text = "Bangla: \"$initialSourceContext\"",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Output: \"$initialTranslationContext\"",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        // Feedback Text Field
                        OutlinedTextField(
                            value = feedbackText,
                            onValueChange = { feedbackText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("feedback_text_input"),
                            label = { Text("Your thoughts or suggestions (optional)") },
                            placeholder = { Text("E.g., In Sylheti/Chittagonian dialect, this word has a different cultural meaning...") },
                            minLines = 3,
                            maxLines = 6,
                            shape = RoundedCornerShape(14.dp)
                        )

                        // Submit Button
                        Button(
                            onClick = {
                                val feedback = UserFeedback(
                                    rating = rating,
                                    category = selectedCategory,
                                    feedbackText = feedbackText.trim(),
                                    sourceContext = if (includeContext) initialSourceContext else null,
                                    translationContext = if (includeContext) initialTranslationContext else null
                                )
                                onSubmit(feedback)
                                isSubmitted = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("submit_feedback_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Submit Feedback",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
