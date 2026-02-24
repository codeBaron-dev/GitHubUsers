package com.codebaron.githubusers.presentation.theme

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = DarkTextPrimary,//LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
)

private val DarkColors = darkColorScheme(
    primary = PrimaryColor,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkSurface, //DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val transition = updateTransition(targetState = darkTheme, label = "themeTransition")

    val animatedColorScheme = animateColorScheme(
        transition = transition,
        lightColors = LightColors,
        darkColors = DarkColors
    )

    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        ProvideAdaptiveTypography {
            val fontSizes = AdaptiveFontSizes.fromScale(LocalTypographyScale.current)

            MaterialTheme(
                colorScheme = animatedColorScheme,
                typography = appTypography(fontSizes),
                content = content
            )
        }
    }
}

@Composable
private fun animateColorScheme(
    transition: Transition<Boolean>,
    lightColors: ColorScheme,
    darkColors: ColorScheme
): ColorScheme {
    val spec = tween<Color>(durationMillis = 500, easing = FastOutSlowInEasing)

    val primary by transition.animateColor(label = "primary",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.primary else lightColors.primary }
    val onPrimary by transition.animateColor(label = "onPrimary",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.onPrimary else lightColors.onPrimary }
    val primaryContainer by transition.animateColor(label = "primaryContainer",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.primaryContainer else lightColors.primaryContainer }
    val onPrimaryContainer by transition.animateColor(label = "onPrimaryContainer",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.onPrimaryContainer else lightColors.onPrimaryContainer }
    val secondary by transition.animateColor(label = "secondary",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.secondary else lightColors.secondary }
    val onSecondary by transition.animateColor(label = "onSecondary",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.onSecondary else lightColors.onSecondary }
    val secondaryContainer by transition.animateColor(label = "secondaryContainer",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.secondaryContainer else lightColors.secondaryContainer }
    val onSecondaryContainer by transition.animateColor(label = "onSecondaryContainer",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.onSecondaryContainer else lightColors.onSecondaryContainer }
    val tertiary by transition.animateColor(label = "tertiary",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.tertiary else lightColors.tertiary }
    val onTertiary by transition.animateColor(label = "onTertiary",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.onTertiary else lightColors.onTertiary }
    val tertiaryContainer by transition.animateColor(label = "tertiaryContainer",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.tertiaryContainer else lightColors.tertiaryContainer }
    val onTertiaryContainer by transition.animateColor(label = "onTertiaryContainer",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.onTertiaryContainer else lightColors.onTertiaryContainer }
    val error by transition.animateColor(label = "error",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.error else lightColors.error }
    val onError by transition.animateColor(label = "onError",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.onError else lightColors.onError }
    val errorContainer by transition.animateColor(label = "errorContainer",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.errorContainer else lightColors.errorContainer }
    val onErrorContainer by transition.animateColor(label = "onErrorContainer",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.onErrorContainer else lightColors.onErrorContainer }
    val background by transition.animateColor(label = "background",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.background else lightColors.background }
    val onBackground by transition.animateColor(label = "onBackground",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.onBackground else lightColors.onBackground }
    val surface by transition.animateColor(label = "surface",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.surface else lightColors.surface }
    val onSurface by transition.animateColor(label = "onSurface",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.onSurface else lightColors.onSurface }
    val surfaceVariant by transition.animateColor(label = "surfaceVariant",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.surfaceVariant else lightColors.surfaceVariant }
    val onSurfaceVariant by transition.animateColor(label = "onSurfaceVariant",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.onSurfaceVariant else lightColors.onSurfaceVariant }
    val outline by transition.animateColor(label = "outline",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.outline else lightColors.outline }
    val outlineVariant by transition.animateColor(label = "outlineVariant",
        transitionSpec = { spec }) { isDark -> if (isDark) darkColors.outlineVariant else lightColors.outlineVariant }

    return lightColorScheme().copy(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        tertiary = tertiary,
        onTertiary = onTertiary,
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = onTertiaryContainer,
        error = error,
        onError = onError,
        errorContainer = errorContainer,
        onErrorContainer = onErrorContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        outline = outline,
        outlineVariant = outlineVariant,
    )
}
