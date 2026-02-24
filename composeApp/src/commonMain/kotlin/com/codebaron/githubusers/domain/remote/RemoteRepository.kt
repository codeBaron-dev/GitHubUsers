package com.codebaron.githubusers.domain.remote

import com.codebaron.githubusers.data.model.GitHubUsersResponseItem
import com.codebaron.githubusers.domain.utils.remote_config.ErrorDataTypes
import com.codebaron.githubusers.domain.utils.remote_config.ServerResultHandler

interface RemoteRepository {

    suspend fun getGitHubUsers(since: Int): ServerResultHandler<List<GitHubUsersResponseItem>, ErrorDataTypes.Remote>
}