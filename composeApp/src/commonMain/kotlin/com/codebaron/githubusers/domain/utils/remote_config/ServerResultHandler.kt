package com.codebaron.githubusers.domain.utils.remote_config

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.codebaron.githubusers.domain.utils.extension.CustomLoadingDialog
import com.codebaron.githubusers.domain.utils.extension.RemoteNetworkUiEvent

sealed interface ServerResultHandler<out D, out E: ErrorDataTypes> {
    data class Success<out D>(val data: D): ServerResultHandler<D, Nothing>
    data class Error<out E>(val error: String): ServerResultHandler<E, Nothing>
}

inline fun <T, E: ErrorDataTypes, R> ServerResultHandler<T, E>.map(map: (T) -> R): ServerResultHandler<R, E> {
    return when(this) {
        is ServerResultHandler.Error -> ServerResultHandler.Error(error)
        is ServerResultHandler.Success -> ServerResultHandler.Success(map(data))
    }
}

fun <T, E: ErrorDataTypes> ServerResultHandler<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map {  }
}

inline fun <T, E: ErrorDataTypes> ServerResultHandler<T, E>.onSuccess(action: (T) -> Unit): ServerResultHandler<T, E> {
    return when(this) {
        is ServerResultHandler.Error -> this
        is ServerResultHandler.Success -> {
            action(data)
            this
        }
    }
}
inline fun <T, E: ErrorDataTypes> ServerResultHandler<T, E>.onError(action: (String) -> Unit): ServerResultHandler<T, E> {
    return when(this) {
        is ServerResultHandler.Error -> {
            action(error)
            this
        }
        is ServerResultHandler.Success -> this
    }
}

typealias EmptyResult<E> = ServerResultHandler<Unit, E>

/**
 * A composable function that handles UI events (Loading, Success, Error) from ViewModels.
 *
 * This approach uses an event-based pattern where each API call emits a new event object
 * with a unique ID. LaunchedEffect automatically detects when a new event is emitted and
 * triggers the appropriate callback, eliminating the need for manual state tracking.
 *
 * Key Features:
 * - **Automatic Re-triggering**: Each new event object triggers LaunchedEffect naturally
 * - **No Manual State Tracking**: Compose handles state changes automatically
 * - **Works for Repeated Attempts**: Each API call creates a new event with unique ID
 * - **Clean Separation**: ViewModel manages events, UI reacts
 * - **Simple and Predictable**: No complex cleanup or de-duplication logic needed
 *
 * Usage:
 * 1. In ViewModel: Create a StateFlow<UiEvent?> and emit events on state changes
 * 2. In Composable: Collect the event and pass to this function
 * 3. The function handles showing loading dialogs and error messages automatically
 *
 * @param event The current UI event from ViewModel (null when idle)
 * @param showLoadingDialog Whether to show a loading dialog during Loading events
 * @param onSuccess Callback invoked when a Success event is received
 * @param onError Callback invoked when an Error event is received (before showing snackbar)
 */
@Composable
fun HandleUiEvent(
    event: RemoteNetworkUiEvent?,
    showLoadingDialog: Boolean = true,
    onSuccess: () -> Unit = {},
    onError: () -> Unit = {},
    onLoading: () -> Unit = {}
) {
    // Remember the last handled event to avoid reprocessing duplicates
    val lastHandledEvent = remember { mutableStateOf<RemoteNetworkUiEvent?>(null) }

    // Trigger side effects only when a *new distinct* event occurs
    LaunchedEffect(event) {
        if (event != null && event != lastHandledEvent.value) {
            lastHandledEvent.value = event

            when (event) {
                is RemoteNetworkUiEvent.Loading -> onLoading()
                is RemoteNetworkUiEvent.Success -> onSuccess()
                is RemoteNetworkUiEvent.Error -> onError()
            }
        }
    }

    // Draw loading dialog only when needed (separate recomposition path)
    if (event is RemoteNetworkUiEvent.Loading && showLoadingDialog) {
        CustomLoadingDialog(
            showDialog = true,
            message = event.message
        )
    }
}