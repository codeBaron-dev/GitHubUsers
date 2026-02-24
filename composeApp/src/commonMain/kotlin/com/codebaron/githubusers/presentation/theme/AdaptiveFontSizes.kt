package com.codebaron.githubusers.presentation.theme

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Defines font sizes for each typography scale category.
 *
 * Scale factors:
 * - COMPACT (phones < 600dp): Base sizes
 * - MEDIUM (tablets 600dp - 840dp): ~12% larger
 * - EXPANDED (large tablets/desktop > 840dp): ~20% larger
 */
sealed class AdaptiveFontSizes(
    // Display styles
    val displayLarge: TextUnit,
    val displayMedium: TextUnit,
    val displaySmall: TextUnit,

    // Headline styles
    val headlineLarge: TextUnit,
    val headlineMedium: TextUnit,
    val headlineSmall: TextUnit,

    // Title styles
    val titleLarge: TextUnit,
    val titleMedium: TextUnit,
    val titleSmall: TextUnit,

    // Body styles
    val bodyLarge: TextUnit,
    val bodyMedium: TextUnit,
    val bodySmall: TextUnit,

    // Label styles
    val labelLarge: TextUnit,
    val labelMedium: TextUnit,
    val labelSmall: TextUnit
) {
    /**
     * COMPACT - Phones (< 600dp width)
     * Base Material3 defaults optimized for small screens
     */
    data object Compact : AdaptiveFontSizes(
        displayLarge = 57.sp,
        displayMedium = 45.sp,
        displaySmall = 36.sp,
        headlineLarge = 32.sp,
        headlineMedium = 28.sp,
        headlineSmall = 24.sp,
        titleLarge = 22.sp,
        titleMedium = 16.sp,
        titleSmall = 14.sp,
        bodyLarge = 16.sp,
        bodyMedium = 14.sp,
        bodySmall = 12.sp,
        labelLarge = 14.sp,
        labelMedium = 12.sp,
        labelSmall = 11.sp
    )

    /**
     * MEDIUM - Small tablets, foldables (600dp - 840dp width)
     * ~10-15% larger than compact for improved readability
     */
    data object Medium : AdaptiveFontSizes(
        displayLarge = 64.sp,
        displayMedium = 50.sp,
        displaySmall = 40.sp,
        headlineLarge = 36.sp,
        headlineMedium = 32.sp,
        headlineSmall = 27.sp,
        titleLarge = 24.sp,
        titleMedium = 18.sp,
        titleSmall = 15.sp,
        bodyLarge = 18.sp,
        bodyMedium = 16.sp,
        bodySmall = 14.sp,
        labelLarge = 15.sp,
        labelMedium = 13.sp,
        labelSmall = 12.sp
    )

    /**
     * EXPANDED - Large tablets, desktops (> 840dp width)
     * ~20-25% larger than compact for comfortable reading at distance
     */
    data object Expanded : AdaptiveFontSizes(
        displayLarge = 72.sp,
        displayMedium = 56.sp,
        displaySmall = 45.sp,
        headlineLarge = 40.sp,
        headlineMedium = 36.sp,
        headlineSmall = 30.sp,
        titleLarge = 27.sp,
        titleMedium = 20.sp,
        titleSmall = 17.sp,
        bodyLarge = 20.sp,
        bodyMedium = 18.sp,
        bodySmall = 16.sp,
        labelLarge = 17.sp,
        labelMedium = 15.sp,
        labelSmall = 14.sp
    )

    companion object {
        fun fromScale(scale: TypographyScale): AdaptiveFontSizes = when (scale) {
            TypographyScale.COMPACT -> Compact
            TypographyScale.MEDIUM -> Medium
            TypographyScale.EXPANDED -> Expanded
        }
    }
}
