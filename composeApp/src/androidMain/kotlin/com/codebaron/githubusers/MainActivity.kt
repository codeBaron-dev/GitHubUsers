package com.codebaron.githubusers

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initContext(this)

        setContent {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(
                    lightScrim = if (isSystemInDarkTheme()) Color.WHITE else Color.BLACK,
                    darkScrim = if (isSystemInDarkTheme()) Color.BLACK else Color.WHITE
                ),
                navigationBarStyle = SystemBarStyle.auto(
                    lightScrim = if (isSystemInDarkTheme()) Color.WHITE else Color.BLACK,
                    darkScrim = if (isSystemInDarkTheme()) Color.BLACK else Color.WHITE
                )
            )
            App()
        }
    }
}