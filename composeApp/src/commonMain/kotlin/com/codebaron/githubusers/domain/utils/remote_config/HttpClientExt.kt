package com.codebaron.githubusers.domain.utils.remote_config

import com.codebaron.githubusers.data.model.failure.response.DefaultServerErrorResponse
import com.codebaron.githubusers.domain.utils.extension.getUserFriendlyErrorMessage
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.JsonConvertException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): ServerResultHandler<T, ErrorDataTypes.Remote> {
    val response = try {
        // Execute the provided network request
        execute()
    } catch (e: HttpRequestTimeoutException) {
        // Handle HTTP request timeout
        return ServerResultHandler.Error(
            error = getUserFriendlyErrorMessage(ErrorDataTypes.Remote.request_timeout.name)
        )
    } catch (e: SocketTimeoutException) {
        // Handle socket timeout error
        return ServerResultHandler.Error(
            error = getUserFriendlyErrorMessage(ErrorDataTypes.Remote.request_timeout.name)
        )
    } catch (e: ConnectTimeoutException) {
        // Handle connection timeout error
        return ServerResultHandler.Error(
            error = getUserFriendlyErrorMessage(ErrorDataTypes.Remote.request_timeout.name)
        )
    } catch (e: UnresolvedAddressException) {
        // Handle unknown host error (e.g., no internet or invalid URL)
        return ServerResultHandler.Error(
            error = getUserFriendlyErrorMessage(ErrorDataTypes.Remote.unknown_host.name)
        )
    } catch (e: Exception) {
        // Ensure coroutine is still active
        currentCoroutineContext().ensureActive()
        // Handle any unknown errors with user-friendly message
        return ServerResultHandler.Error(
            error = getUserFriendlyErrorMessage(ErrorDataTypes.Local.unknown_error.name, e.message)
        )
    }

    // Convert the HttpResponse to ServerResultHandler
    return responseToServerResultHandler(response)
}

suspend inline fun <reified T> responseToServerResultHandler(
    response: HttpResponse
): ServerResultHandler<T, ErrorDataTypes.Remote> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                // Attempt to deserialize the response body into the expected type `T`
                ServerResultHandler.Success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                // Handle deserialization error if no transformation is found
                ServerResultHandler.Error(
                    error = getUserFriendlyErrorMessage(ErrorDataTypes.Remote.serialization.name, e.message)
                )
            } catch (e: JsonConvertException) {
                // Handle JSON conversion errors
                ServerResultHandler.Error(
                    error = getUserFriendlyErrorMessage(ErrorDataTypes.Remote.serialization.name, e.message)
                )
            }
        }
        401 -> {
            // Handle unauthorized access specifically
            try {
                val error = response.body<DefaultServerErrorResponse>()
                ServerResultHandler.Error(
                    error = getUserFriendlyErrorMessage(ErrorDataTypes.Remote.unauthorized.name, error.getFormattedErrorMessage())
                )
            } catch (exception: Exception) {
                ServerResultHandler.Error(
                    error = getUserFriendlyErrorMessage(ErrorDataTypes.Remote.unauthorized.name)
                )
            }
        }
        429 -> {
            // Handle too many requests specifically
            try {
                val error = response.body<DefaultServerErrorResponse>()
                ServerResultHandler.Error(
                    error = getUserFriendlyErrorMessage(ErrorDataTypes.Remote.too_many_requests.name, error.getFormattedErrorMessage())
                )
            } catch (exception: Exception) {
                ServerResultHandler.Error(
                    error = getUserFriendlyErrorMessage(ErrorDataTypes.Remote.too_many_requests.name)
                )
            }
        }
        in 500..599 -> {
            // Handle server errors specifically
            try {
                val error = response.body<DefaultServerErrorResponse>()
                ServerResultHandler.Error(
                    error = getUserFriendlyErrorMessage(ErrorDataTypes.Remote.server.name, error.getFormattedErrorMessage())
                )
            } catch (exception: Exception) {
                ServerResultHandler.Error(
                    error = getUserFriendlyErrorMessage(ErrorDataTypes.Remote.server.name)
                )
            }
        }
        else -> {
            try {
                // Attempt to parse the error response body for other status codes
                val error = response.body<DefaultServerErrorResponse>()
                ServerResultHandler.Error(error = error.getFormattedErrorMessage())
            } catch (exception: JsonConvertException) {
                // If the error is about missing 'data' field, handle it appropriately
                if (exception.message?.contains("Field 'data' is required") == true) {
                    // Create a "default" response with null data field
                    ServerResultHandler.Success(response.body<T>())
                } else {
                    ServerResultHandler.Error(
                        error = getUserFriendlyErrorMessage(ErrorDataTypes.Remote.serialization.name, exception.message)
                    )
                }
            } catch (exception: Exception) {
                // Handle cases where the error response itself cannot be parsed
                ServerResultHandler.Error(
                    error = getUserFriendlyErrorMessage(ErrorDataTypes.Local.unknown_error.name, exception.message)
                )
            }
        }
    }
}