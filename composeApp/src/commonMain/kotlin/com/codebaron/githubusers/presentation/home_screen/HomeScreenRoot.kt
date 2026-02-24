package com.codebaron.githubusers.presentation.home_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.codebaron.githubusers.domain.utils.extension.MoonIcon
import com.codebaron.githubusers.domain.utils.extension.SunIcon
import com.codebaron.githubusers.domain.view_model.home_view_model.HomeScreenIntent
import com.codebaron.githubusers.domain.view_model.home_view_model.HomeScreenState
import com.codebaron.githubusers.domain.view_model.home_view_model.HomeScreenViewModel
import com.codebaron.githubusers.presentation.components.UserListItem
import com.codebaron.githubusers.presentation.theme.LightTextPrimary
import com.codebaron.githubusers.presentation.theme.LocalIsDarkTheme
import com.codebaron.githubusers.presentation.theme.LocalToggleTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun HomeScreenRoot(
    navController: NavHostController,
    homeScreenViewModel: HomeScreenViewModel
) {
    val homeScreenState by homeScreenViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        homeScreenViewModel.navigationEvent.collect { destination ->
            navController.navigate(route = destination)
        }
    }

    HomeScreen(
        state = homeScreenState,
        onAction = { intent -> homeScreenViewModel.sendIntent(intent) },
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeScreenState,
    onAction: (HomeScreenIntent) -> Unit,
    navController: NavHostController
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentOnAction by rememberUpdatedState(onAction)
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val isDarkTheme = LocalIsDarkTheme.current
    val toggleTheme = LocalToggleTheme.current

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 3 && totalItems > 0
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                if (!state.isPaginating && state.hasMorePages && !state.isSearchActive) {
                    currentOnAction(HomeScreenIntent.LoadNextPage)
                }
            }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(message = error)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "GitHub Users",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (LocalIsDarkTheme.current) Color.White else LightTextPrimary
                        )
                    )
                },
                actions = {
                    IconButton(onClick = { currentOnAction(HomeScreenIntent.ToggleSearch) }) {
                        Icon(
                            imageVector = if (state.isSearchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = if (state.isSearchActive) "Close search" else "Search"
                        )
                    }
                    IconButton(onClick = toggleTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) SunIcon else MoonIcon,
                            contentDescription = if (isDarkTheme) "Switch to light mode" else "Switch to dark mode"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                AnimatedVisibility(
                    visible = state.isSearchActive,
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                    content = {
                        OutlinedTextField(
                            value = state.searchQuery,
                            onValueChange = { query ->
                                currentOnAction(HomeScreenIntent.SearchUsers(query))
                            },
                            textStyle =  MaterialTheme.typography.bodyMedium.copy(
                                color = if (LocalIsDarkTheme.current) Color.White else LightTextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            placeholder = { Text("Search by username...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            },
                            trailingIcon = {
                                if (state.searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { currentOnAction(HomeScreenIntent.ClearSearch) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear search"
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = { keyboardController?.hide() }
                            )
                        )
                    }
                )

                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { currentOnAction(HomeScreenIntent.RefreshUsers) },
                    modifier = Modifier.fillMaxSize(),
                    content = {
                        if (state.isLoading && state.users.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else if (state.users.isEmpty() && !state.isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (state.searchQuery.isNotEmpty())
                                        "No users found for \"${state.searchQuery}\""
                                    else
                                        "No users available",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = state.users,
                                    key = { user -> user.id }
                                ) { user ->
                                    UserListItem(
                                        user = user,
                                        onClick = {
                                            currentOnAction(HomeScreenIntent.NavigateToDetail(user))
                                        }
                                    )
                                }

                                if (state.isPaginating) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    )
}
