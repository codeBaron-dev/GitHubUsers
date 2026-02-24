package com.codebaron.githubusers.fake

import com.codebaron.githubusers.data.model.GitHubUsersResponseItem

/**
 * Factory class for creating mock data and test objects for unit and UI tests.
 * This object provides utility methods to generate instances of [GitHubUsersResponseItem]
 * with default values, simplifying the setup of test cases.
 */
object TestDataFactory {

    fun createUser(
        id: Int = 1,
        login: String = "testuser",
        type: String = "User",
        avatarUrl: String = "https://avatars.githubusercontent.com/u/$id",
        htmlUrl: String = "https://github.com/$login",
        siteAdmin: Boolean = false
    ): GitHubUsersResponseItem {
        return GitHubUsersResponseItem(
            avatarUrl = avatarUrl,
            eventsUrl = "https://api.github.com/users/$login/events{/privacy}",
            followersUrl = "https://api.github.com/users/$login/followers",
            followingUrl = "https://api.github.com/users/$login/following{/other_user}",
            gistsUrl = "https://api.github.com/users/$login/gists{/gist_id}",
            gravatarId = "",
            htmlUrl = htmlUrl,
            id = id,
            login = login,
            nodeId = "MDQ6VXNlcg$id",
            organizationsUrl = "https://api.github.com/users/$login/orgs",
            receivedEventsUrl = "https://api.github.com/users/$login/received_events",
            reposUrl = "https://api.github.com/users/$login/repos",
            siteAdmin = siteAdmin,
            starredUrl = "https://api.github.com/users/$login/starred{/owner}{/repo}",
            subscriptionsUrl = "https://api.github.com/users/$login/subscriptions",
            type = type,
            url = "https://api.github.com/users/$login",
            userViewType = "public"
        )
    }

    fun createUserList(count: Int, startId: Int = 1): List<GitHubUsersResponseItem> {
        return (startId until startId + count).map { id ->
            createUser(
                id = id,
                login = "user$id"
            )
        }
    }

    fun createOrganization(
        id: Int = 100,
        login: String = "testorg"
    ): GitHubUsersResponseItem {
        return createUser(
            id = id,
            login = login,
            type = "Organization"
        )
    }
}
