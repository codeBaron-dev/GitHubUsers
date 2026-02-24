package com.codebaron.githubusers.data.room.git_user_database

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

fun getRoomDatabase(
    builder: RoomDatabase.Builder<GitHubDatabase>
): GitHubDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .apply {
            if (AutoMigrationConfig.USE_DESTRUCTIVE_MIGRATION) {
                fallbackToDestructiveMigration(dropAllTables = true)
                fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
            }
        }
        .build()
}