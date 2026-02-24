package com.codebaron.githubusers.fake

import com.codebaron.githubusers.data.model.GitHubUsersResponseItem
import com.codebaron.githubusers.domain.remote.RemoteRepository
import com.codebaron.githubusers.domain.utils.remote_config.ErrorDataTypes
import com.codebaron.githubusers.domain.utils.remote_config.ServerResultHandler

/**
 * A fake implementation of [RemoteRepository] used for testing purposes.
 *
 * This class allows for simulating various network scenarios, including successful data retrieval,
 * network errors, and artificial delays, without making actual network calls.
 *
 * @property usersToReturn The list of [GitHubUsersResponseItem] to be returned on a successful call.
 * @property shouldReturnError A flag to determine if the repository should simulate a failure.
 * @property errorMessage The error message to be returned when [shouldReturnError] is true.
 * @property delayMs An optional delay in milliseconds to simulate network latency.
 */
class FakeRemoteRepository : RemoteRepository {

    private var usersToReturn: List<GitHubUsersResponseItem> = emptyList()
    private var shouldReturnError: Boolean = false
    private var errorMessage: String = "Network error"
    private var delayMs: Long = 0L

    fun setUsersToReturn(users: List<GitHubUsersResponseItem>) {
        usersToReturn = users
    }

    fun setShouldReturnError(shouldError: Boolean, message: String = "Network error") {
        shouldReturnError = shouldError
        errorMessage = message
    }

    fun setDelay(delayMs: Long) {
        this.delayMs = delayMs
    }

    override suspend fun getGitHubUsers(since: Int): ServerResultHandler<List<GitHubUsersResponseItem>, ErrorDataTypes.Remote> {
        if (delayMs > 0) {
            kotlinx.coroutines.delay(delayMs)
        }

        return if (shouldReturnError) {
            ServerResultHandler.Error(errorMessage)
        } else {
            val filteredUsers = usersToReturn.filter { it.id > since }
            ServerResultHandler.Success(filteredUsers)
        }
    }
}
