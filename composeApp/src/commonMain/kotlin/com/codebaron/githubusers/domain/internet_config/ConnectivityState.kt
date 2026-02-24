package com.codebaron.githubusers.domain.internet_config

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import com.codebaron.githubusers.domain.utils.enums.ConnectivityStatus

@Composable
fun rememberConnectivityState(): State<ConnectivityStatus> {
    val connectivityObserver = LocalConnectivityObserver.current
    return connectivityObserver.observe().collectAsState(
        initial = connectivityObserver.getCurrentStatus()
    )
}