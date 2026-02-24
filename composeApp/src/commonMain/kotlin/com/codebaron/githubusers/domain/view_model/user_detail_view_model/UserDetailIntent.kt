package com.codebaron.githubusers.domain.view_model.user_detail_view_model

sealed class UserDetailIntent {
    data class LoadUser(val userId: Int) : UserDetailIntent()
    object NavigateBack : UserDetailIntent()
}
