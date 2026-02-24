package com.codebaron.githubusers.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.codebaron.githubusers.data.model.GitHubUsersResponseItem
import com.codebaron.githubusers.domain.view_model.home_view_model.HomeScreenIntent
import com.codebaron.githubusers.domain.view_model.home_view_model.HomeScreenState
import com.codebaron.githubusers.presentation.home_screen.HomeScreen
import com.codebaron.githubusers.presentation.theme.AppTheme
import org.junit.Rule
import org.junit.Test

class HomeScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createTestUser(
        id: Int = 1,
        login: String = "testuser",
        type: String = "User"
    ): GitHubUsersResponseItem {
        return GitHubUsersResponseItem(
            avatarUrl = "https://avatars.githubusercontent.com/u/$id",
            eventsUrl = null,
            followersUrl = null,
            followingUrl = null,
            gistsUrl = null,
            gravatarId = null,
            htmlUrl = "https://github.com/$login",
            id = id,
            login = login,
            nodeId = null,
            organizationsUrl = null,
            receivedEventsUrl = null,
            reposUrl = null,
            siteAdmin = false,
            starredUrl = null,
            subscriptionsUrl = null,
            type = type,
            url = null,
            userViewType = null
        )
    }

    @Test
    fun homeScreen_displaysTitle() {
        composeTestRule.setContent {
            AppTheme {
                HomeScreen(
                    state = HomeScreenState(),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("GitHub Users").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysSearchIcon() {
        composeTestRule.setContent {
            AppTheme {
                HomeScreen(
                    state = HomeScreenState(),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Search").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysDarkModeToggle() {
        composeTestRule.setContent {
            AppTheme {
                HomeScreen(
                    state = HomeScreenState(),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Switch to dark mode").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysUserList() {
        val users = listOf(
            createTestUser(id = 1, login = "johndoe"),
            createTestUser(id = 2, login = "janedoe")
        )

        composeTestRule.setContent {
            AppTheme {
                HomeScreen(
                    state = HomeScreenState(users = users),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("johndoe").assertIsDisplayed()
        composeTestRule.onNodeWithText("janedoe").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysUserType() {
        val users = listOf(
            createTestUser(id = 1, login = "testuser", type = "User"),
            createTestUser(id = 2, login = "testorg", type = "Organization")
        )

        composeTestRule.setContent {
            AppTheme {
                HomeScreen(
                    state = HomeScreenState(users = users),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("User").assertIsDisplayed()
        composeTestRule.onNodeWithText("Organization").assertIsDisplayed()
    }

    @Test
    fun homeScreen_showsLoadingIndicator_whenLoading() {
        composeTestRule.setContent {
            AppTheme {
                HomeScreen(
                    state = HomeScreenState(isLoading = true),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("GitHub Users").assertIsDisplayed()
    }

    @Test
    fun homeScreen_showsEmptyMessage_whenNoUsers() {
        composeTestRule.setContent {
            AppTheme {
                HomeScreen(
                    state = HomeScreenState(users = emptyList(), isLoading = false),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("No users available").assertIsDisplayed()
    }

    @Test
    fun homeScreen_showsSearchEmptyMessage_whenNoSearchResults() {
        composeTestRule.setContent {
            AppTheme {
                HomeScreen(
                    state = HomeScreenState(
                        users = emptyList(),
                        isLoading = false,
                        searchQuery = "nonexistent"
                    ),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("No users found for \"nonexistent\"").assertIsDisplayed()
    }

    @Test
    fun homeScreen_searchIconClick_triggersToggleSearch() {
        var toggleSearchCalled = false

        composeTestRule.setContent {
            AppTheme {
                HomeScreen(
                    state = HomeScreenState(),
                    onAction = { intent ->
                        if (intent is HomeScreenIntent.ToggleSearch) {
                            toggleSearchCalled = true
                        }
                    },
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Search").performClick()
        assert(toggleSearchCalled) { "ToggleSearch intent should be called" }
    }

    @Test
    fun homeScreen_showsSearchField_whenSearchActive() {
        composeTestRule.setContent {
            AppTheme {
                HomeScreen(
                    state = HomeScreenState(isSearchActive = true),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("Search by username...").assertIsDisplayed()
    }

    @Test
    fun homeScreen_userClick_triggersNavigateToDetail() {
        val user = createTestUser(id = 1, login = "clickableuser")
        var navigateToDetailCalled = false

        composeTestRule.setContent {
            AppTheme {
                HomeScreen(
                    state = HomeScreenState(users = listOf(user)),
                    onAction = { intent ->
                        if (intent is HomeScreenIntent.NavigateToDetail) {
                            navigateToDetailCalled = true
                        }
                    },
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("clickableuser").performClick()
        assert(navigateToDetailCalled) { "NavigateToDetail intent should be called" }
    }
}
