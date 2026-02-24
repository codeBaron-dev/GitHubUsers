package com.codebaron.githubusers

import androidx.compose.ui.window.ComposeUIViewController
import com.codebaron.githubusers.domain.koin.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }