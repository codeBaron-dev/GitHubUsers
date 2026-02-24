package com.codebaron.githubusers.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import githubusers.composeapp.generated.resources.Poppins_Black
import githubusers.composeapp.generated.resources.Poppins_Bold
import githubusers.composeapp.generated.resources.Poppins_ExtraBold
import githubusers.composeapp.generated.resources.Poppins_ExtraLight
import githubusers.composeapp.generated.resources.Poppins_Light
import githubusers.composeapp.generated.resources.Poppins_Medium
import githubusers.composeapp.generated.resources.Poppins_Regular
import githubusers.composeapp.generated.resources.Poppins_SemiBold
import githubusers.composeapp.generated.resources.Poppins_Thin
import githubusers.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

// Font styles
val fontStyles = listOf("NORMAL", "BOLD", "ITALIC")

@Composable
fun poppinsFontType() = FontFamily(
    Font(Res.font.Poppins_Light, weight = FontWeight.Light),
    Font(Res.font.Poppins_ExtraLight, weight = FontWeight.ExtraLight),
    Font(Res.font.Poppins_Medium, weight = FontWeight.Medium),
    Font(Res.font.Poppins_Bold, weight = FontWeight.Bold),
    Font(Res.font.Poppins_SemiBold, weight = FontWeight.SemiBold),
    Font(Res.font.Poppins_ExtraBold, weight = FontWeight.ExtraBold),
    Font(Res.font.Poppins_Regular, weight = FontWeight.Normal),
    Font(Res.font.Poppins_Black, weight = FontWeight.Black),
    Font(Res.font.Poppins_Thin, weight = FontWeight.Thin),
)

/**
 * Creates Typography with adaptive font sizes based on the provided scale
 */
@Composable
fun appTypography(fontSizes: AdaptiveFontSizes = AdaptiveFontSizes.Compact): Typography {
    val fontFamily = poppinsFontType()

    return Typography(
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = fontSizes.displayLarge,
            lineHeight = fontSizes.displayLarge * 1.12f
        ),
        displayMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = fontSizes.displayMedium,
            lineHeight = fontSizes.displayMedium * 1.16f
        ),
        displaySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = fontSizes.displaySmall,
            lineHeight = fontSizes.displaySmall * 1.22f
        ),
        headlineLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = fontSizes.headlineLarge,
            lineHeight = fontSizes.headlineLarge * 1.25f
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = fontSizes.headlineMedium,
            lineHeight = fontSizes.headlineMedium * 1.29f
        ),
        headlineSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = fontSizes.headlineSmall,
            lineHeight = fontSizes.headlineSmall * 1.33f
        ),
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = fontSizes.titleLarge,
            lineHeight = fontSizes.titleLarge * 1.27f
        ),
        titleMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = fontSizes.titleMedium,
            lineHeight = fontSizes.titleMedium * 1.5f
        ),
        titleSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = fontSizes.titleSmall,
            lineHeight = fontSizes.titleSmall * 1.43f
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = fontSizes.bodyLarge,
            lineHeight = fontSizes.bodyLarge * 1.5f
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = fontSizes.bodyMedium,
            lineHeight = fontSizes.bodyMedium * 1.43f
        ),
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = fontSizes.bodySmall,
            lineHeight = fontSizes.bodySmall * 1.33f
        ),
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = fontSizes.labelLarge,
            lineHeight = fontSizes.labelLarge * 1.43f
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = fontSizes.labelMedium,
            lineHeight = fontSizes.labelMedium * 1.33f
        ),
        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = fontSizes.labelSmall,
            lineHeight = fontSizes.labelSmall * 1.45f
        )
    )
}