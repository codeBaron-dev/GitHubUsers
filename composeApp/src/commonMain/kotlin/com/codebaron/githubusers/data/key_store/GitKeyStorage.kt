package com.codebaron.githubusers.data.key_store

interface GitKeyStorage {

    var isDarkTheme: Boolean?

    fun clearKeyStorage()
}