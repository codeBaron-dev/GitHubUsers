package com.codebaron.githubusers.domain.koin

import com.codebaron.githubusers.data.key_store.GitKeyStorage
import com.codebaron.githubusers.data.key_store.GitKeyStorageImpl
import com.codebaron.githubusers.data.room.git_user_database.GitHubDatabase
import com.codebaron.githubusers.data.room.git_user_database.GitHubUsersDao
import com.codebaron.githubusers.data.room.git_user_database.getRoomDatabase
import com.codebaron.githubusers.databaseModules
import com.codebaron.githubusers.domain.remote.RemoteDataSource
import com.codebaron.githubusers.domain.remote.RemoteDataSourceImpl
import com.codebaron.githubusers.domain.remote.RemoteRepository
import com.codebaron.githubusers.domain.remote.RemoteRepositoryImpl
import com.codebaron.githubusers.domain.utils.remote_config.HttpClientFactory
import com.codebaron.githubusers.domain.view_model.home_view_model.HomeScreenViewModel
import com.codebaron.githubusers.domain.view_model.splash_screen_view_model.SplashScreenViewModel
import com.codebaron.githubusers.domain.view_model.user_detail_view_model.UserDetailViewModel
import com.codebaron.githubusers.gitHubUserPlatformModule
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

val gitHubUsersModules = module {
    viewModelOf(constructor = ::SplashScreenViewModel)
    viewModel { HomeScreenViewModel(remoteRepository = get(), gitHubUsersDao = get()) }
    viewModel {UserDetailViewModel(gitHubUsersDao = get())}

    single<GitKeyStorage> { GitKeyStorageImpl() }
    single<GitHubDatabase> { getRoomDatabase(builder = get()) }
    single<GitHubUsersDao> { get<GitHubDatabase>().gitHubUsersDao() }
    single { HttpClientFactory.create(engine = get()) }

    singleOf(constructor = ::RemoteDataSourceImpl).bind<RemoteDataSource>()
    singleOf(constructor = ::RemoteRepositoryImpl).bind<RemoteRepository>()
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(gitHubUsersModules, gitHubUserPlatformModule, databaseModules)
    }
}