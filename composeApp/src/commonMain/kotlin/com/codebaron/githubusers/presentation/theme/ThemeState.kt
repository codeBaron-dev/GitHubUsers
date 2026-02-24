package com.codebaron.githubusers.presentation.theme

import androidx.compose.runtime.compositionLocalOf

val LocalIsDarkTheme = compositionLocalOf { false }

val LocalToggleTheme = compositionLocalOf<() -> Unit> { {} }
