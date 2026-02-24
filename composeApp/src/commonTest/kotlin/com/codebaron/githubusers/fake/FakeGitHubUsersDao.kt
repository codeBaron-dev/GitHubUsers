package com.codebaron.githubusers.fake

import com.codebaron.githubusers.data.model.GitHubUsersResponseItem
import com.codebaron.githubusers.data.room.git_user_database.GitHubUsersDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * A fake implementation of [GitHubUsersDao] that stores GitHub users in memory using [MutableStateFlow].
 * This class is primarily used for testing purposes to simulate database operations without requiring
 * a real Room database instance.
 */
class FakeGitHubUsersDao : GitHubUsersDao {

    private val usersFlow = MutableStateFlow<List<GitHubUsersResponseItem>>(emptyList())

    override fun getAllUsers(): Flow<List<GitHubUsersResponseItem>> = usersFlow

    override fun searchUsers(query: String): Flow<List<GitHubUsersResponseItem>> {
        return usersFlow.map { users ->
            users.filter { it.login?.contains(query, ignoreCase = true) == true }
        }
    }

    override suspend fun getUserById(userId: Int): GitHubUsersResponseItem? {
        return usersFlow.value.find { it.id == userId }
    }

    override suspend fun insertUsers(users: List<GitHubUsersResponseItem>) {
        val currentUsers = usersFlow.value.toMutableList()
        users.forEach { newUser ->
            val existingIndex = currentUsers.indexOfFirst { it.id == newUser.id }
            if (existingIndex >= 0) {
                currentUsers[existingIndex] = newUser
            } else {
                currentUsers.add(newUser)
            }
        }
        usersFlow.value = currentUsers.sortedBy { it.id }
    }

    override suspend fun clearAllUsers() {
        usersFlow.value = emptyList()
    }

    override suspend fun getLastUserId(): Int? {
        return usersFlow.value.maxOfOrNull { it.id }
    }

    fun setUsers(users: List<GitHubUsersResponseItem>) {
        usersFlow.value = users
    }
}
