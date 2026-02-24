package com.codebaron.githubusers.data.room.git_user_database

/**
 * Auto Migration Configuration for StakenyDatabase
 * 
 * This file provides configuration for automatic database migrations.
 * 
 * How it works:
 * 1. fallbackToDestructiveMigration() - Automatically recreates the database when schema changes
 * 2. fallbackToDestructiveMigrationOnDowngrade() - Handles version downgrades
 * 3. exportSchema = false - Disables schema export to avoid version management
 * 
 * Benefits:
 * - No manual version updates required
 * - Automatic handling of schema changes
 * - Simplified database management
 * 
 * Trade-offs:
 * - Data will be lost on schema changes (destructive migration)
 * - Not suitable for production apps with critical user data
 * 
 * For production apps with data preservation needs:
 * 1. Set exportSchema = true
 * 2. Manually increment version numbers
 * 3. Provide custom Migration objects
 * 4. Use addMigrations() instead of fallbackToDestructiveMigration()
 */

object AutoMigrationConfig {
    const val USE_DESTRUCTIVE_MIGRATION = true
    const val EXPORT_SCHEMA = true
    
    /**
     * Database version strategy:
     * - For development: Keep version = 1 with destructive migration
     * - For production: Increment version manually and provide migrations
     */
    const val DATABASE_VERSION = 1
}