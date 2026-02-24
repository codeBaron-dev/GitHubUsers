package com.codebaron.githubusers.domain.internet_config

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import com.codebaron.githubusers.ConnectivityObserver

val LocalConnectivityObserver = compositionLocalOf<ConnectivityObserver> {
    error("ConnectivityObserver not provided")
}

@Composable
fun ProvideConnectivity(
    connectivityObserver: ConnectivityObserver,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalConnectivityObserver provides connectivityObserver) {
        content()
    }
}