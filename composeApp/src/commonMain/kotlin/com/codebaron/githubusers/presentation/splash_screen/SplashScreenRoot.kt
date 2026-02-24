package com.codebaron.githubusers.presentation.splash_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.codebaron.githubusers.domain.navigation_config.NavigationRoutes
import com.codebaron.githubusers.domain.view_model.splash_screen_view_model.SplashScreenIntent
import com.codebaron.githubusers.domain.view_model.splash_screen_view_model.SplashScreenState
import com.codebaron.githubusers.domain.view_model.splash_screen_view_model.SplashScreenViewModel
import githubusers.composeapp.generated.resources.Res
import githubusers.composeapp.generated.resources.git_logo
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreenRoot(
    navController: NavHostController,
    splashScreenViewModel: SplashScreenViewModel,
) {
    val splashScreenState by splashScreenViewModel.state.collectAsState(initial = SplashScreenState())

    SplashScreen(
        splashScreenViewModel = splashScreenViewModel,
        onAction = { intent -> splashScreenViewModel.sendIntent(intent) },
        splashScreenState = splashScreenState,
        navController = navController
    )
}

@Composable
fun SplashScreen(
    splashScreenViewModel: SplashScreenViewModel,
    onAction: (SplashScreenIntent) -> Unit,
    splashScreenState: SplashScreenState,
    navController: NavHostController
) {
    val currentOnAction by rememberUpdatedState(onAction)
    val currentSplashScreenState by rememberUpdatedState(splashScreenState)

    LaunchedEffect(key1 = Unit) {
        splashScreenViewModel.navigationEvent.collect { destination ->
            navController.navigate(route = destination) {
                popUpTo(route = NavigationRoutes.Splash) { inclusive = true }
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(2000)
        currentOnAction(SplashScreenIntent.NavigateToHome)
    }

    Scaffold(
        content = { paddingValues ->

            Box(
                modifier = Modifier.padding(paddingValues = paddingValues).fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center,
                content = {
                    Image(
                        modifier = Modifier.size(200.dp),
                        painter = painterResource(Res.drawable.git_logo),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = null
                    )
                }
            )
        }
    )
}