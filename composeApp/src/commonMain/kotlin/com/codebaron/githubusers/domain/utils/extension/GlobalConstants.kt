package com.codebaron.githubusers.domain.utils.extension

val SENSITIVE_PATTERNS = listOf(
    Regex("Bearer [A-Za-z0-9-_.]+") to "Bearer [REDACTED]",
    Regex("\"password\"\\s*:\\s*\"[^\"]+\"") to "\"password\": \"[REDACTED]\"",
    Regex("\"token\"\\s*:\\s*\"[^\"]+\"") to "\"token\": \"[REDACTED]\"",
    Regex("\"accessToken\"\\s*:\\s*\"[^\"]+\"") to "\"accessToken\": \"[REDACTED]\"",
    Regex("\"refreshToken\"\\s*:\\s*\"[^\"]+\"") to "\"refreshToken\": \"[REDACTED]\"",
    Regex("\"fcmToken\"\\s*:\\s*\"[^\"]+\"") to "\"fcmToken\": \"[REDACTED]\"",
    Regex("Authorization:\\s*Bearer [A-Za-z0-9-_.]+") to "Authorization: Bearer [REDACTED]"
)

const val BASE_URL = "https://api.github.com"