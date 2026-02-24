# GitHub Users

A Kotlin Multiplatform application that displays GitHub users with a modern, clean architecture. Built with Compose Multiplatform for Android and iOS.

## Features

- **User List**: Browse GitHub users with infinite scroll pagination
- **User Details**: View detailed information about each user
- **Search**: Filter users by username with real-time search
- **Dark Mode**: Toggle between light and dark themes
- **Offline Support**: Full offline caching with Room database
- **Pull-to-Refresh**: Refresh user list with swipe gesture

## Screenshots

| Home Screen | User Detail | Search | Dark Mode |
|-------------|-------------|--------|-----------|
| User list with avatars | User information card | Filter by username | Dark theme support |

## Architecture

This project follows **Clean Architecture** principles with the **MVI (Model-View-Intent)** pattern for state management.

```
composeApp/
├── data/
│   ├── model/                    # Data models
│   │   └── GitHubUsersResponseItem.kt
│   └── room/                     # Local database
│       └── git_user_database/
│           ├── GitHubDatabase.kt
│           └── GitHubUsersDao.kt
├── domain/
│   ├── koin/                     # Dependency injection
│   │   └── KoinSetup.kt
│   ├── navigation_config/        # Navigation routes
│   │   └── NavigationRoutes.kt
│   ├── remote/                   # Network layer
│   │   ├── RemoteDataSource.kt
│   │   ├── RemoteDataSourceImpl.kt
│   │   ├── RemoteRepository.kt
│   │   └── RemoteRepositoryImpl.kt
│   └── view_model/               # ViewModels (MVI)
│       ├── home_view_model/
│       │   ├── HomeScreenIntent.kt
│       │   ├── HomeScreenState.kt
│       │   └── HomeScreenViewModel.kt
│       └── user_detail_view_model/
│           ├── UserDetailIntent.kt
│           ├── UserDetailState.kt
│           └── UserDetailViewModel.kt
└── presentation/                 # UI layer
    ├── components/
    │   └── UserListItem.kt
    ├── home_screen/
    │   └── HomeScreenRoot.kt
    ├── splash_screen/
    │   └── SplashScreenRoot.kt
    ├── theme/
    │   └── AppTheme.kt
    └── user_detail_screen/
        └── UserDetailScreenRoot.kt
```

## Tech Stack

| Category | Technology |
|----------|------------|
| **UI Framework** | Compose Multiplatform |
| **Architecture** | Clean Architecture + MVI |
| **Dependency Injection** | Koin |
| **Networking** | Ktor Client |
| **Local Database** | Room (with KSP) |
| **Image Loading** | Coil |
| **Navigation** | Jetpack Navigation Compose |
| **Async Operations** | Kotlin Coroutines & Flow |
| **Serialization** | Kotlinx Serialization |

## API

