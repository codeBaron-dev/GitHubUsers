package com.codebaron.githubusers

import androidx.room.RoomDatabaseConstructor
import com.codebaron.githubusers.data.room.git_user_database.GitHubDatabase
import com.codebaron.githubusers.domain.utils.enums.ConnectivityStatus
import kotlinx.coroutines.flow.Flow
import org.koin.core.module.Module

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform


expect class ConnectivityObserver {
    fun observe(): Flow<ConnectivityStatus>
    fun getCurrentStatus(): ConnectivityStatus
}

expect fun getPlatformConnectivityObserver(): ConnectivityObserver

expect val gitHubUserPlatformModule: Module

expect val databaseModules: Module

@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<GitHubDatabase> {
    override fun initialize(): GitHubDatabase
}