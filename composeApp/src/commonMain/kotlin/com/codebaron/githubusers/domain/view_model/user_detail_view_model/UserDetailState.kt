package com.codebaron.githubusers.domain.view_model.user_detail_view_model

import com.codebaron.githubusers.data.model.GitHubUsersResponseItem

data class UserDetailState(
    val user: GitHubUsersResponseItem? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
