package com.codebaron.githubusers.domain.view_model.home_view_model

import com.codebaron.githubusers.data.model.GitHubUsersResponseItem

sealed class HomeScreenIntent {
    object LoadUsers : HomeScreenIntent()
    object LoadNextPage : HomeScreenIntent()
    object RefreshUsers : HomeScreenIntent()
    data class SearchUsers(val query: String) : HomeScreenIntent()
    data class NavigateToDetail(val user: GitHubUsersResponseItem) : HomeScreenIntent()
    object ClearSearch : HomeScreenIntent()
    object ToggleSearch : HomeScreenIntent()
}