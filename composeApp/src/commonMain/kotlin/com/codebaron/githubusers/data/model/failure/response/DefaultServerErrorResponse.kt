package com.codebaron.githubusers.data.model.failure.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DefaultServerErrorResponse(
    @SerialName("error")
    val error: List<String> = listOf(),
    @SerialName("message")
    val message: String = "",
    @SerialName("status")
    val status: Boolean = false,
    @SerialName("statusCode")
    val statusCode: Int = 0
) {
    fun getFormattedErrorMessage(): String {
        return when {
            error.isNotEmpty() -> {
                if (error.size == 1) {
                    error.first()
                } else {
                    error.joinToString("\n")
                }
            }
            else -> message
        }
    }
}