The app uses the [GitHub Users API](https://docs.github.com/en/rest/users/users):

```
GET https://api.github.com/users?since={lastUserId}
```

Pagination is handled using the `since` parameter, which returns users with IDs greater than the specified value.

## Getting Started

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or later
- **Xcode** 15.0 or later (for iOS)
- **JDK** 11 or later
- **Kotlin** 2.0+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/GitHubUsers.git
   cd GitHubUsers
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Sync Gradle**
   - Android Studio will automatically sync Gradle dependencies
   - Wait for the sync to complete

### Build and Run

#### Android

Using Android Studio:
- Select the `composeApp` run configuration
- Choose an Android emulator or device
- Click Run

Using Terminal:
```bash
# macOS/Linux
./gradlew :composeApp:assembleDebug

# Windows
.\gradlew.bat :composeApp:assembleDebug
```

#### iOS

Using Android Studio:
- Select the `iosApp` run configuration
- Choose an iOS simulator
- Click Run

Using Xcode:
- Open `/iosApp/iosApp.xcodeproj` in Xcode
- Select a simulator or device
- Click Run

## Testing

The project includes comprehensive unit and UI tests.

### Unit Tests

Located in `composeApp/src/commonTest/`:

```bash
# Run all common tests
./gradlew :composeApp:allTests
```

**ViewModel Tests:**
- `HomeScreenViewModelTest` - Tests for home screen logic
- `UserDetailViewModelTest` - Tests for user detail logic

**Test Infrastructure:**
- `FakeGitHubUsersDao` - Mock DAO for testing
- `FakeRemoteRepository` - Mock repository for testing
- `TestDataFactory` - Factory for creating test data

### UI Tests (Android)

Located in `composeApp/src/androidInstrumentedTest/`:

```bash
# Run Android instrumented tests
./gradlew :composeApp:connectedAndroidTest
```

**UI Test Classes:**
- `HomeScreenUiTest` - Tests for home screen UI
- `UserDetailScreenUiTest` - Tests for user detail screen UI

### Test Coverage

| Component | Coverage |
|-----------|----------|
| HomeScreenViewModel | Initial state, load users, pagination, search, refresh, navigation |
| UserDetailViewModel | Initial state, load user, error handling, navigation |
| HomeScreen UI | Title, search, dark mode toggle, user list, empty states |
| UserDetailScreen UI | Title, back button, user info, error states |

## Project Structure

### Data Layer

**GitHubUsersResponseItem** - Main data model representing a GitHub user:
```kotlin
@Serializable
@Entity(tableName = "git_hub_users")
data class GitHubUsersResponseItem(
    @PrimaryKey val id: Int,
    val login: String?,
    val avatarUrl: String?,
    val type: String?,
    val siteAdmin: Boolean?,
    // ... other fields
)
```

**GitHubUsersDao** - Room DAO for local caching:
```kotlin
@Dao
interface GitHubUsersDao {
    fun getAllUsers(): Flow<List<GitHubUsersResponseItem>>
    fun searchUsers(query: String): Flow<List<GitHubUsersResponseItem>>
    suspend fun getUserById(userId: Int): GitHubUsersResponseItem?
    suspend fun insertUsers(users: List<GitHubUsersResponseItem>)
    suspend fun clearAllUsers()
    suspend fun getLastUserId(): Int?
}
```

### Domain Layer

**HomeScreenState** - Represents the UI state:
```kotlin
data class HomeScreenState(
    val users: List<GitHubUsersResponseItem> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isPaginating: Boolean = false,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val errorMessage: String? = null,
    val hasMorePages: Boolean = true
)
```

**HomeScreenIntent** - User actions:
```kotlin
sealed class HomeScreenIntent {
    object LoadUsers : HomeScreenIntent()
    object LoadNextPage : HomeScreenIntent()
    object RefreshUsers : HomeScreenIntent()
    data class SearchUsers(val query: String) : HomeScreenIntent()
    data class NavigateToDetail(val user: GitHubUsersResponseItem) : HomeScreenIntent()
    object ClearSearch : HomeScreenIntent()
    object ToggleSearch : HomeScreenIntent()
}
```

### Presentation Layer

The UI is built using Jetpack Compose with Material 3 design:

- **HomeScreen**: Displays user list with search and dark mode toggle
- **UserDetailScreen**: Shows detailed user information
- **UserListItem**: Reusable card component for user display

## Configuration

### Dependencies

Key dependencies are managed in `gradle/libs.versions.toml`:

```toml
[versions]
kotlin = "2.1.0"
compose = "1.7.6"
room = "2.7.0-alpha12"
ktor = "2.3.4"
koin = "4.0.0"

[libraries]
room-runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
koin-compose = { module = "io.insert-koin:koin-compose", version.ref = "koin" }
```

### Room Database

Room schema is exported to `composeApp/schemas/` for migration support.

```kotlin
@Database(
    entities = [GitHubUsersResponseItem::class],
    version = 2,
    exportSchema = true
)
abstract class GitHubDatabase : RoomDatabase() {
    abstract fun gitHubUsersDao(): GitHubUsersDao
}
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Write tests for new features
- Keep functions small and focused

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [GitHub REST API](https://docs.github.com/en/rest) for user data
- [JetBrains](https://www.jetbrains.com/) for Kotlin Multiplatform
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) for cross-platform UI

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
