package com.codebaron.githubusers.entry_point

import android.app.Application
import com.codebaron.githubusers.domain.koin.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent

class GitHubUserApp: Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(androidContext = this@GitHubUserApp)
        }
    }
}