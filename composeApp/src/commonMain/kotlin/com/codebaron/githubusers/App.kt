package com.codebaron.githubusers

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.codebaron.githubusers.data.key_store.GitKeyStorage
import com.codebaron.githubusers.data.key_store.GitKeyStorageImpl
import com.codebaron.githubusers.domain.internet_config.ProvideConnectivity
import com.codebaron.githubusers.domain.internet_config.rememberConnectivityState
import com.codebaron.githubusers.domain.navigation_config.NavigationRoutes
import com.codebaron.githubusers.domain.utils.extension.smoothExitTransition
import com.codebaron.githubusers.domain.utils.extension.smoothPopEnterTransition
import com.codebaron.githubusers.domain.view_model.home_view_model.HomeScreenViewModel
import com.codebaron.githubusers.domain.view_model.splash_screen_view_model.SplashScreenViewModel
import com.codebaron.githubusers.domain.view_model.user_detail_view_model.UserDetailViewModel
import com.codebaron.githubusers.presentation.home_screen.HomeScreenRoot
import com.codebaron.githubusers.presentation.splash_screen.SplashScreenRoot
import com.codebaron.githubusers.presentation.theme.AppTheme
import com.codebaron.githubusers.presentation.theme.LocalIsDarkTheme
import com.codebaron.githubusers.presentation.theme.LocalToggleTheme
import com.codebaron.githubusers.presentation.user_detail_screen.UserDetailScreenRoot
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val connectivityObserver = remember { getPlatformConnectivityObserver() }
    val systemIsDark = isSystemInDarkTheme()
    val keyValueStorage: GitKeyStorage = GitKeyStorageImpl()
    var isDarkTheme by remember { mutableStateOf(keyValueStorage.isDarkTheme ?: systemIsDark) }
    val toggleTheme: () -> Unit = {
        isDarkTheme = !isDarkTheme
        keyValueStorage.isDarkTheme = isDarkTheme
    }

    ProvideConnectivity(connectivityObserver = connectivityObserver) {
        CompositionLocalProvider(
            LocalToggleTheme provides toggleTheme,
            LocalIsDarkTheme provides isDarkTheme
        ) {
            AppTheme(
                darkTheme = isDarkTheme,
                content = {
                    Scaffold(
                        content = {
                            GitHubUserNavigation()
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun GitHubUserNavigation() {
    val navController = rememberNavController()
    val connectivityState by rememberConnectivityState()
    val keyValueStorage: GitKeyStorage = GitKeyStorageImpl()
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Splash
    ) {
        composable<NavigationRoutes.Splash>(
            exitTransition = { smoothExitTransition() },
            popEnterTransition = { smoothPopEnterTransition() },
            content = {
                val splashScreenViewModel: SplashScreenViewModel = koinInject()
                SplashScreenRoot(
                    navController = navController,
                    splashScreenViewModel = splashScreenViewModel,
                )
            }
        )

        composable<NavigationRoutes.Home>(
            exitTransition = { smoothExitTransition() },
            popEnterTransition = { smoothPopEnterTransition() },
            content = {
                val homeScreenViewModel: HomeScreenViewModel = koinInject()
                HomeScreenRoot(
                    navController = navController,
                    homeScreenViewModel = homeScreenViewModel,
                )
            }
        )

        composable<NavigationRoutes.UserDetail>(
            exitTransition = { smoothExitTransition() },
            popEnterTransition = { smoothPopEnterTransition() },
            content = { backStackEntry ->
                val userDetail: NavigationRoutes.UserDetail = backStackEntry.toRoute()
                val userDetailViewModel: UserDetailViewModel = koinInject()
                UserDetailScreenRoot(
                    navController = navController,
                    userDetailViewModel = userDetailViewModel,
                    userId = userDetail.userId
                )
            }
        )
    }
}