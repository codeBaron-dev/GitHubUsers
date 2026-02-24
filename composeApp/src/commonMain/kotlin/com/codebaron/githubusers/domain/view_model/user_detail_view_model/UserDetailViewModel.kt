package com.codebaron.githubusers.domain.view_model.user_detail_view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codebaron.githubusers.data.room.git_user_database.GitHubUsersDao
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserDetailViewModel(
    private val gitHubUsersDao: GitHubUsersDao
) : ViewModel() {

    private val _state = MutableStateFlow(UserDetailState())
    val state = _state.asStateFlow()

    private val intents = MutableSharedFlow<UserDetailIntent>()

    private val _navigationBack = MutableSharedFlow<Boolean>()
    val navigationBack = _navigationBack.asSharedFlow()

    init {
        handleIntents()
    }

    fun sendIntent(intent: UserDetailIntent) {
        viewModelScope.launch {
            intents.emit(intent)
        }
    }

    private fun handleIntents() = viewModelScope.launch {
        intents.collect { intent ->
            when (intent) {
                is UserDetailIntent.LoadUser -> loadUser(intent.userId)
                is UserDetailIntent.NavigateBack -> navigateBack()
            }
        }
    }

    /**
     * Fetches user details from the local database by their unique identifier and updates the state.
     *
     * This function handles the loading lifecycle by setting [UserDetailState.isLoading] to true,
     * retrieving the user data from [GitHubUsersDao], and then updating the state with the
     * result or an error message if the user is not found.
     *
     * @param userId The unique identifier of the user to be loaded.
     */
    private fun loadUser(userId: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        val user = gitHubUsersDao.getUserById(userId)
        _state.update {
            it.copy(
                user = user,
                isLoading = false,
                errorMessage = if (user == null) "User not found" else null
            )
        }
    }

    private fun navigateBack() = viewModelScope.launch {
        _navigationBack.emit(true)
    }
}
