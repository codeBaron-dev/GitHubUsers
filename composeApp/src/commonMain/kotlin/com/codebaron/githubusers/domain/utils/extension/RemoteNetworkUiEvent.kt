package com.codebaron.githubusers.domain.utils.extension

import kotlin.random.Random

private fun generateId(): String {
    return (1..8).joinToString("") { Random.nextInt(0, 16).toString(16) }
}

sealed class RemoteNetworkUiEvent {
    data class Loading(
        val message: String,
        val id: String = generateId()
    ) : RemoteNetworkUiEvent()

    data class Success(
        val id: String = generateId()
    ) : RemoteNetworkUiEvent()

    data class Error(
        val message: String,
        val id: String = generateId()
    ) : RemoteNetworkUiEvent()
}