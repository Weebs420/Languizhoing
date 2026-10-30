package com.example.banglatranslator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.banglatranslator.ui.MainApp
import com.example.banglatranslator.ui.theme.BanglaTranslatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BanglaTranslatorTheme {
                MainApp()
            }
        }
    }
}
