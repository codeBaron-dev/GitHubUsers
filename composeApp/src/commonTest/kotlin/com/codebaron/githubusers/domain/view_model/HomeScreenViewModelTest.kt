package com.codebaron.githubusers.domain.view_model

import com.codebaron.githubusers.domain.view_model.home_view_model.HomeScreenIntent
import com.codebaron.githubusers.domain.view_model.home_view_model.HomeScreenState
import com.codebaron.githubusers.domain.view_model.home_view_model.HomeScreenViewModel
import com.codebaron.githubusers.fake.FakeGitHubUsersDao
import com.codebaron.githubusers.fake.FakeRemoteRepository
import com.codebaron.githubusers.fake.TestDataFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {

    private lateinit var viewModel: HomeScreenViewModel
    private lateinit var fakeRepository: FakeRemoteRepository
    private lateinit var fakeDao: FakeGitHubUsersDao
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeRemoteRepository()
        fakeDao = FakeGitHubUsersDao()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): HomeScreenViewModel {
        return HomeScreenViewModel(
            remoteRepository = fakeRepository,
            gitHubUsersDao = fakeDao
        )
    }

    @Test
    fun `initial state should be default HomeScreenState`() = runTest {
        val users = TestDataFactory.createUserList(5)
        fakeRepository.setUsersToReturn(users)

        viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertNotNull(state)
    }

    @Test
    fun `loadUsers should update state with users on success`() = runTest {
        val users = TestDataFactory.createUserList(5)
        fakeRepository.setUsersToReturn(users)

        viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertEquals(5, state.users.size)
        assertFalse(state.isLoading)
        assertTrue(state.hasMorePages)
    }

    @Test
    fun `loadUsers should update state with error message on failure`() = runTest {
        fakeRepository.setShouldReturnError(true, "Network error")

        viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertEquals("Network error", state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun `loadNextPage should append users to existing list`() = runTest {
        val initialUsers = TestDataFactory.createUserList(5, startId = 1)
        val nextPageUsers = TestDataFactory.createUserList(5, startId = 6)
        fakeRepository.setUsersToReturn(initialUsers + nextPageUsers)

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(HomeScreenIntent.LoadNextPage)
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertEquals(10, state.users.size)
        assertFalse(state.isPaginating)
    }

    @Test
    fun `refreshUsers should clear and reload users`() = runTest {
        val initialUsers = TestDataFactory.createUserList(3)
        fakeRepository.setUsersToReturn(initialUsers)

        viewModel = createViewModel()
        advanceUntilIdle()

        val newUsers = TestDataFactory.createUserList(5)
        fakeRepository.setUsersToReturn(newUsers)

        viewModel.sendIntent(HomeScreenIntent.RefreshUsers)
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertEquals(5, state.users.size)
        assertFalse(state.isRefreshing)
    }

    @Test
    fun `toggleSearch should toggle isSearchActive state`() = runTest {
        val users = TestDataFactory.createUserList(5)
        fakeRepository.setUsersToReturn(users)

        viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.state.first().isSearchActive)

        viewModel.sendIntent(HomeScreenIntent.ToggleSearch)
        advanceUntilIdle()

        assertTrue(viewModel.state.first().isSearchActive)
    }

    @Test
    fun `searchUsers should filter users by query`() = runTest {
        val users = listOf(
            TestDataFactory.createUser(id = 1, login = "johndoe"),
            TestDataFactory.createUser(id = 2, login = "janedoe"),
            TestDataFactory.createUser(id = 3, login = "testuser")
        )
        fakeRepository.setUsersToReturn(users)
        fakeDao.setUsers(users)

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(HomeScreenIntent.SearchUsers("doe"))
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertEquals("doe", state.searchQuery)
        assertEquals(2, state.users.size)
    }

    @Test
    fun `clearSearch should reset search state and restore users`() = runTest {
        val users = TestDataFactory.createUserList(5)
        fakeRepository.setUsersToReturn(users)
        fakeDao.setUsers(users)

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(HomeScreenIntent.ToggleSearch)
        viewModel.sendIntent(HomeScreenIntent.SearchUsers("test"))
        advanceUntilIdle()

        viewModel.sendIntent(HomeScreenIntent.ClearSearch)
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertEquals("", state.searchQuery)
        assertFalse(state.isSearchActive)
    }

    @Test
    fun `navigateToDetail should emit navigation event`() = runTest {
        val users = TestDataFactory.createUserList(5)
        fakeRepository.setUsersToReturn(users)

        viewModel = createViewModel()
        advanceUntilIdle()

        val user = users.first()
        viewModel.sendIntent(HomeScreenIntent.NavigateToDetail(user))
        advanceUntilIdle()

        val selectedUser = viewModel.selectedUser.first()
        assertEquals(user.id, selectedUser.id)
    }

    @Test
    fun `loadNextPage should not load when already paginating`() = runTest {
        val users = TestDataFactory.createUserList(5)
        fakeRepository.setUsersToReturn(users)
        fakeRepository.setDelay(1000)

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(HomeScreenIntent.LoadNextPage)
        viewModel.sendIntent(HomeScreenIntent.LoadNextPage)
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertFalse(state.isPaginating)
    }

    @Test
    fun `loadNextPage should not load when search is active`() = runTest {
        val users = TestDataFactory.createUserList(5)
        fakeRepository.setUsersToReturn(users)

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(HomeScreenIntent.ToggleSearch)
        viewModel.sendIntent(HomeScreenIntent.SearchUsers("test"))
        advanceUntilIdle()

        viewModel.sendIntent(HomeScreenIntent.LoadNextPage)
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertTrue(state.isSearchActive)
    }

    @Test
    fun `hasMorePages should be false when empty response returned`() = runTest {
        fakeRepository.setUsersToReturn(emptyList())

        viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertFalse(state.hasMorePages)
    }
}
