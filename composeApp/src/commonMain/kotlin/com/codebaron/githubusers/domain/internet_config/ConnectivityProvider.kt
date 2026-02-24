package com.codebaron.githubusers.domain.internet_config

import com.codebaron.githubusers.ConnectivityObserver

object ConnectivityProvider {
    private var _connectivityObserver: ConnectivityObserver? = null

    fun initialize(observer: ConnectivityObserver) {
        _connectivityObserver = observer
    }

    val observer: ConnectivityObserver
        get() = _connectivityObserver ?: throw IllegalStateException(
            "ConnectivityObserver not initialized. Call ConnectivityProvider.initialize() first."
        )
}