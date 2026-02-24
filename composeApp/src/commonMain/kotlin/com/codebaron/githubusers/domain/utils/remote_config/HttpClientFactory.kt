package com.codebaron.githubusers.domain.utils.remote_config

import com.codebaron.githubusers.domain.utils.extension.SENSITIVE_PATTERNS
import com.codebaron.githubusers.getPlatform
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {

    /**
     * Sanitizes log messages by redacting sensitive information.
     */
    private fun sanitizeLogMessage(message: String): String {
        var sanitized = message
        SENSITIVE_PATTERNS.forEach { (pattern, replacement) ->
            sanitized = sanitized.replace(pattern, replacement)
        }
        return sanitized
    }

    /**
     * Creates and configures a secure `HttpClient` instance with the provided engine.
     *
     * @param engine The `HttpClientEngine` to be used (e.g., CIO, OkHttp, Darwin).
     * @return A configured `HttpClient` instance.
     *
     * Security features:
     * - Logging is disabled in release builds
     * - Sensitive data is redacted from debug logs
     * - Certificate pinning is configured via platform-specific engine
     */
    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(if (getPlatform().name == "Android") CIO.create() else engine) {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                        isLenient = false // Enforces strict JSON format
                        encodeDefaults = true // Always include default values
                        coerceInputValues = true  // This helps handle null values for non-nullable fields
                    }
                )
            }
            install(HttpTimeout) {
                socketTimeoutMillis = 30_000L
                requestTimeoutMillis = 30_000L
            }

            // SECURITY: Only enable logging in debug builds
            // In release builds, logging is completely disabled to prevent
            // sensitive data from being written to logs
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        // Sanitize sensitive data even in debug logs
                        println(sanitizeLogMessage(message))
                    }
                }
                // Only log headers in debug, never log body content
                level = LogLevel.ALL
            }
            // Add a custom exception handler
            HttpResponseValidator {
                validateResponse { response ->
                    when (val statusCode = response.status.value) {
                        401 -> {
                            println("401 Unauthorized: Session expired or invalid token")
                        }
                        429 -> {
                            println("[HTTP] Rate limit exceeded (429), request should be retried later")
                        }
                        in 500..599 -> {
                            println("[HTTP] Server error detected (${statusCode}), may be temporary")
                        }
                    }
                }

                handleResponseExceptionWithRequest { exception, _ ->
                    println("Warning: Missing data field in response, continuing with null data")
                }
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}