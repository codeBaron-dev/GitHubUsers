package com.codebaron.githubusers.presentation.user_detail_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.codebaron.githubusers.domain.view_model.user_detail_view_model.UserDetailIntent
import com.codebaron.githubusers.domain.view_model.user_detail_view_model.UserDetailState
import com.codebaron.githubusers.domain.view_model.user_detail_view_model.UserDetailViewModel
import com.codebaron.githubusers.presentation.components.UserTypeBadge
import com.codebaron.githubusers.presentation.theme.LightTextPrimary
import com.codebaron.githubusers.presentation.theme.LocalIsDarkTheme

@Composable
fun UserDetailScreenRoot(
    navController: NavHostController,
    userDetailViewModel: UserDetailViewModel,
    userId: Int
) {
    val userDetailState by userDetailViewModel.state.collectAsState()

    LaunchedEffect(userId) {
        userDetailViewModel.sendIntent(UserDetailIntent.LoadUser(userId))
    }

    LaunchedEffect(Unit) {
        userDetailViewModel.navigationBack.collect { shouldNavigateBack ->
            if (shouldNavigateBack) {
                navController.popBackStack()
            }
        }
    }

    UserDetailScreen(
        state = userDetailState,
        onAction = { intent -> userDetailViewModel.sendIntent(intent) },
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    state: UserDetailState,
    onAction: (UserDetailIntent) -> Unit,
    navController: NavHostController
) {
    val uriHandler = LocalUriHandler.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.user?.login ?: "User Details",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (LocalIsDarkTheme.current) Color.White else LightTextPrimary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = if (LocalIsDarkTheme.current) Color.White else LightTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    state.errorMessage != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = state.errorMessage,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    state.user != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AsyncImage(
                                        model = state.user.avatarUrl,
                                        contentDescription = "Avatar of ${state.user.login}",
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = state.user.login ?: "Unknown",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (LocalIsDarkTheme.current) Color.White else LightTextPrimary
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    UserTypeBadge(type = state.user.type ?: "User")

                                    Spacer(modifier = Modifier.height(24.dp))

                                    state.user.htmlUrl?.let { url ->
                                        Button(
                                            onClick = { uriHandler.openUri(url) },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.OpenInBrowser,
                                                contentDescription = null,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                            Text(text = "View on GitHub")
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "User Information",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (LocalIsDarkTheme.current) Color.White else LightTextPrimary
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    InfoRow(label = "ID", value = state.user.id.toString())
                                    InfoRow(label = "Type", value = state.user.type ?: "User")
                                    InfoRow(
                                        label = "Site Admin",
                                        value = if (state.user.siteAdmin == true) "Yes" else "No"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (LocalIsDarkTheme.current) Color.White else LightTextPrimary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = if (LocalIsDarkTheme.current) Color.White else LightTextPrimary
        )
    }
}
