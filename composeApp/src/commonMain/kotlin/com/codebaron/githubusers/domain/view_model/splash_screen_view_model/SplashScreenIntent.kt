package com.codebaron.githubusers.domain.view_model.splash_screen_view_model

sealed class SplashScreenIntent {
    data object NavigateToHome: SplashScreenIntent()
}