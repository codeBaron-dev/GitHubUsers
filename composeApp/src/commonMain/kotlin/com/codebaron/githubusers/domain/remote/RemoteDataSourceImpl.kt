package com.codebaron.githubusers.domain.remote

import com.codebaron.githubusers.data.model.GitHubUsersResponseItem
import com.codebaron.githubusers.domain.utils.extension.BASE_URL
import com.codebaron.githubusers.domain.utils.remote_config.ErrorDataTypes
import com.codebaron.githubusers.domain.utils.remote_config.ServerResultHandler
import com.codebaron.githubusers.domain.utils.remote_config.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

class RemoteDataSourceImpl(private val httpClient: HttpClient) : RemoteDataSource {

    override suspend fun getGitHubUsers(since: Int): ServerResultHandler<List<GitHubUsersResponseItem>, ErrorDataTypes.Remote> {
        return safeCall {
            httpClient.get {
                url("${BASE_URL}/users")
                parameter("since", since)
            }
        }
    }
}