package com.codebaron.githubusers

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.room.Room
import androidx.room.RoomDatabase
import com.codebaron.githubusers.data.room.git_user_database.GitHubDatabase
import com.codebaron.githubusers.data.room.git_user_database.getRoomDatabase
import com.codebaron.githubusers.domain.utils.enums.ConnectivityStatus
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

class AndroidPlatform : Platform {
    override val name: String = "Android"
}

actual fun getPlatform(): Platform = AndroidPlatform()

private lateinit var appContext: Context

fun initContext(context: Context) {
    appContext = context
}
actual class ConnectivityObserver(private val context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    actual fun observe(): Flow<ConnectivityStatus> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(ConnectivityStatus.Available)
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                trySend(ConnectivityStatus.Losing)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(ConnectivityStatus.Lost)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                trySend(ConnectivityStatus.Unavailable)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Send current status
        trySend(getCurrentStatus())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    actual fun getCurrentStatus(): ConnectivityStatus {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return if (capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) {
            ConnectivityStatus.Available
        } else {
            ConnectivityStatus.Unavailable
        }
    }
}

actual fun getPlatformConnectivityObserver(): ConnectivityObserver {
    // You'll need to pass context here - get it from your Activity or Application
    return ConnectivityObserver(context = appContext)
}

actual val gitHubUserPlatformModule: Module = module {
    single<HttpClientEngine> { OkHttp.create {} }
}

/**
 * Creates a Room database builder for Android.
 *
 * Security note:
 * - Sensitive data (tokens, credentials) is stored in EncryptedSharedPreferences
 * - The database contains cached data (challenges, chats, etc.)
 * - Android's file-based encryption protects data at rest
 *
 * @param ctx The application context
 * @return A configured RoomDatabase.Builder
 */
fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<GitHubDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("github_database.db")

    return Room.databaseBuilder<GitHubDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}

val androidModule = module {
    single<GitHubDatabase> {
        val context = androidContext()
        getRoomDatabase(getDatabaseBuilder(context))
    }
}

actual val databaseModules: Module = module {
    includes(androidModule)
}