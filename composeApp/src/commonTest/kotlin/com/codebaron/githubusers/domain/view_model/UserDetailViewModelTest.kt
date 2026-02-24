package com.codebaron.githubusers.domain.view_model

import com.codebaron.githubusers.domain.view_model.user_detail_view_model.UserDetailIntent
import com.codebaron.githubusers.domain.view_model.user_detail_view_model.UserDetailViewModel
import com.codebaron.githubusers.fake.FakeGitHubUsersDao
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [UserDetailViewModel].
 *
 * This class verifies the business logic and state management for the user detail screen,
 * ensuring that intents such as loading user details and navigating back result in
 * the expected UI states and side effects.
 *
 * Testing includes:
 * - Verification of the initial UI state.
 * - Success and failure scenarios for user data retrieval from the DAO.
 * - Proper handling of loading states.
 * - Navigation event emission.
 * - Support for different user types (e.g., User vs. Organization).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UserDetailViewModelTest {

    private lateinit var viewModel: UserDetailViewModel
    private lateinit var fakeDao: FakeGitHubUsersDao
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakeGitHubUsersDao()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): UserDetailViewModel {
        return UserDetailViewModel(gitHubUsersDao = fakeDao)
    }

    @Test
    fun `initial state should be default UserDetailState`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertNull(state.user)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun `loadUser should update state with user on success`() = runTest {
        val user = TestDataFactory.createUser(id = 1, login = "testuser")
        fakeDao.setUsers(listOf(user))

        viewModel = createViewModel()
        viewModel.sendIntent(UserDetailIntent.LoadUser(userId = 1))
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertNotNull(state.user)
        assertEquals("testuser", state.user?.login)
        assertEquals(1, state.user?.id)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun `loadUser should show error when user not found`() = runTest {
        fakeDao.setUsers(emptyList())

        viewModel = createViewModel()
        viewModel.sendIntent(UserDetailIntent.LoadUser(userId = 999))
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertNull(state.user)
        assertFalse(state.isLoading)
        assertEquals("User not found", state.errorMessage)
    }

    @Test
    fun `loadUser should set isLoading to true while loading`() = runTest {
        val user = TestDataFactory.createUser(id = 1)
        fakeDao.setUsers(listOf(user))

        viewModel = createViewModel()

        viewModel.sendIntent(UserDetailIntent.LoadUser(userId = 1))

        val loadingState = viewModel.state.first()
        assertTrue(loadingState.isLoading)

        advanceUntilIdle()

        val finalState = viewModel.state.first()
        assertFalse(finalState.isLoading)
    }

    @Test
    fun `navigateBack should emit navigation event`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(UserDetailIntent.NavigateBack)
        advanceUntilIdle()

        val shouldNavigateBack = viewModel.navigationBack.first()
        assertTrue(shouldNavigateBack)
    }

    @Test
    fun `loadUser should correctly load organization type user`() = runTest {
        val org = TestDataFactory.createOrganization(id = 100, login = "testorg")
        fakeDao.setUsers(listOf(org))

        viewModel = createViewModel()
        viewModel.sendIntent(UserDetailIntent.LoadUser(userId = 100))
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertNotNull(state.user)
        assertEquals("Organization", state.user?.type)
        assertEquals("testorg", state.user?.login)
    }

    @Test
    fun `loadUser should handle multiple users in database`() = runTest {
        val users = TestDataFactory.createUserList(10, startId = 1)
        fakeDao.setUsers(users)

        viewModel = createViewModel()
        viewModel.sendIntent(UserDetailIntent.LoadUser(userId = 5))
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertNotNull(state.user)
        assertEquals(5, state.user?.id)
        assertEquals("user5", state.user?.login)
    }

    @Test
    fun `state should preserve user data after loading`() = runTest {
        val user = TestDataFactory.createUser(
            id = 1,
            login = "johndoe",
            type = "User",
            avatarUrl = "https://example.com/avatar.png",
            htmlUrl = "https://github.com/johndoe",
            siteAdmin = true
        )
        fakeDao.setUsers(listOf(user))

        viewModel = createViewModel()
        viewModel.sendIntent(UserDetailIntent.LoadUser(userId = 1))
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertNotNull(state.user)
        assertEquals("johndoe", state.user?.login)
        assertEquals("User", state.user?.type)
        assertEquals("https://example.com/avatar.png", state.user?.avatarUrl)
        assertEquals("https://github.com/johndoe", state.user?.htmlUrl)
        assertEquals(true, state.user?.siteAdmin)
    }

    @Test
    fun `sendIntent should handle sequential load requests`() = runTest {
        val user1 = TestDataFactory.createUser(id = 1, login = "user1")
        val user2 = TestDataFactory.createUser(id = 2, login = "user2")
        fakeDao.setUsers(listOf(user1, user2))

        viewModel = createViewModel()

        viewModel.sendIntent(UserDetailIntent.LoadUser(userId = 1))
        advanceUntilIdle()
        assertEquals("user1", viewModel.state.first().user?.login)

        viewModel.sendIntent(UserDetailIntent.LoadUser(userId = 2))
        advanceUntilIdle()
        assertEquals("user2", viewModel.state.first().user?.login)
    }
}
