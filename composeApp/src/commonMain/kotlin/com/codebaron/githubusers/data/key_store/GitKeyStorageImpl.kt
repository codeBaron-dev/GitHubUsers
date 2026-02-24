package com.codebaron.githubusers.data.key_store

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings

class GitKeyStorageImpl : GitKeyStorage {
    private val settings: Settings by lazy { Settings() }
    private val observableSettings: ObservableSettings by lazy { settings as ObservableSettings }


    override var isDarkTheme: Boolean?
        get() = settings.getBooleanOrNull(GitKeys.IS_DARK_THEME.key)
        set(value) {
            settings.putBoolean(GitKeys.IS_DARK_THEME.key, value ?: false)
        }

    override fun clearKeyStorage() {
        val savedTheme = isDarkTheme
        settings.clear()
        isDarkTheme = savedTheme
    }
}