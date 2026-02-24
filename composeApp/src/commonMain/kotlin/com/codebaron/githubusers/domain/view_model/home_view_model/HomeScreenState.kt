package com.codebaron.githubusers.domain.view_model.home_view_model

import com.codebaron.githubusers.data.model.GitHubUsersResponseItem

data class HomeScreenState(
    val users: List<GitHubUsersResponseItem> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isPaginating: Boolean = false,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val errorMessage: String? = null,
    val hasMorePages: Boolean = true
)