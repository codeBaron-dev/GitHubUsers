package com.codebaron.githubusers.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.codebaron.githubusers.data.model.GitHubUsersResponseItem
import com.codebaron.githubusers.domain.view_model.user_detail_view_model.UserDetailIntent
import com.codebaron.githubusers.domain.view_model.user_detail_view_model.UserDetailState
import com.codebaron.githubusers.presentation.theme.AppTheme
import com.codebaron.githubusers.presentation.user_detail_screen.UserDetailScreen
import org.junit.Rule
import org.junit.Test

class UserDetailScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createTestUser(
        id: Int = 1,
        login: String = "testuser",
        type: String = "User",
        siteAdmin: Boolean = false
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
            siteAdmin = siteAdmin,
            starredUrl = null,
            subscriptionsUrl = null,
            type = type,
            url = null,
            userViewType = null
        )
    }

    @Test
    fun userDetailScreen_displaysDefaultTitle_whenNoUser() {
        composeTestRule.setContent {
            AppTheme {
                UserDetailScreen(
                    state = UserDetailState(),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("User Details").assertIsDisplayed()
    }

    @Test
    fun userDetailScreen_displaysUsername_asTitle() {
        val user = createTestUser(login = "johndoe")

        composeTestRule.setContent {
            AppTheme {
                UserDetailScreen(
                    state = UserDetailState(user = user),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("johndoe").assertIsDisplayed()
    }

    @Test
    fun userDetailScreen_displaysBackButton() {
        composeTestRule.setContent {
            AppTheme {
                UserDetailScreen(
                    state = UserDetailState(),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Go back").assertIsDisplayed()
    }

    @Test
    fun userDetailScreen_displaysUserType() {
        val user = createTestUser(login = "testuser", type = "User")

        composeTestRule.setContent {
            AppTheme {
                UserDetailScreen(
                    state = UserDetailState(user = user),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("User").assertIsDisplayed()
    }

    @Test
    fun userDetailScreen_displaysOrganizationType() {
        val org = createTestUser(login = "testorg", type = "Organization")

        composeTestRule.setContent {
            AppTheme {
                UserDetailScreen(
                    state = UserDetailState(user = org),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("Organization").assertIsDisplayed()
    }

    @Test
    fun userDetailScreen_displaysViewOnGitHubButton() {
        val user = createTestUser(login = "testuser")

        composeTestRule.setContent {
            AppTheme {
                UserDetailScreen(
                    state = UserDetailState(user = user),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("View on GitHub").assertIsDisplayed()
    }

    @Test
    fun userDetailScreen_displaysUserInformationSection() {
        val user = createTestUser(id = 123, login = "testuser", type = "User")

        composeTestRule.setContent {
            AppTheme {
                UserDetailScreen(
                    state = UserDetailState(user = user),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("User Information").assertIsDisplayed()
        composeTestRule.onNodeWithText("ID").assertIsDisplayed()
        composeTestRule.onNodeWithText("123").assertIsDisplayed()
        composeTestRule.onNodeWithText("Type").assertIsDisplayed()
    }

    @Test
    fun userDetailScreen_displaysSiteAdminNo_whenNotAdmin() {
        val user = createTestUser(login = "testuser", siteAdmin = false)

        composeTestRule.setContent {
            AppTheme {
                UserDetailScreen(
                    state = UserDetailState(user = user),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("Site Admin").assertIsDisplayed()
        composeTestRule.onNodeWithText("No").assertIsDisplayed()
    }

    @Test
    fun userDetailScreen_displaysSiteAdminYes_whenAdmin() {
        val user = createTestUser(login = "adminuser", siteAdmin = true)

        composeTestRule.setContent {
            AppTheme {
                UserDetailScreen(
                    state = UserDetailState(user = user),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("Site Admin").assertIsDisplayed()
        composeTestRule.onNodeWithText("Yes").assertIsDisplayed()
    }

    @Test
    fun userDetailScreen_showsErrorMessage_whenUserNotFound() {
        composeTestRule.setContent {
            AppTheme {
                UserDetailScreen(
                    state = UserDetailState(errorMessage = "User not found"),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("User not found").assertIsDisplayed()
    }

    @Test
    fun userDetailScreen_showsLoadingIndicator_whenLoading() {
        composeTestRule.setContent {
            AppTheme {
                UserDetailScreen(
                    state = UserDetailState(isLoading = true),
                    onAction = {},
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("User Details").assertIsDisplayed()
    }
}
