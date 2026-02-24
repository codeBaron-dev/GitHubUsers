@file:OptIn(ExperimentalForeignApi::class)

package com.codebaron.githubusers

import androidx.room.Room
import com.codebaron.githubusers.data.room.git_user_database.GitHubDatabase
import com.codebaron.githubusers.data.room.git_user_database.getRoomDatabase
import com.codebaron.githubusers.domain.utils.enums.ConnectivityStatus
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual class ConnectivityObserver {
    actual fun observe(): Flow<ConnectivityStatus> = flow {
        // Simple implementation - you might want to use a more sophisticated approach
        while (true) {
            emit(getCurrentStatus())
            kotlinx.coroutines.delay(1000) // Check every second
        }
    }

    actual fun getCurrentStatus(): ConnectivityStatus {
        // Simplified iOS implementation
        // In a real app, you'd use Network.framework or Reachability
        return ConnectivityStatus.Available // Placeholder
    }
}

actual fun getPlatformConnectivityObserver(): ConnectivityObserver {
    return ConnectivityObserver()
}

actual val gitHubUserPlatformModule: Module = module {
    single<HttpClientEngine> {
        Darwin.create {
            // Configure Darwin engine for secure networking
            configureSession {
                // Enable HTTP/2 for better performance
                // Note: Certificate pinning would require a custom URLSessionDelegate
                // which is best implemented in Swift and bridged to Kotlin
            }
            configureRequest {
                // Request-level configuration
                setAllowsCellularAccess(true)
            }
        }
    }
}

private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}

val iosModule = module {
    val dbFilePath = documentDirectory() + "/github_database.db"
    single<GitHubDatabase> {
        getRoomDatabase(
            Room.databaseBuilder<GitHubDatabase>(
                name = dbFilePath
            )
        )
    }
}

actual val databaseModules: Module = module {
    includes(iosModule)
}