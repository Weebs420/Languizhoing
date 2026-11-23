package com.example.banglatranslator.ui.screens

import android.content.Context
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
import com.example.banglatranslator.data.TranslationEngine
import com.example.banglatranslator.data.TranslationHistoryRepository
import com.example.banglatranslator.model.TargetLanguage
import com.example.banglatranslator.model.Tone
import com.example.banglatranslator.model.TranslationHistoryItem
import com.example.banglatranslator.model.TranslationResult
import com.example.banglatranslator.ui.TranslatorPrefill
import com.example.banglatranslator.ui.components.FeedbackDialog
import com.example.banglatranslator.ui.theme.TealAccent
import com.example.banglatranslator.ui.theme.TealDark
import com.example.banglatranslator.ui.theme.TealPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslatorScreen(
    modifier: Modifier = Modifier,
    prefill: TranslatorPrefill? = null,
    onPrefillConsumed: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var text by remember { mutableStateOf("ভাই, আজকে অফিসে অনেক প্যারা গেছে। চিল করতে চাই, চলো কোথাও ঘুরে আসি।") }
    var sourceLanguage by remember { mutableStateOf(TargetLanguage.BANGLA) }
    var targetLanguage by remember { mutableStateOf(TargetLanguage.ENGLISH_US) }
    var tone by remember { mutableStateOf(Tone.CASUAL) }
    var activePickerSide by remember { mutableStateOf<String?>(null) }
    var showSamplesDrawer by remember { mutableStateOf(false) }

    var result by remember { mutableStateOf<TranslationResult?>(null) }
    var isTranslating by remember { mutableStateOf(false) }
    var copiedField by remember { mutableStateOf<String?>(null) }
    var swapRotation by remember { mutableFloatStateOf(0f) }
    val historyList by TranslationHistoryRepository.history.collectAsState()
    var showHistorySheet by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var isTranslationHelpful by remember { mutableStateOf<Boolean?>(null) }

    var isGrayscaleAuditMode by remember { mutableStateOf(false) }

    val uxPrefs = remember(context) {
        context.applicationContext.getSharedPreferences("translator_ux_prefs", Context.MODE_PRIVATE)
    }
    var useClassicBanglaLayout by remember {
        mutableStateOf(uxPrefs.getBoolean("use_classic_bangla_layout", false))
    }
    var showTransitionNotice by remember {
        mutableStateOf(!uxPrefs.getBoolean("dismissed_transition_notice", false))
    }
    var recentLanguages by remember {
        val savedCodes = uxPrefs.getString("recent_language_codes", null)
        val initial = if (!savedCodes.isNullOrBlank()) {
            savedCodes.split(",").mapNotNull { TargetLanguage.fromCodeOrName(it) }
        } else {
            listOf(TargetLanguage.ENGLISH_US, TargetLanguage.SPANISH, TargetLanguage.ARABIC, TargetLanguage.HINDI)
        }
        mutableStateOf(initial)
    }

    fun recordRecentLanguage(lang: TargetLanguage) {
        if (lang == TargetLanguage.AUTO_DETECT) return
        val updated = (listOf(lang) + recentLanguages.filter { it != lang }).take(4)
        recentLanguages = updated
        uxPrefs.edit().putString("recent_language_codes", updated.joinToString(",") { it.code }).apply()
    }

    fun setClassicLayoutMode(classic: Boolean) {
        useClassicBanglaLayout = classic
        uxPrefs.edit().putBoolean("use_classic_bangla_layout", classic).apply()
        if (classic) {
            if (sourceLanguage != TargetLanguage.BANGLA && targetLanguage != TargetLanguage.BANGLA) {
                sourceLanguage = TargetLanguage.BANGLA
                if (targetLanguage == TargetLanguage.BANGLA) {
                    targetLanguage = TargetLanguage.ENGLISH_US
                }
            }
        }
    }

    val themePrimary = if (isGrayscaleAuditMode) Color(0xFF334155) else TealPrimary
    val themeDark = if (isGrayscaleAuditMode) Color(0xFF0F172A) else TealDark
    val themeAccent = if (isGrayscaleAuditMode) Color(0xFF475569) else TealAccent
    val themeGradient = if (isGrayscaleAuditMode) {
        listOf(Color(0xFF1E293B), Color(0xFF475569))
    } else {
        listOf(TealDark, TealPrimary)
    }

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

    fun speakText(content: String, language: TargetLanguage, resolvedTextForAuto: String = content) {
        val effectiveLanguage = if (language == TargetLanguage.AUTO_DETECT) {
            result?.sourceLanguage?.takeIf { it != TargetLanguage.AUTO_DETECT }
                ?: TranslationEngine.detectLanguage(resolvedTextForAuto)
        } else {
            language
        }
        tts?.let { engine ->
            engine.language = effectiveLanguage.toLocale()
            isSpeaking = true
            engine.speak(content, TextToSpeech.QUEUE_FLUSH, null, "tts_utterance_${System.currentTimeMillis()}")
            Toast.makeText(context, "Playing ${effectiveLanguage.displayName} audio pronunciation…", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, "Speech engine initializing…", Toast.LENGTH_SHORT).show()
        }
    }

    fun performTranslation() {
        if (text.isBlank() || isTranslating) return
        coroutineScope.launch {
            isTranslating = true
            isTranslationHelpful = null
            val currentSource = sourceLanguage
            val currentTarget = if (targetLanguage == TargetLanguage.AUTO_DETECT) TargetLanguage.ENGLISH_US else targetLanguage
            val currentSourceText = text
            val res = GeminiTranslationService.translate(
                banglaText = currentSourceText,
                targetLanguage = currentTarget,
                tone = tone,
                isBanglaToTarget = currentSource == TargetLanguage.BANGLA,
                sourceLanguage = currentSource
            )
            result = res
            isTranslating = false
            if (res.translation.isNotBlank()) {
                TranslationHistoryRepository.addEntry(
                    TranslationHistoryItem(
                        sourceText = currentSourceText,
                        translatedText = res.translation,
                        sourceLanguage = res.detectedSourceLanguage ?: res.sourceLanguage.displayName,
                        sourceLangCode = res.sourceLanguage.code,
                        targetLanguage = currentTarget,
                        tone = tone,
                        detectedDialect = res.detectedDialect,
                        isGeminiPowered = res.isGeminiPowered,
                        literalMeaning = res.literal,
                        pronunciation = res.pronunciation,
                        isBanglaToTarget = res.isBanglaToTarget
                    )
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        FeedbackRepository.init(context)
        TranslationHistoryRepository.init(context)
        if (prefill == null) {
            performTranslation()
        }
    }

    LaunchedEffect(prefill?.id) {
        if (prefill != null) {
            text = prefill.text
            sourceLanguage = prefill.sourceLanguage
            targetLanguage = prefill.targetLanguage
            onPrefillConsumed()
            performTranslation()
        }
    }

    fun restoreFromHistory(item: TranslationHistoryItem) {
        val resolvedSource = item.resolvedSourceLanguage()
        text = item.sourceText
        sourceLanguage = resolvedSource
        targetLanguage = item.targetLanguage
        tone = item.tone
        isTranslationHelpful = null
        result = TranslationResult(
            originalBangla = item.sourceText,
            translation = item.translatedText,
            literal = item.literalMeaning,
            sourceLanguage = resolvedSource,
            targetLanguage = item.targetLanguage,
            tone = item.tone,
            detectedDialect = item.detectedDialect,
            pronunciation = item.pronunciation,
            isGeminiPowered = item.isGeminiPowered,
            detectedSourceLanguage = if (!item.sourceLanguage.equals(resolvedSource.displayName, ignoreCase = true)) item.sourceLanguage else null,
            isBanglaToTarget = item.isBanglaToTarget
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

    data class SampleEntry(val phrase: String, val lang: TargetLanguage)

    val samplePhraseCategories = listOf(
        "🌍 Multilingual Samples" to listOf(
            SampleEntry("How are you doing today?", TargetLanguage.ENGLISH_US),
            SampleEntry("¿Cómo estás? Muchas gracias.", TargetLanguage.SPANISH),
            SampleEntry("مرحبا، كيف حالك؟ شكراً جزيلاً", TargetLanguage.ARABIC),
            SampleEntry("आप कैसे हैं? धन्यवाद!", TargetLanguage.HINDI),
            SampleEntry("Bonjour ! Comment ça va ?", TargetLanguage.FRENCH),
            SampleEntry("おはようございます！お元気ですか？", TargetLanguage.JAPANESE)
        ),
        "🔥 Bangla Raw & Slang" to listOf(
            SampleEntry("ধুর বাল! প্যারা নাই, চিল!", TargetLanguage.BANGLA),
            SampleEntry("কী বাল করছো ভাই?", TargetLanguage.BANGLA),
            SampleEntry("বেশি চোদাস না, কাজ কর।", TargetLanguage.BANGLA)
        ),
        "💼 Everyday & Gen Z Bangla" to listOf(
            SampleEntry("ভাই, আজকে অফিসে অনেক প্যারা গেছে। চিল করতে চাই, চলো কোথাও ঘুরে আসি।", TargetLanguage.BANGLA),
            SampleEntry("পরীক্ষায় একবারে কোপ দিয়া আসছি!", TargetLanguage.BANGLA),
            SampleEntry("আমার জন্য একটু wait করো, আমি প্রায় চলে এসেছি।", TargetLanguage.BANGLA)
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header: Clear Identity & Purposeful Utilities
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = themeGradient
                    ),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ব⇄",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bangla & Global AI Translator",
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Any-to-any language translation with slang, idioms & honorifics",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Utility & Status Header Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Grayscale / Mono Audit Mode toggle pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isGrayscaleAuditMode) Color.White else Color.White.copy(alpha = 0.22f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { isGrayscaleAuditMode = !isGrayscaleAuditMode }
                            .testTag("grayscale_audit_toggle")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Contrast,
                                contentDescription = "Toggle Grayscale Audit Mode",
                                tint = if (isGrayscaleAuditMode) Color(0xFF0F172A) else Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (isGrayscaleAuditMode) "Mono ON" else "Mono OFF",
                                color = if (isGrayscaleAuditMode) Color(0xFF0F172A) else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // History quick button
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.22f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showHistorySheet = true }
                            .testTag("header_history_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "View Translation History",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "History (${historyList.size})",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Feedback quick button
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.22f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showFeedbackDialog = true }
                            .testTag("header_feedback_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Feedback,
                                contentDescription = "Share Feedback",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Feedback",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Familiar Version Switcher Pill (minimizes transition mismatch)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (useClassicBanglaLayout) Color.White else Color.White.copy(alpha = 0.22f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { setClassicLayoutMode(!useClassicBanglaLayout) }
                            .testTag("layout_version_toggle")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = if (useClassicBanglaLayout) Icons.Default.Translate else Icons.Default.Public,
                                contentDescription = "Switch translator layout version",
                                tint = if (useClassicBanglaLayout) TealDark else Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (useClassicBanglaLayout) "Classic Bangla" else "Universal Any⇄Any",
                                color = if (useClassicBanglaLayout) TealDark else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Engine & Capabilities Header Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                        modifier = Modifier.testTag("header_model_pill")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Gemini 3.8 · Any⇄Any",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Uncensored / Direct Register Header Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                        modifier = Modifier.testTag("header_register_pill")
                    ) {
                        Text(
                            text = "Uncensored · Natural Mapping",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Smooth Version Transition Strip: lets returning users stay on Classic Bangla ⇄ Target or switch anytime
        AnimatedVisibility(
            visible = useClassicBanglaLayout || showTransitionNotice,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp)
                    .testTag("version_transition_banner"),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                border = BorderStroke(1.dp, themePrimary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHorizontalCircle,
                            contentDescription = null,
                            tint = themePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (useClassicBanglaLayout) {
                                "Using Classic Bangla ⇄ Partner layout."
                            } else {
                                "New: Any-to-Any & Auto-Detect. Prefer Classic Bangla?"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = themeDark,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TextButton(
                            onClick = { setClassicLayoutMode(!useClassicBanglaLayout) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (useClassicBanglaLayout) "Try Any⇄Any" else "Use Classic",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = themePrimary
                            )
                        }
                        if (!useClassicBanglaLayout) {
                            IconButton(
                                onClick = {
                                    showTransitionNotice = false
                                    uxPrefs.edit().putBoolean("dismissed_transition_notice", true).apply()
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss layout notice",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Grayscale / Mono Audit Mode Banner (verifies visual hierarchy without color)
        AnimatedVisibility(
            visible = isGrayscaleAuditMode,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp)
                    .testTag("grayscale_audit_banner"),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E293B),
                border = BorderStroke(1.dp, Color(0xFF475569))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Grayscale audit active",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "GRAYSCALE / MONO ON — AUDIT BANNER",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Hierarchy checked via typography scale, weights, borders & contrast without hue.",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    TextButton(
                        onClick = { isGrayscaleAuditMode = false },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Exit",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
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
            // 1. UNIVERSAL LANGUAGE STAGE (Spatial Mapping: Any Source ⇄ Any Target + Quick Target Chips)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bilateral_language_bar"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "1. Language Pair · Source (Left) translates into Target (Right). Tap ⇄ to reverse flow.",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left Slot: Classic locked Bangla or Universal independent Source Picker
                        if (useClassicBanglaLayout && sourceLanguage == TargetLanguage.BANGLA) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                                border = BorderStroke(1.dp, themePrimary.copy(alpha = 0.35f)),
                                modifier = Modifier.weight(1.1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "🇧🇩", fontSize = 18.sp)
                                    Column {
                                        Text(
                                            text = "Bangla",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = themeDark,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "SOURCE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                                border = BorderStroke(1.5.dp, themePrimary.copy(alpha = 0.55f)),
                                modifier = Modifier
                                    .weight(1.1f)
                                    .minimumInteractiveComponentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { activePickerSide = "source" }
                                    .testTag("source_language_pill")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = sourceLanguage.flagEmoji, fontSize = 18.sp)
                                        Column {
                                            Text(
                                                text = if (sourceLanguage == TargetLanguage.AUTO_DETECT) {
                                                    "Auto-Detect"
                                                } else {
                                                    sourceLanguage.displayName.substringBefore(" ")
                                                },
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = themeDark,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "SOURCE ▾",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = themePrimary
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Choose source language",
                                        tint = themePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Middle: Genuine Bidirectional Swap Button
                        Surface(
                            shape = CircleShape,
                            color = themePrimary.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, themePrimary.copy(alpha = 0.45f)),
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .minimumInteractiveComponentSize()
                                .size(44.dp)
                                .clip(CircleShape)
                                .clickable {
                                    swapRotation += 180f
                                    val resolvedOldSource = if (sourceLanguage == TargetLanguage.AUTO_DETECT) {
                                        result?.sourceLanguage?.takeIf { it != TargetLanguage.AUTO_DETECT }
                                            ?: if (targetLanguage != TargetLanguage.BANGLA) TargetLanguage.BANGLA else TargetLanguage.ENGLISH_US
                                    } else {
                                        sourceLanguage
                                    }
                                    val oldTarget = targetLanguage
                                    val previousOutput = result?.translation?.trim()
                                    sourceLanguage = oldTarget
                                    targetLanguage = resolvedOldSource
                                    if (!previousOutput.isNullOrBlank()) {
                                        text = previousOutput
                                    }
                                    if (text.isNotBlank()) {
                                        performTranslation()
                                    }
                                }
                                .testTag("swap_languages_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Swap source and target languages",
                                    tint = themePrimary,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .rotate(swapRotation)
                                )
                            }
                        }

                        // Right Slot: Classic locked Bangla or Universal independent Target Picker
                        if (useClassicBanglaLayout && targetLanguage == TargetLanguage.BANGLA) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, themePrimary.copy(alpha = 0.35f)),
                                modifier = Modifier.weight(1.1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "🇧🇩", fontSize = 18.sp)
                                    Column {
                                        Text(
                                            text = "Bangla",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "TARGET",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = themePrimary
                                        )
                                    }
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.5.dp, themePrimary.copy(alpha = 0.55f)),
                                modifier = Modifier
                                    .weight(1.1f)
                                    .minimumInteractiveComponentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { activePickerSide = "target" }
                                    .testTag("target_language_pill")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = targetLanguage.flagEmoji, fontSize = 18.sp)
                                        Column {
                                            Text(
                                                text = targetLanguage.displayName.substringBefore(" "),
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "TARGET ▾",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = themePrimary
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Choose target language",
                                        tint = themePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Quick-Switch Language Pills (partner-focused in Classic mode, universal target in Any⇄Any mode)
                    val chipLanguages = if (useClassicBanglaLayout) {
                        TargetLanguage.quickLanguages.filter { it != TargetLanguage.BANGLA }
                    } else {
                        TargetLanguage.quickLanguages
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        chipLanguages.forEach { lang ->
                            val isSelected = if (useClassicBanglaLayout && targetLanguage == TargetLanguage.BANGLA) {
                                sourceLanguage == lang
                            } else {
                                targetLanguage == lang
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (useClassicBanglaLayout && targetLanguage == TargetLanguage.BANGLA) {
                                        sourceLanguage = lang
                                    } else {
                                        if (sourceLanguage == lang) {
                                            sourceLanguage = targetLanguage
                                        }
                                        targetLanguage = lang
                                    }
                                    recordRecentLanguage(lang)
                                    performTranslation()
                                },
                                label = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(lang.flagEmoji, fontSize = 12.sp)
                                        Text(
                                            text = lang.displayName.substringBefore(" "),
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = themePrimary,
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("quick_lang_${lang.name.lowercase()}")
                            )
                        }
                    }
                }
            }

            // 2. SOURCE COMPOSER CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bangla_input_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Bar with Active Source Language, Detected Pill & Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (sourceLanguage == TargetLanguage.AUTO_DETECT) {
                                    "AUTO-DETECT INPUT"
                                } else {
                                    "${sourceLanguage.displayName.uppercase()} INPUT"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.6.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            val detectedLabel = result?.detectedSourceLanguage
                            if (sourceLanguage == TargetLanguage.AUTO_DETECT && !detectedLabel.isNullOrBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = detectedLabel,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = themeDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Inline Samples Drawer Toggle (always available for any language!)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (showSamplesDrawer) themePrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(
                                    1.dp,
                                    if (showSamplesDrawer) themePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showSamplesDrawer = !showSamplesDrawer }
                                    .testTag("toggle_samples_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = "Sample phrases",
                                        tint = if (showSamplesDrawer) themePrimary else themeDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Samples",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (showSamplesDrawer) themePrimary else themeDark
                                    )
                                }
                            }

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
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
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
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
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

                    // Expandable Samples Drawer inside Composer
                    AnimatedVisibility(
                        visible = showSamplesDrawer,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            samplePhraseCategories.forEach { (catTitle, samples) ->
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
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        samples.forEach { sample ->
                                            Surface(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .clickable {
                                                        text = sample.phrase
                                                        if (sourceLanguage != TargetLanguage.AUTO_DETECT) {
                                                            sourceLanguage = sample.lang
                                                        }
                                                        if (targetLanguage == sample.lang) {
                                                            targetLanguage = if (sample.lang == TargetLanguage.ENGLISH_US) {
                                                                TargetLanguage.BANGLA
                                                            } else {
                                                                TargetLanguage.ENGLISH_US
                                                            }
                                                        }
                                                        showSamplesDrawer = false
                                                        performTranslation()
                                                    }
                                                    .widthIn(max = 240.dp),
                                                shape = RoundedCornerShape(10.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                                border = CardDefaults.outlinedCardBorder()
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Text(text = sample.lang.flagEmoji, fontSize = 13.sp)
                                                    Text(
                                                        text = sample.phrase,
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "2. Enter or paste phrase — idioms, street slang, Banglish & any global language are translated faithfully.",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = text,
                        onValueChange = { if (it.length <= 4000) text = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 116.dp)
                            .testTag("bangla_text_field"),
                        placeholder = {
                            Text(
                                text = when (sourceLanguage) {
                                    TargetLanguage.AUTO_DETECT -> "Type or paste any language — we'll detect it automatically…"
                                    TargetLanguage.BANGLA -> "Type or paste Bangla (slang, idioms, Banglish, or everyday speech)…"
                                    else -> "Type or paste ${sourceLanguage.displayName} to translate into ${targetLanguage.displayName}…"
                                },
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

                    // Live Counter + Familiar Source Listen Speaker Affordance
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val wordCount = if (text.isBlank()) 0 else text.trim().split(Regex("\\s+")).size
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (text.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = themePrimary.copy(alpha = 0.08f),
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { speakText(text, sourceLanguage, resolvedTextForAuto = text) }
                                        .testTag("source_tts_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Listen to source text",
                                            tint = themePrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Listen",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = themePrimary
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "$wordCount ${if (wordCount == 1) "word" else "words"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "${text.length} / 4000",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 3. TONE REGISTER & HONORIFIC MODEL
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (targetLanguage == TargetLanguage.BANGLA) {
                        "BANGLA HONORIFIC & TONE (তুই / তুমি / আপনি)"
                    } else {
                        "TONE REGISTER"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "3. Choose social register to calibrate formality, slang intensity, and pronoun politeness.",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Tone.values().forEach { t ->
                        val isSelected = tone == t
                        val dynamicHint = if (targetLanguage == TargetLanguage.BANGLA) {
                            when (t) {
                                Tone.CASUAL -> "তুই · Friend"
                                Tone.NEUTRAL -> "তুমি · Everyday"
                                Tone.FORMAL -> "আপনি · Polite"
                            }
                        } else {
                            t.hint
                        }
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
                                    text = dynamicHint,
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 4. PRIMARY TRANSLATE ACTION (Result appears immediately below!)
            val isTranslateAfforded = text.isNotBlank() && !isTranslating
            Button(
                onClick = { performTranslation() },
                enabled = isTranslateAfforded,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("translate_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themePrimary,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 3.dp,
                    pressedElevation = 1.dp,
                    disabledElevation = 0.dp
                )
            ) {
                if (isTranslating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Translating…",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else if (text.isBlank()) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "Enter text to translate",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Enter text above to translate",
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
                    val sourceLabel = if (sourceLanguage == TargetLanguage.AUTO_DETECT) {
                        "Auto"
                    } else {
                        sourceLanguage.displayName.substringBefore(" ")
                    }
                    Text(
                        text = "Translate $sourceLabel → ${targetLanguage.displayName}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
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

                        // 5. PRIMARY TRANSLATION OUTPUT (Directly below Translate button)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("result_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = if (isSpeaking) BorderStroke(2.dp, themeAccent) else BorderStroke(1.5.dp, themePrimary)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Top status bar: Output Language + Bookmark Star + Engine Indicator
                                val matchingHistoryItem = historyList.firstOrNull {
                                    it.sourceText.trim() == text.trim() &&
                                        it.targetLanguage == currentResult.targetLanguage &&
                                        it.tone == currentResult.tone
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = currentResult.targetLanguage.flagEmoji,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = "${currentResult.targetLanguage.displayName.uppercase()} (${currentResult.tone.label.uppercase()})",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (matchingHistoryItem != null) {
                                            IconButton(
                                                onClick = {
                                                    TranslationHistoryRepository.toggleFavorite(matchingHistoryItem.id)
                                                    val nextMsg = if (matchingHistoryItem.isFavorite) {
                                                        "Removed from starred translations"
                                                    } else {
                                                        "Saved to starred translations ★"
                                                    }
                                                    Toast.makeText(context, nextMsg, Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier
                                                    .minimumInteractiveComponentSize()
                                                    .size(32.dp)
                                                    .testTag("result_favorite_button")
                                            ) {
                                                Icon(
                                                    imageVector = if (matchingHistoryItem.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                                    contentDescription = if (matchingHistoryItem.isFavorite) "Remove bookmark" else "Bookmark translation",
                                                    tint = if (matchingHistoryItem.isFavorite) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }

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
                                                    text = if (currentResult.isGeminiPowered) "Gemini AI" else "Offline Engine",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (currentResult.isGeminiPowered) Color(0xFF2E7D32) else Color(0xFFF57F17)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                SelectionContainer {
                                    Text(
                                        text = currentResult.translation,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        lineHeight = 28.sp,
                                        color = MaterialTheme.colorScheme.onSurface
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

                                if (currentResult.detectedDialect != null || currentResult.detectedSourceLanguage != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        currentResult.detectedSourceLanguage?.let { src ->
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Text(
                                                    text = "FROM ${src.uppercase()}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = themeDark,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                        currentResult.detectedDialect?.let { dialect ->
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant
                                            ) {
                                                Text(
                                                    text = "REGISTER: $dialect",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(10.dp))

                                // Bottom Action Bar
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            if (isSpeaking) {
                                                tts?.stop()
                                                isSpeaking = false
                                            } else {
                                                speakText(currentResult.translation, currentResult.targetLanguage)
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
                                        val copyLabel = currentResult.targetLanguage.displayName
                                        Button(
                                            onClick = { copyToClipboard(currentResult.translation, copyLabel) },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = themePrimary),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier.minimumInteractiveComponentSize().testTag("copy_button")
                                        ) {
                                            Icon(
                                                imageVector = if (copiedField == copyLabel) Icons.Default.Check else Icons.Default.ContentCopy,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (copiedField == copyLabel) "Copied" else "Copy",
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

        // Searchable Source / Target Language Picker Bottom Sheet
        activePickerSide?.let { side ->
            val availableLanguages = when {
                useClassicBanglaLayout -> TargetLanguage.targetLanguages.filter { it != TargetLanguage.BANGLA }
                side == "source" -> TargetLanguage.sourceLanguages
                else -> TargetLanguage.targetLanguages
            }
            LanguagePickerBottomSheet(
                title = if (side == "source") "Choose Source Language" else "Choose Target Language",
                languages = availableLanguages,
                recentLanguages = recentLanguages.filter { it in availableLanguages },
                selectedLanguage = if (side == "source") sourceLanguage else targetLanguage,
                onSelect = { chosen ->
                    recordRecentLanguage(chosen)
                    if (side == "source") {
                        if (chosen == targetLanguage) {
                            targetLanguage = if (sourceLanguage != TargetLanguage.AUTO_DETECT) {
                                sourceLanguage
                            } else if (chosen != TargetLanguage.BANGLA) {
                                TargetLanguage.BANGLA
                            } else {
                                TargetLanguage.ENGLISH_US
                            }
                        }
                        sourceLanguage = chosen
                    } else {
                        if (chosen == sourceLanguage) {
                            sourceLanguage = targetLanguage
                        }
                        targetLanguage = chosen
                    }
                    activePickerSide = null
                    if (text.isNotBlank()) {
                        performTranslation()
                    }
                },
                onDismiss = { activePickerSide = null }
            )
        }
    }
}

/**
 * Searchable ModalBottomSheet for selecting any source or target language
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePickerBottomSheet(
    title: String,
    languages: List<TargetLanguage>,
    recentLanguages: List<TargetLanguage>,
    selectedLanguage: TargetLanguage,
    onSelect: (TargetLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredLanguages = remember(languages, searchQuery) {
        if (searchQuery.isBlank()) {
            languages
        } else {
            languages.filter {
                it.displayName.contains(searchQuery, ignoreCase = true) ||
                    it.localizedName.contains(searchQuery, ignoreCase = true) ||
                    it.code.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("language_picker_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close language picker",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("language_picker_search"),
                placeholder = { Text("Search language (e.g. Spanish, Bangla, Arabic)…") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search languages",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear language search")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            // Familiar "Recent languages" quick strip at the top when not searching
            if (searchQuery.isBlank() && recentLanguages.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "RECENT LANGUAGES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        recentLanguages.forEach { recent ->
                            val isRecentSelected = recent == selectedLanguage
                            FilterChip(
                                selected = isRecentSelected,
                                onClick = { onSelect(recent) },
                                label = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(text = recent.flagEmoji, fontSize = 14.sp)
                                        Text(
                                            text = recent.displayName.substringBefore(" "),
                                            fontSize = 12.sp,
                                            fontWeight = if (isRecentSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TealPrimary,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("recent_lang_${recent.name.lowercase()}")
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filteredLanguages.forEach { lang ->
                    val isSelected = lang == selectedLanguage
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = if (isSelected) BorderStroke(1.5.dp, TealPrimary) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelect(lang) }
                            .testTag("picker_lang_${lang.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(text = lang.flagEmoji, fontSize = 22.sp)
                                Column {
                                    Text(
                                        text = lang.displayName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (isSelected) TealDark else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = lang.localizedName,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = TealPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
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
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        val srcLabel = item.resolvedSourceLanguage().displayName.substringBefore(" ")
                        val tgtLabel = item.targetLanguage.displayName.substringBefore(" ")
                        val directionLabel = "$srcLabel → $tgtLabel"
                        Text(
                            text = directionLabel,
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

