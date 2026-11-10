package com.example.banglatranslator.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.banglatranslator.ui.screens.*
import com.example.banglatranslator.ui.theme.TealDark
import com.example.banglatranslator.ui.theme.TealPrimary

enum class AppTab(val title: String, val icon: ImageVector) {
    TRANSLATE("Translate", Icons.Default.GTranslate),
    PHRASEBOOK("Phrases", Icons.Default.AutoStories),
    VOCABULARY("Vocab", Icons.Default.MenuBook),
    COACHING("Coach", Icons.Default.WorkspacePremium),
    GUIDE("Guide", Icons.Default.School)
}

@Composable
fun MainApp() {
    var currentTab by remember { mutableStateOf(AppTab.TRANSLATE) }

    // Intercept back button to return to Translate if on secondary tab
    if (currentTab != AppTab.TRANSLATE) {
        BackHandler {
            currentTab = AppTab.TRANSLATE
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = NavigationBarDefaults.Elevation
            ) {
                AppTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        alwaysShowLabel = true,
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TealPrimary,
                            selectedTextColor = TealDark,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        val screenModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when (currentTab) {
            AppTab.TRANSLATE -> TranslatorScreen(modifier = screenModifier)
            AppTab.PHRASEBOOK -> PhrasebookScreen(modifier = screenModifier)
            AppTab.VOCABULARY -> VocabularyScreen(modifier = screenModifier)
            AppTab.COACHING -> CoachingScreen(modifier = screenModifier)
            AppTab.GUIDE -> GrammarGuideScreen(modifier = screenModifier)
        }
    }
}
