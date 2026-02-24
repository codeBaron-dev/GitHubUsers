package com.codebaron.githubusers.domain.view_model.home_view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codebaron.githubusers.data.model.GitHubUsersResponseItem
import com.codebaron.githubusers.data.room.git_user_database.GitHubUsersDao
import com.codebaron.githubusers.domain.navigation_config.NavigationRoutes
import com.codebaron.githubusers.domain.remote.RemoteRepository
import com.codebaron.githubusers.domain.utils.remote_config.onError
import com.codebaron.githubusers.domain.utils.remote_config.onSuccess
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val remoteRepository: RemoteRepository,
    private val gitHubUsersDao: GitHubUsersDao
) : ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state.asStateFlow()

    private val intents = MutableSharedFlow<HomeScreenIntent>()

    private val _navigationEvent = MutableSharedFlow<NavigationRoutes>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _selectedUser = MutableSharedFlow<GitHubUsersResponseItem>()
    val selectedUser = _selectedUser.asSharedFlow()

    private var searchJob: Job? = null

    init {
        handleIntents()
        loadInitialData()
    }

    fun sendIntent(intent: HomeScreenIntent) {
        viewModelScope.launch {
            intents.emit(intent)
        }
    }

    private fun handleIntents() = viewModelScope.launch {
        intents.collect { intent ->
            when (intent) {
                is HomeScreenIntent.LoadUsers -> loadUsers()
                is HomeScreenIntent.LoadNextPage -> loadNextPage()
                is HomeScreenIntent.RefreshUsers -> refreshUsers()
                is HomeScreenIntent.SearchUsers -> searchUsers(intent.query)
                is HomeScreenIntent.NavigateToDetail -> navigateToDetail(intent.user)
                is HomeScreenIntent.ClearSearch -> clearSearch()
                is HomeScreenIntent.ToggleSearch -> toggleSearch()
            }
        }
    }

    /**
     * Loads the initial set of users from the local database or fetches them from the remote API.
     *
     * This function observes the local cache provided by [GitHubUsersDao]. If the cache is not empty
     * and a search is not currently active, it updates the UI state with the cached data.
     * Otherwise, it triggers [loadUsers] to fetch data from the remote repository.
     */
    private fun loadInitialData() = viewModelScope.launch {
        gitHubUsersDao.getAllUsers().collect { cachedUsers ->
            if (cachedUsers.isNotEmpty() && !_state.value.isSearchActive) {
                _state.update { it.copy(users = cachedUsers) }
            } else loadUsers()
        }
    }

    /**
     * Fetches the initial set of GitHub users from the remote repository.
     *
     * This function performs the following steps:
     * 1. Checks if a loading operation is already in progress to prevent duplicate calls.
     * 2. Updates the UI state to show a loading indicator.
     * 3. Calls the remote repository to fetch users starting from ID 0.
     * 4. On success: Clears the local database cache, inserts the new users, and updates the UI state.
     * 5. On error or exception: Updates the UI state with the relevant error message and stops the loading indicator.
     */
    private fun loadUsers() = viewModelScope.launch {
        if (_state.value.isLoading) return@launch
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            remoteRepository.getGitHubUsers(since = 0)
                .onSuccess { response ->
                    gitHubUsersDao.clearAllUsers()
                    gitHubUsersDao.insertUsers(response)
                    _state.update {
                        it.copy(
                            users = response,
                            isLoading = false,
                            hasMorePages = response.isNotEmpty()
                        )
                    }
                }
                .onError { message ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = message
                        )
                    }
                }
        } catch (exception: Exception) {
            _state.update {
                it.copy(
                    isLoading = false,
                    errorMessage = exception.message
                )
            }
        }
    }

    /**
     * Fetches the next page of GitHub users for pagination.
     *
     * This function identifies the ID of the last user in the current list and requests
     * subsequent users from the [remoteRepository]. Upon a successful fetch, the new users
     * are persisted in the local [gitHubUsersDao] and appended to the current state.
     *
     * The process is skipped if:
     * - A pagination request is already in progress.
     * - There are no more pages available to fetch.
     * - A search filter is currently active.
     */
    private fun loadNextPage() = viewModelScope.launch {
        if (_state.value.isPaginating || !_state.value.hasMorePages || _state.value.isSearchActive) return@launch
        val lastUserId = _state.value.users.lastOrNull()?.id ?: 0
        _state.update { it.copy(isPaginating = true, errorMessage = null) }
        try {
            remoteRepository.getGitHubUsers(since = lastUserId)
                .onSuccess { response ->
                    if (response.isNotEmpty()) {
                        gitHubUsersDao.insertUsers(response)
                    }
                    _state.update {
                        it.copy(
                            users = it.users + response,
                            isPaginating = false,
                            hasMorePages = response.isNotEmpty()
                        )
                    }
                }
                .onError { message ->
                    _state.update {
                        it.copy(
                            isPaginating = false,
                            errorMessage = message
                        )
                    }
                }
        } catch (exception: Exception) {
            _state.update {
                it.copy(
                    isPaginating = false,
                    errorMessage = exception.message
                )
            }
        }
    }

    /**
     * Refreshes the GitHub users list by fetching the first page of users from the remote repository.
     *
     * This function performs the following steps:
     * 1. Updates the UI state to show a refreshing indicator.
     * 2. Fetches the latest users starting from the first user (since = 0).
     * 3. On success, clears the local [gitHubUsersDao] cache and populates it with the fresh data.
     * 4. On error or exception, updates the UI state with the relevant error message.
     * 5. Resets the refreshing indicator once the operation is complete.
     */
    private fun refreshUsers() = viewModelScope.launch {
        _state.update { it.copy(isRefreshing = true, errorMessage = null) }
        try {
            remoteRepository.getGitHubUsers(since = 0)
                .onSuccess { response ->
                    gitHubUsersDao.clearAllUsers()
                    gitHubUsersDao.insertUsers(response)
                    _state.update {
                        it.copy(
                            users = response,
                            isRefreshing = false,
                            hasMorePages = response.isNotEmpty()
                        )
                    }
                }
                .onError { message ->
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            errorMessage = message
                        )
                    }
                }
        } catch (exception: Exception) {
            _state.update {
                it.copy(
                    isRefreshing = false,
                    errorMessage = exception.message
                )
            }
        }
    }

    /**
     * Searches for users in the local database based on the provided [query].
     *
     * This function cancels any ongoing search job to prevent race conditions.
     * If the query is blank, it restores the full list of users from the local cache.
     * If the query is not blank, it filters the users in the database and updates the UI state.
     *
     * @param query The search string used to filter users by their username.
     */
    private fun searchUsers(query: String) {
        searchJob?.cancel()
        _state.update { it.copy(searchQuery = query) }

        if (query.isBlank()) {
            viewModelScope.launch {
                val allUsers = gitHubUsersDao.getLastUserId()?.let {
                    // Reload from cache
                    gitHubUsersDao.getAllUsers()
                }
                allUsers?.collect { users ->
                    _state.update { it.copy(users = users) }
                }
            }
            return
        }

        searchJob = viewModelScope.launch {
            gitHubUsersDao.searchUsers(query).collect { users ->
                _state.update { it.copy(users = users) }
            }
        }
    }

    /**
     * Toggles the active state of the search interface.
     *
     * When search is being deactivated, it clears the current [HomeScreenState.searchQuery]
     * and resets the user list by calling [clearSearch].
     */
    private fun toggleSearch() {
        _state.update {
            it.copy(
                isSearchActive = !it.isSearchActive,
                searchQuery = if (it.isSearchActive) "" else it.searchQuery
            )
        }
        if (!_state.value.isSearchActive) {
            clearSearch()
        }
    }

    /**
     * Clears the current search state, cancels any ongoing search jobs, and restores the
     * list of users from the local database cache.
     */
    private fun clearSearch() {
        searchJob?.cancel()
        _state.update { it.copy(searchQuery = "", isSearchActive = false) }
        viewModelScope.launch {
            gitHubUsersDao.getAllUsers().collect { users ->
                _state.update { it.copy(users = users) }
            }
        }
    }

    private fun navigateToDetail(user: GitHubUsersResponseItem) = viewModelScope.launch {
        _selectedUser.emit(user)
        _navigationEvent.emit(NavigationRoutes.UserDetail(userId = user.id))
    }
}