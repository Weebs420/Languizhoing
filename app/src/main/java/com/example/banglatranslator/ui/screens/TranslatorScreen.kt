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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.banglatranslator.data.GeminiTranslationService
import com.example.banglatranslator.model.TargetLanguage
import com.example.banglatranslator.model.Tone
import com.example.banglatranslator.model.TranslationResult
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

    // TextToSpeech for multi-sensory pronunciation (Dual Coding Theory)
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val speech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
            }
        }
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
            engine.speak(content, TextToSpeech.QUEUE_FLUSH, null, "tts_utterance_${System.currentTimeMillis()}")
            Toast.makeText(context, "Playing pronunciation…", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, "Speech synthesis initializing…", Toast.LENGTH_SHORT).show()
        }
    }

    fun performTranslation() {
        if (text.isBlank() || isTranslating) return
        coroutineScope.launch {
            isTranslating = true
            result = GeminiTranslationService.translate(text, selectedLanguage, tone)
            isTranslating = false
        }
    }

    // Initial translation on launch
    LaunchedEffect(Unit) {
        performTranslation()
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
        // 1. Header with Psychology & AI Transparency (Jakob's Law & Ethical Disclosure)
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
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                            text = "Idiomatic & Uncensored Translation · Powered by Gemini 3.8",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }

                    // Cognitive Status Badges
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Uncensored Mode Pill
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
                                    contentDescription = "Uncensored",
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

                        // Gemini Model Transparency Badge
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
                                    contentDescription = "Gemini AI",
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
            // 2. JAKOB'S LAW: Universal Bilateral Language Exchange Bar
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
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Source: Bangla (Fixed Source)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🇧🇩", fontSize = 18.sp)
                            Column {
                                Text(
                                    text = "Bangla",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "বাংলা (Source)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // FITTS'S LAW: Swap / Orientation Button with generous touch target (48dp)
                    Box(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable {
                                swapRotation += 180f
                                performTranslation()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Swap orientation",
                            tint = TealPrimary,
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(swapRotation)
                        )
                    }

                    // Target Language Selector Pill
                    ExposedDropdownMenuBox(
                        expanded = isDropdownExpanded,
                        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("target_language_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
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
                                            text = selectedLanguage.localizedName,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                            maxLines = 1
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = if (isDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Select language",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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

            // HICK'S LAW: Quick Access Language Chips (Minimize Cognitive Choice Friction)
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
                            Text(
                                text = "${lang.flagEmoji} ${lang.displayName.substringBefore(" ")}",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealPrimary,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("quick_lang_${lang.name.lowercase()}")
                    )
                }
            }

            // 3. GESTALT COMMON REGION: Input Card (Encapsulating Text + Paste + Clear + Counter)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bangla_input_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BANGLA INPUT (বাংলা)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.6.sp
                        )

                        // FITTS'S LAW: Accessible 48dp action buttons for Paste & Clear
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Paste Button
                            TextButton(
                                onClick = { pasteFromClipboard() },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .height(36.dp)
                                    .testTag("paste_button"),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                colors = ButtonDefaults.textButtonColors(contentColor = TealDark)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Paste",
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Paste", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }

                            // Clear Button
                            if (text.isNotEmpty()) {
                                TextButton(
                                    onClick = {
                                        text = ""
                                        result = null
                                    },
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .height(36.dp)
                                        .testTag("clear_button"),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Clear", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = text,
                        onValueChange = { if (it.length <= 4000) text = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp)
                            .testTag("bangla_text_field"),
                        placeholder = {
                            Text(
                                "Type or paste Bangla here... (Uncensored: street slang, idioms, profanities supported)",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            focusedBorderColor = TealPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // DOHERTY THRESHOLD: Real-time Metric Feedback
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val wordCount = if (text.isBlank()) 0 else text.trim().split(Regex("\\s+")).size
                        Text(
                            text = "$wordCount ${if (wordCount == 1) "word" else "words"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )

                        Text(
                            text = "${text.length} / 4000",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 4. HICK'S LAW: Structured Tone & Register Selector (3 Clear Paths)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "TRANSLATION REGISTER & TONE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

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
                                containerColor = if (isSelected) TealPrimary else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) null else CardDefaults.outlinedCardBorder()
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
                                    val icon = when (t) {
                                        Tone.CASUAL -> Icons.Default.ChatBubbleOutline
                                        Tone.NEUTRAL -> Icons.Default.Balance
                                        Tone.FORMAL -> Icons.Default.BusinessCenter
                                    }
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(13.dp),
                                        tint = if (isSelected) Color.White else TealPrimary
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

            // 5. FITTS'S LAW: Primary Call-to-Action (Generous Size & Thumb Reach)
            Button(
                onClick = { performTranslation() },
                enabled = text.isNotBlank() && !isTranslating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("translate_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
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
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Translate",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Translate to ${selectedLanguage.displayName}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // 6. PROGRESSIVE DISCLOSURE: Categorized Sample Phrases
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Try a sample phrase:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                samplePhraseCategories.forEach { (catTitle, phrases) ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = catTitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TealDark
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
                                    Text(
                                        text = sample,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 2,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 7. DOHERTY THRESHOLD: Skeleton Feedback during translation
            AnimatedVisibility(
                visible = isTranslating,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, TealAccent.copy(alpha = 0.4f))
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
                                color = TealPrimary
                            )
                            Text(
                                text = "Analyzing linguistic nuances, register, and idioms…",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TealPrimary
                            )
                        }

                        // Shimmering skeleton placeholders
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

            // 8. MILLER'S LAW: Chunked Translation Output Cards
            AnimatedVisibility(
                visible = result != null && result?.translation?.isNotBlank() == true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                result?.let { currentResult ->
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Peak-End Rule: Animated Copy Feedback Pill
                        AnimatedVisibility(
                            visible = copiedField != null,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = TealPrimary,
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

                        // Card A: Primary Target Language Translation
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("result_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.5.dp, TealPrimary)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(text = selectedLanguage.flagEmoji, fontSize = 18.sp)
                                        Text(
                                            text = "${selectedLanguage.displayName.uppercase()} (${tone.label.uppercase()})",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    // FITTS'S LAW: High-Affordance 48dp Interactive Actions
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        // DUAL CODING THEORY: Speech/TTS Pronunciation
                                        IconButton(
                                            onClick = { speakText(currentResult.translation, selectedLanguage) },
                                            modifier = Modifier
                                                .minimumInteractiveComponentSize()
                                                .size(40.dp)
                                                .testTag("tts_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = "Listen pronunciation",
                                                tint = TealPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        // Share
                                        IconButton(
                                            onClick = { shareText(currentResult.translation) },
                                            modifier = Modifier
                                                .minimumInteractiveComponentSize()
                                                .size(40.dp)
                                                .testTag("share_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "Share",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Copy
                                        IconButton(
                                            onClick = { copyToClipboard(currentResult.translation, selectedLanguage.displayName) },
                                            modifier = Modifier
                                                .minimumInteractiveComponentSize()
                                                .size(40.dp)
                                                .testTag("copy_button")
                                        ) {
                                            Icon(
                                                imageVector = if (copiedField == selectedLanguage.displayName) Icons.Default.Check else Icons.Default.ContentCopy,
                                                contentDescription = "Copy Translation",
                                                tint = if (copiedField == selectedLanguage.displayName) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = currentResult.translation,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 28.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

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
                                            tint = TealAccent
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
                            }
                        }

                        // Card B: Literal Word-by-Word Translation Card
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

                        // Card C: Cultural & Linguistic Insights
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

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
