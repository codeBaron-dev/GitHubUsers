package com.codebaron.githubusers.data.model.default_response


import com.codebaron.githubusers.data.model.GitHubUsersResponseItem
import kotlinx.serialization.Serializable

@Serializable
data class DefaultResponse(
    val gitHubUsersResponseItem: List<GitHubUsersResponseItem>? = null,
    val error: List<String>? = null,
    val message: String? = null,
    val status: Boolean? = null,
    val statusCode: Int? = null
)