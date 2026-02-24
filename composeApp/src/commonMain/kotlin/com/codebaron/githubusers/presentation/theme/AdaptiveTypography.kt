package com.codebaron.githubusers.presentation.theme

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.window.core.layout.WindowWidthSizeClass

/**
 * Enum representing the three typography scale categories
 */
enum class TypographyScale {
    COMPACT,    // Phones - smallest font sizes
    MEDIUM,     // Small tablets, foldables - medium font sizes
    EXPANDED    // Large tablets, desktops - largest font sizes
}

/**
 * CompositionLocal to provide current typography scale throughout the app
 */
val LocalTypographyScale = compositionLocalOf { TypographyScale.COMPACT }

/**
 * Determines the appropriate typography scale based on current window size
 */
@Composable
fun currentTypographyScale(): TypographyScale {
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val widthSizeClass = windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass

    return when (widthSizeClass) {
        WindowWidthSizeClass.COMPACT -> TypographyScale.COMPACT
        WindowWidthSizeClass.MEDIUM -> TypographyScale.MEDIUM
        WindowWidthSizeClass.EXPANDED -> TypographyScale.EXPANDED
        else -> TypographyScale.COMPACT
    }
}

/**
 * Provider composable that makes typography scale available to children
 */
@Composable
fun ProvideAdaptiveTypography(
    content: @Composable () -> Unit
) {
    val typographyScale = currentTypographyScale()

    CompositionLocalProvider(LocalTypographyScale provides typographyScale) {
        content()
    }
}
