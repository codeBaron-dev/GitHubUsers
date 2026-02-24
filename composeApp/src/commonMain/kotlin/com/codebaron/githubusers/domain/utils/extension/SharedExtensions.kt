package com.codebaron.githubusers.domain.utils.extension

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.ui.graphics.vector.ImageVector
import com.codebaron.githubusers.domain.utils.remote_config.ErrorDataTypes

/**
 * Maps error types to user-friendly messages that are easy to understand.
 *
 * @param errorType The error type from ErrorDataTypes
 * @param originalMessage The original error message (optional)
 * @return A user-friendly error message
 */
fun getUserFriendlyErrorMessage(errorType: String, originalMessage: String? = null): String {
    return when (errorType) {
        ErrorDataTypes.Remote.request_timeout.name ->
            "Request timed out. Please check your internet connection and try again."

        ErrorDataTypes.Remote.no_internet.name ->
            "No internet connection. Please check your network settings and try again."

        ErrorDataTypes.Remote.unknown_host.name ->
            "Unable to connect to the server. Please check your internet connection or try again later."

        ErrorDataTypes.Remote.server.name ->
            "Server error occurred. Please try again later or contact support if the problem persists."

        ErrorDataTypes.Remote.unauthorized.name -> originalMessage ?: "Your session has expired. Please log in again to continue."

        ErrorDataTypes.Remote.too_many_requests.name ->
            "Too many requests. Please wait a moment and try again."

        ErrorDataTypes.Remote.serialization.name ->
            "Data processing error. Please try again or contact support if the problem continues."

        ErrorDataTypes.Local.disk_full.name ->
            "Device storage is full. Please free up some space and try again."

        ErrorDataTypes.Local.unknown_error.name ->
            "An unexpected error occurred. Please try again or contact support if the problem persists."

        else -> {
            // For custom error messages or unhandled types, return the original message
            // but make it more user-friendly if it's too technical
            originalMessage?.let { message ->
                when {
                    message.contains("timeout", ignoreCase = true) ->
                        "Connection timed out. Please check your internet and try again."
                    message.contains("network", ignoreCase = true) ->
                        "Network error. Please check your connection and try again."
                    message.contains("connection", ignoreCase = true) ->
                        "Unable to connect. Please check your internet connection and try again."
                    else -> message
                }
            } ?: "An unexpected error occurred. Please try again."
        }
    }
}

fun smoothExitTransition(): ExitTransition {
    return scaleOut(
        targetScale = 0.9f,
        animationSpec = tween(durationMillis = 300)
    ) + fadeOut(animationSpec = tween(300))
}

fun smoothPopEnterTransition(): EnterTransition {
    return slideInVertically(
        initialOffsetY = { it / 4 },
        animationSpec = tween(300)
    ) + fadeIn(animationSpec = tween(300))
}

val SunIcon: ImageVector
    get() = Icons.Filled.LightMode

val MoonIcon: ImageVector
    get() = Icons.Filled.DarkMode