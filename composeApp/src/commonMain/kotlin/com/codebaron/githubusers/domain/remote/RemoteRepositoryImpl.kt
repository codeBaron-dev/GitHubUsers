package com.codebaron.githubusers.domain.remote

import com.codebaron.githubusers.data.model.GitHubUsersResponseItem
import com.codebaron.githubusers.domain.utils.remote_config.ErrorDataTypes
import com.codebaron.githubusers.domain.utils.remote_config.ServerResultHandler
import com.codebaron.githubusers.domain.utils.remote_config.map

class RemoteRepositoryImpl(private val remoteDataSource: RemoteDataSource) : RemoteRepository {

    override suspend fun getGitHubUsers(since: Int): ServerResultHandler<List<GitHubUsersResponseItem>, ErrorDataTypes.Remote> {
        return remoteDataSource.getGitHubUsers(since).map { it }
    }
}