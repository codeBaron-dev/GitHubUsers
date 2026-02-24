package com.codebaron.githubusers.domain.navigation_config

import kotlinx.serialization.Serializable

interface NavigationRoutes {

    @Serializable
    object Splash : NavigationRoutes

    @Serializable
    object Home : NavigationRoutes

    @Serializable
    data class UserDetail(val userId: Int) : NavigationRoutes
}