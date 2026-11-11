package com.example.banglatranslator.ui.screens

import android.content.Intent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.banglatranslator.data.FeedbackRepository
import com.example.banglatranslator.data.GeminiTranslationService
import com.example.banglatranslator.data.TranslationHistoryRepository
import com.example.banglatranslator.model.TargetLanguage
import com.example.banglatranslator.model.Tone
import com.example.banglatranslator.model.TranslationHistoryItem
import com.example.banglatranslator.model.TranslationResult
import com.example.banglatranslator.ui.components.FeedbackDialog
import com.example.banglatranslator.ui.theme.TealAccent
import com.example.banglatranslator.ui.theme.TealDark
import com.example.banglatranslator.ui.theme.TealPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslatorScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var text by remember { mutableStateOf("ভাই, আজকে অফিসে অনেক প্যারা গেছে। চিল করতে চাই, চলো কোথাও ঘুরে আসি।") }
    var selectedLanguage by remember { mutableStateOf(TargetLanguage.ENGLISH_US) }
    var tone by remember { mutableStateOf(Tone.CASUAL) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    var result by remember { mutableStateOf<TranslationResult?>(null) }
    var isTranslating by remember { mutableStateOf(false) }
    var copiedField by remember { mutableStateOf<String?>(null) }
    var swapRotation by remember { mutableFloatStateOf(0f) }
    var isGrayscaleAudit by remember { mutableStateOf(false) }
    val historyList by TranslationHistoryRepository.history.collectAsState()
    var showHistorySheet by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var isTranslationHelpful by remember { mutableStateOf<Boolean?>(null) }

    // Dynamic color tokens: Refactoring UI Principle (Designing in Grayscale First)
    val themePrimary = if (isGrayscaleAudit) Color(0xFF0F172A) else TealPrimary
    val themeDark = if (isGrayscaleAudit) Color(0xFF1E293B) else TealDark
    val themeAccent = if (isGrayscaleAudit) Color(0xFF475569) else TealAccent
    val themeGradient = if (isGrayscaleAudit) listOf(Color(0xFF0F172A), Color(0xFF334155)) else listOf(TealDark, TealPrimary)

    // TextToSpeech for pronunciation with Auditory-Visual Mapping
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val speech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
            }
        }
        speech.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isSpeaking = true
            }
            override fun onDone(utteranceId: String?) {
                isSpeaking = false
            }
            override fun onError(utteranceId: String?) {
                isSpeaking = false
            }
        })
        tts = speech
        onDispose {
            try {
                speech.stop()
                speech.shutdown()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun speakText(content: String, targetLanguage: TargetLanguage) {
        tts?.let { engine ->
            val locale = when (targetLanguage) {
                TargetLanguage.SPANISH -> Locale("es", "ES")
                TargetLanguage.FRENCH -> Locale.FRENCH
                TargetLanguage.GERMAN -> Locale.GERMAN
                TargetLanguage.JAPANESE -> Locale.JAPANESE
                TargetLanguage.HINDI -> Locale("hi", "IN")
                TargetLanguage.ARABIC -> Locale("ar")
                TargetLanguage.ENGLISH_UK -> Locale.UK
                else -> Locale.US
            }
            engine.language = locale
            isSpeaking = true
            engine.speak(content, TextToSpeech.QUEUE_FLUSH, null, "tts_utterance_${System.currentTimeMillis()}")
            Toast.makeText(context, "Playing audio pronunciation…", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, "Speech engine initializing…", Toast.LENGTH_SHORT).show()
        }
    }

    fun performTranslation() {
        if (text.isBlank() || isTranslating) return
        coroutineScope.launch {
            isTranslating = true
            val res = GeminiTranslationService.translate(text, selectedLanguage, tone)
            result = res
            isTranslating = false
            if (res.translation.isNotBlank()) {
                TranslationHistoryRepository.addEntry(
                    TranslationHistoryItem(
                        sourceText = text,
                        translatedText = res.translation,
                        sourceLanguage = "Bangla",
                        targetLanguage = selectedLanguage,
                        tone = tone,
                        detectedDialect = res.detectedDialect,
                        isGeminiPowered = res.isGeminiPowered,
                        literalMeaning = res.literal,
                        pronunciation = res.pronunciation
                    )
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        FeedbackRepository.init(context)
        TranslationHistoryRepository.init(context)
        performTranslation()
    }

    fun restoreFromHistory(item: TranslationHistoryItem) {
        text = item.sourceText
        selectedLanguage = item.targetLanguage
        tone = item.tone
        result = TranslationResult(
            originalBangla = item.sourceText,
            translation = item.translatedText,
            literal = item.literalMeaning,
            targetLanguage = item.targetLanguage,
            tone = item.tone,
            detectedDialect = item.detectedDialect,
            pronunciation = item.pronunciation,
            isGeminiPowered = item.isGeminiPowered
        )
        Toast.makeText(context, "Restored translation into editor", Toast.LENGTH_SHORT).show()
    }

    fun copyToClipboard(content: String, label: String) {
        clipboardManager.setText(AnnotatedString(content))
        copiedField = label
        coroutineScope.launch {
            delay(2500)
            if (copiedField == label) copiedField = null
        }
    }

    fun shareText(content: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share translation via")
        context.startActivity(shareIntent)
    }

    fun pasteFromClipboard() {
        val clip = clipboardManager.getText()
        if (clip != null && clip.text.isNotBlank()) {
            text = clip.text
            performTranslation()
            Toast.makeText(context, "Pasted & translating", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
        }
    }

    val samplePhraseCategories = listOf(
        "🔥 Raw & Slang" to listOf(
            "ধুর বাল! প্যারা নাই, চিল!",
            "কী বাল করছো ভাই?",
            "বেশি চোদাস না, কাজ কর।"
        ),
        "💼 Everyday & Office" to listOf(
            "ভাই, আজকে অফিসে অনেক প্যারা গেছে। চিল করতে চাই, চলো কোথাও ঘুরে আসি।",
            "দেরি হওয়ার জন্য অত্যন্ত দুঃখিত, জ্যামে আটকে ছিলাম।",
            "তুমি কাল মিটিংয়ে আসবা, তাই না?"
        ),
        "🎉 Gen Z & College" to listOf(
            "পরীক্ষায় একবারে কোপ দিয়া আসছি!",
            "গানটা পুরা সেই ছিল রে ভাই!",
            "আমার জন্য একটু wait করো, আমি প্রায় চলে এসেছি।"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header: System State Signifiers
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = themeGradient
                    ),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Main Header Row: Logo & Title Column (unconstrained, full width)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "বE",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bangla AI Translator",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Idiomatic, Slang & Uncensored Cross-Language Engine",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // System State Signifiers Row (Beneath title, so title is never crushed)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFD32F2F).copy(alpha = 0.9f),
                        modifier = Modifier.testTag("uncensored_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = "Uncensored mode enabled",
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "Uncensored",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Gemini Model",
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "Gemini 3.8",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Grayscale Audit Toggle Pill (Refactoring UI Principle)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isGrayscaleAudit) Color.White else Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { isGrayscaleAudit = !isGrayscaleAudit }
                            .testTag("grayscale_audit_toggle")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.InvertColors,
                                contentDescription = "Toggle Grayscale Audit",
                                tint = if (isGrayscaleAudit) Color(0xFF0F172A) else Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = if (isGrayscaleAudit) "Mono ON" else "Grayscale",
                                color = if (isGrayscaleAudit) Color(0xFF0F172A) else Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // History quick button
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showHistorySheet = true }
                            .testTag("header_history_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "View Translation History",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "History (${historyList.size})",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Feedback quick button
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showFeedbackDialog = true }
                            .testTag("header_feedback_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Feedback,
                                contentDescription = "Share Feedback",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Feedback",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Grayscale Audit Notice Banner
        AnimatedVisibility(
            visible = isGrayscaleAudit,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF1F5F9),
                border = BorderStroke(1.dp, Color(0xFF94A3B8)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.InvertColors,
                        contentDescription = null,
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(16.dp)
                    )
                    Column {
                        Text(
                            text = "Refactoring UI: Grayscale Audit Mode Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Colors stripped to monochrome. Spacing (16dp), contrast (slate vs charcoal), and sizing (54dp buttons, 20sp text) do all the heavy lifting without relying on color.",
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // SIGNIFIER 1: Bilateral Language Exchange Bar with Explicit Interactive Affordances
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bilateral_language_bar"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Source Language (Signified as Source)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, themePrimary.copy(alpha = 0.3f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🇧🇩", fontSize = 18.sp)
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(
                                        text = "Bangla",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Fixed source",
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                                Text(
                                    text = "SOURCE (বাংলা)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Middle: Swap Action Signifier (48dp Touch Target, Rotating Double Arrow)
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, themePrimary.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .minimumInteractiveComponentSize()
                            .size(46.dp)
                            .clip(CircleShape)
                            .clickable {
                                swapRotation += 180f
                                performTranslation()
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Swap orientation",
                                tint = themePrimary,
                                modifier = Modifier
                                    .size(22.dp)
                                    .rotate(swapRotation)
                            )
                        }
                    }

                    // Right: Target Language Signifier (Signifying 'Tap to Change' with Down Chevron)
                    ExposedDropdownMenuBox(
                        expanded = isDropdownExpanded,
                        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.5.dp, themePrimary.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("target_language_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = selectedLanguage.flagEmoji, fontSize = 18.sp)
                                    Column {
                                        Text(
                                            text = selectedLanguage.displayName.substringBefore(" "),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "CHANGE ▾",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = themePrimary,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = if (isDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Tap to open language menu",
                                    tint = themePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        ExposedDropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            TargetLanguage.values().forEach { lang ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Text(text = lang.flagEmoji, fontSize = 20.sp)
                                            Column {
                                                Text(
                                                    text = lang.displayName,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 14.sp
                                                )
                                                Text(
                                                    text = lang.localizedName,
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        selectedLanguage = lang
                                        isDropdownExpanded = false
                                        performTranslation()
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }
            }

            // Quick Access Language Signifiers (Visual Filter Pills)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TargetLanguage.quickLanguages.forEach { lang ->
                    val isSelected = selectedLanguage == lang
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedLanguage = lang
                            performTranslation()
                        },
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(lang.flagEmoji, fontSize = 13.sp)
                                Text(
                                    text = lang.displayName.substringBefore(" "),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = themePrimary,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("quick_lang_${lang.name.lowercase()}")
                    )
                }
            }

            // SIGNIFIER 2: Input Box with State Indicator Signifiers
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bangla_input_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Bar with Explicit Action Signifiers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "BANGLA INPUT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.6.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (text.isNotBlank()) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = if (text.isNotBlank()) "ACTIVE" else "EMPTY",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (text.isNotBlank()) themePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Labeled Signifiers for Paste & Clear with 48dp Touch Affordance
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Paste Affordance
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { pasteFromClipboard() }
                                    .testTag("paste_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentPaste,
                                        contentDescription = "Paste text",
                                        tint = themeDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Paste",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = themeDark
                                    )
                                }
                            }

                            // Clear Affordance
                            if (text.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            text = ""
                                            result = null
                                        }
                                        .testTag("clear_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear text",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Clear",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = text,
                        onValueChange = { if (it.length <= 4000) text = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp)
                            .testTag("bangla_text_field"),
                        placeholder = {
                            Text(
                                "Type or paste Bangla here... (Uncensored: street slang, idioms, swear words supported)",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            focusedBorderColor = themePrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Live Counter Signifier
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val wordCount = if (text.isBlank()) 0 else text.trim().split(Regex("\\s+")).size
                        Text(
                            text = "📊 $wordCount ${if (wordCount == 1) "word" else "words"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "${text.length} / 4000 characters",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // SIGNIFIER 3: Tone Register Cards with Explicit Radio Selection Signifiers
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "TRANSLATION REGISTER & TONE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "(SELECT ONE)",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = themePrimary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Tone.values().forEach { t ->
                        val isSelected = tone == t
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .clickable {
                                    tone = t
                                    performTranslation()
                                }
                                .testTag("tone_chip_${t.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) themePrimary else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) BorderStroke(2.dp, themeDark) else CardDefaults.outlinedCardBorder()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // Geometric state signifier for color-blind accessibility
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = if (isSelected) "Selected" else "Unselected",
                                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = t.label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = t.hint,
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // AFFORDANCE & SIGNIFIER: Primary Action Button with Explicit Affordance State
            val isTranslateAfforded = text.isNotBlank() && !isTranslating
            Button(
                onClick = { performTranslation() },
                enabled = isTranslateAfforded,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("translate_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themePrimary,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 1.dp,
                    disabledElevation = 0.dp
                )
            ) {
                if (isTranslating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Translating with Gemini 3.8…",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else if (text.isBlank()) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "Enter text to enable translation",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Enter Bangla text above to translate",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Translate to ${selectedLanguage.displayName} →",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quick Samples with Tap Signifier
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Tap any sample phrase to load:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                samplePhraseCategories.forEach { (catTitle, phrases) ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = catTitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeDark
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            phrases.forEach { sample ->
                                Surface(
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            text = sample
                                            performTranslation()
                                        }
                                        .widthIn(max = 260.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = CardDefaults.outlinedCardBorder()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NorthEast,
                                            contentDescription = "Use phrase",
                                            tint = themePrimary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = sample,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Shimmer Loading Signifier
            AnimatedVisibility(
                visible = isTranslating,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, themeAccent.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = themePrimary
                            )
                            Text(
                                text = "Analyzing linguistic nuances, register, and idioms…",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = themePrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        )
                    }
                }
            }

            // SIGNIFIER 5: Translation Output with Clear Action Labels & Engine State Beacon
            AnimatedVisibility(
                visible = result != null && result?.translation?.isNotBlank() == true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                result?.let { currentResult ->
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Peak-End Rule: Animated Feedback Signifier
                        AnimatedVisibility(
                            visible = copiedField != null,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = themePrimary,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${copiedField ?: "Text"} copied to clipboard!",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // NATURAL MAPPING: Source-to-Target Data Flow Connector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = themePrimary.copy(alpha = 0.35f))
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, themePrimary.copy(alpha = 0.4f)),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "🇧🇩 Source",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "Maps to target",
                                        tint = themePrimary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "${selectedLanguage.flagEmoji} ${selectedLanguage.displayName.substringBefore(" ")}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = themeDark
                                    )
                                    Text(
                                        text = "(${tone.label})",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            HorizontalDivider(modifier = Modifier.weight(1f), color = themePrimary.copy(alpha = 0.35f))
                        }

                        // Primary Translation Card with Direct Spatial Mapping
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("result_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = if (isSpeaking) BorderStroke(2.dp, themeAccent) else BorderStroke(1.5.dp, themePrimary)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Top status bar: Language + Engine State Signifier
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = selectedLanguage.flagEmoji, fontSize = 18.sp)
                                        Text(
                                            text = "${selectedLanguage.displayName.uppercase()} (${tone.label.uppercase()})",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    // Engine State Beacon Signifier (Green = AI Cloud, Amber = Local Engine)
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (currentResult.isGeminiPowered) Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                                        border = BorderStroke(1.dp, if (currentResult.isGeminiPowered) Color(0xFF4CAF50) else Color(0xFFFFB300))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(if (currentResult.isGeminiPowered) Color(0xFF2E7D32) else Color(0xFFF57F17))
                                            )
                                            Text(
                                                text = if (currentResult.isGeminiPowered) "Gemini 3.8 AI" else "Local Engine",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (currentResult.isGeminiPowered) Color(0xFF2E7D32) else Color(0xFFF57F17)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // PERCEIVABLE AFFORDANCE: Native OS text selection handles & copy popup
                                SelectionContainer {
                                    Text(
                                        text = currentResult.translation,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        lineHeight = 28.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TouchApp,
                                        contentDescription = null,
                                        tint = themePrimary.copy(alpha = 0.8f),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Text selectable • Long-press or drag handles to highlight words",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                currentResult.pronunciation?.let { pron ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.RecordVoiceOver,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = themeAccent
                                        )
                                        Text(
                                            text = pron,
                                            fontSize = 13.sp,
                                            fontStyle = FontStyle.Italic,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                currentResult.detectedDialect?.let { dialect ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = "DIALECT: $dialect",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(10.dp))

                                // Bottom Action Bar with EXPLICIT Labeled Signifiers
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 1. Audio Speaker Signifier with 1:1 Auditory-Visual Mapping
                                    OutlinedButton(
                                        onClick = {
                                            if (isSpeaking) {
                                                tts?.stop()
                                                isSpeaking = false
                                            } else {
                                                speakText(currentResult.translation, selectedLanguage)
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        colors = if (isSpeaking) ButtonDefaults.outlinedButtonColors(
                                            containerColor = themePrimary.copy(alpha = 0.15f)
                                        ) else ButtonDefaults.outlinedButtonColors(),
                                        modifier = Modifier.minimumInteractiveComponentSize().testTag("tts_button")
                                    ) {
                                        Icon(
                                            imageVector = if (isSpeaking) Icons.Default.GraphicEq else Icons.Default.VolumeUp,
                                            contentDescription = if (isSpeaking) "Stop speech" else "Listen audio pronunciation",
                                            modifier = Modifier.size(16.dp),
                                            tint = themePrimary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isSpeaking) "Speaking…" else "Listen",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = themePrimary
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        // 2. Share Signifier
                                        OutlinedButton(
                                            onClick = { shareText(currentResult.translation) },
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                            modifier = Modifier.minimumInteractiveComponentSize().testTag("share_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = "Share", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        }

                                        // 3. Copy Signifier
                                        Button(
                                            onClick = { copyToClipboard(currentResult.translation, selectedLanguage.displayName) },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = themePrimary),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier.minimumInteractiveComponentSize().testTag("copy_button")
                                        ) {
                                            Icon(
                                                imageVector = if (copiedField == selectedLanguage.displayName) Icons.Default.Check else Icons.Default.ContentCopy,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (copiedField == selectedLanguage.displayName) "Copied" else "Copy",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                // Immediate & Unobtrusive Translation Quality Feedback
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isTranslationHelpful != null) {
                                            if (isTranslationHelpful == true) "✓ Marked accurate" else "Feedback noted"
                                        } else {
                                            "Was this translation accurate?"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isTranslationHelpful != null) themePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (isTranslationHelpful != null) FontWeight.Bold else FontWeight.Normal
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                isTranslationHelpful = true
                                                Toast.makeText(context, "Thanks for the feedback! Marked accurate.", Toast.LENGTH_SHORT).show()
                                                FeedbackRepository.submitFeedback(
                                                    com.example.banglatranslator.model.UserFeedback(
                                                        rating = 5,
                                                        category = com.example.banglatranslator.model.FeedbackCategory.TRANSLATION_QUALITY,
                                                        feedbackText = "Accurate translation",
                                                        sourceContext = text,
                                                        translationContext = currentResult.translation
                                                    )
                                                )
                                            },
                                            modifier = Modifier
                                                .size(34.dp)
                                                .testTag("feedback_thumb_up")
                                        ) {
                                            Icon(
                                                imageVector = if (isTranslationHelpful == true) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                                                contentDescription = "Accurate translation",
                                                tint = if (isTranslationHelpful == true) themePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                isTranslationHelpful = false
                                                showFeedbackDialog = true
                                            },
                                            modifier = Modifier
                                                .size(34.dp)
                                                .testTag("feedback_thumb_down")
                                        ) {
                                            Icon(
                                                imageVector = if (isTranslationHelpful == false) Icons.Default.ThumbDown else Icons.Default.ThumbDownOffAlt,
                                                contentDescription = "Inaccurate translation",
                                                tint = if (isTranslationHelpful == false) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = { showFeedbackDialog = true },
                                            modifier = Modifier
                                                .size(34.dp)
                                                .testTag("feedback_dialog_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ChatBubbleOutline,
                                                contentDescription = "Send detailed feedback",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Literal Card
                        if (currentResult.literal.isNotBlank()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "LITERAL WORD-BY-WORD RENDERING",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        IconButton(
                                            onClick = { copyToClipboard(currentResult.literal, "Literal") },
                                            modifier = Modifier
                                                .minimumInteractiveComponentSize()
                                                .size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (copiedField == "Literal") Icons.Default.Check else Icons.Default.ContentCopy,
                                                contentDescription = "Copy Literal",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    SelectionContainer {
                                        Text(
                                            text = currentResult.literal,
                                            fontSize = 14.sp,
                                            fontStyle = FontStyle.Italic,
                                            lineHeight = 20.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        // Insights Card
                        if (currentResult.notes.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "LINGUISTIC & CULTURAL INSIGHTS",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                     currentResult.notes.forEach { note ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "•",
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Text(
                                                text = note,
                                                fontSize = 13.sp,
                                                lineHeight = 19.sp,
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

            // ==========================================
            // RECENT TRANSLATION HISTORY SECTION
            // ==========================================
            if (historyList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recent_history_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Section Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Recent Translations",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "${historyList.size}",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            TextButton(
                                onClick = { showHistorySheet = true },
                                modifier = Modifier.testTag("view_all_history_btn")
                            ) {
                                Text(
                                    text = "View All",
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Preview of latest 3 items
                        val recentItems = historyList.take(3)
                        recentItems.forEach { item ->
                            HistoryItemCard(
                                item = item,
                                onRestore = { restoreFromHistory(item) },
                                onToggleFavorite = { TranslationHistoryRepository.toggleFavorite(item.id) },
                                onDelete = { TranslationHistoryRepository.deleteEntry(item.id) },
                                onCopy = { copyToClipboard(item.translatedText, "history_${item.id}") },
                                onSpeak = {
                                    speakText(item.translatedText, item.targetLanguage)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Full History Bottom Sheet
        if (showHistorySheet) {
            TranslationHistoryBottomSheet(
                historyList = historyList,
                onDismiss = { showHistorySheet = false },
                onRestore = { item ->
                    restoreFromHistory(item)
                    showHistorySheet = false
                },
                onToggleFavorite = { id -> TranslationHistoryRepository.toggleFavorite(id) },
                onDelete = { id -> TranslationHistoryRepository.deleteEntry(id) },
                onClearAll = { TranslationHistoryRepository.clearHistory() },
                onCopy = { content -> copyToClipboard(content, "history_sheet") },
                onSpeak = { content, lang ->
                    speakText(content, lang)
                }
            )
        }

        // Feedback Modal Bottom Sheet
        if (showFeedbackDialog) {
            FeedbackDialog(
                initialSourceContext = text,
                initialTranslationContext = result?.translation,
                onDismiss = { showFeedbackDialog = false },
                onSubmit = { feedback ->
                    FeedbackRepository.submitFeedback(feedback)
                    Toast.makeText(context, "Feedback sent! Thank you.", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

/**
 * Compact item card for history entries
 */
@Composable
fun HistoryItemCard(
    item: TranslationHistoryItem,
    onRestore: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onRestore)
            .testTag("history_item_${item.id}"),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header: Badges & Timestamp & Action icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${item.sourceLanguage} → ${item.targetLanguage.displayName}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    if (!item.detectedDialect.isNullOrEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = item.detectedDialect,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    Text(
                        text = formatRelativeTime(item.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("fav_btn_${item.id}")
                    ) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = if (item.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (item.isFavorite) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("delete_btn_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete from history",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Source Text (Bangla)
            Text(
                text = item.sourceText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Translated Text
            Text(
                text = item.translatedText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Bottom actions bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tap to load into editor ↗",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onSpeak,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Pronounce",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy translation",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Full Translation History Bottom Sheet Modal
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationHistoryBottomSheet(
    historyList: List<TranslationHistoryItem>,
    onDismiss: () -> Unit,
    onRestore: (TranslationHistoryItem) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onDelete: (String) -> Unit,
    onClearAll: () -> Unit,
    onCopy: (String) -> Unit,
    onSpeak: (String, TargetLanguage) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showOnlyFavorites by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val filteredList = remember(historyList, searchQuery, showOnlyFavorites) {
        historyList.filter { item ->
            val matchesFilter = !showOnlyFavorites || item.isFavorite
            val matchesSearch = searchQuery.isBlank() ||
                    item.sourceText.contains(searchQuery, ignoreCase = true) ||
                    item.translatedText.contains(searchQuery, ignoreCase = true) ||
                    (item.detectedDialect?.contains(searchQuery, ignoreCase = true) == true)
            matchesFilter && matchesSearch
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("translation_history_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header row: Title + Clear all + Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Translation History",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (historyList.isNotEmpty()) {
                        IconButton(
                            onClick = { showClearConfirmDialog = true },
                            modifier = Modifier.testTag("clear_all_history_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear all history",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_history_sheet_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("history_search_input"),
                placeholder = { Text("Search translated phrases, slang or dialects...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = !showOnlyFavorites,
                    onClick = { showOnlyFavorites = false },
                    label = { Text("All (${historyList.size})") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.FormatListBulleted,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )

                FilterChip(
                    selected = showOnlyFavorites,
                    onClick = { showOnlyFavorites = true },
                    label = { Text("★ Starred (${historyList.count { it.isFavorite }})") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (showOnlyFavorites) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }

            // Content List or Empty State
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (showOnlyFavorites) Icons.Default.StarBorder else Icons.Default.HistoryEdu,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = if (showOnlyFavorites) "No starred translations yet" else "No matching translations found",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (showOnlyFavorites) "Tap the star icon on any entry to bookmark it" else "Translate phrases to build your local translation history",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    filteredList.forEach { item ->
                        HistoryItemCard(
                            item = item,
                            onRestore = { onRestore(item) },
                            onToggleFavorite = { onToggleFavorite(item.id) },
                            onDelete = { onDelete(item.id) },
                            onCopy = { onCopy(item.translatedText) },
                            onSpeak = { onSpeak(item.translatedText, item.targetLanguage) }
                        )
                    }
                }
            }
        }
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Clear All Translation History?") },
            text = { Text("This will permanently remove all saved translations from this device. Bookmarked items will also be removed.") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAll()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Helper to display relative timestamps (e.g., Just now, 5m ago, 2h ago, Yesterday)
 */
fun formatRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 45 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        else -> {
            val sdf = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
            sdf.format(java.util.Date(timestamp))
        }
    }
}

