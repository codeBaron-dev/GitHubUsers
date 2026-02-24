package com.codebaron.githubusers.data.room.git_user_database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codebaron.githubusers.data.model.GitHubUsersResponseItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GitHubUsersDao {

    @Query("SELECT * FROM git_hub_users ORDER BY id ASC")
    fun getAllUsers(): Flow<List<GitHubUsersResponseItem>>

    @Query("SELECT * FROM git_hub_users WHERE login LIKE '%' || :query || '%' ORDER BY id ASC")
    fun searchUsers(query: String): Flow<List<GitHubUsersResponseItem>>

    @Query("SELECT * FROM git_hub_users WHERE id = :userId")
    suspend fun getUserById(userId: Int): GitHubUsersResponseItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<GitHubUsersResponseItem>)

    @Query("DELETE FROM git_hub_users")
    suspend fun clearAllUsers()

    @Query("SELECT MAX(id) FROM git_hub_users")
    suspend fun getLastUserId(): Int?
}
