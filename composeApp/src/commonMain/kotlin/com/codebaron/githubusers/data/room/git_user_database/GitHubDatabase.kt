package com.codebaron.githubusers.data.room.git_user_database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.codebaron.githubusers.AppDatabaseConstructor
import com.codebaron.githubusers.data.model.GitHubUsersResponseItem

@Database(
    entities = [GitHubUsersResponseItem::class],
    version = AutoMigrationConfig.DATABASE_VERSION,
    exportSchema = AutoMigrationConfig.EXPORT_SCHEMA
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class GitHubDatabase : RoomDatabase() {
    abstract fun gitHubUsersDao(): GitHubUsersDao
}