package com.codebaron.githubusers.domain.utils.remote_config

interface ErrorDataTypes : ErrorInterface {

    /**
     * Enum class representing errors that originate from remote sources,
     * such as network failures, server issues, or unauthorized access.
     */
    enum class Remote : ErrorDataTypes {
        /** The request timed out before a response was received. */
        request_timeout,

        /** The client has sent too many requests in a short period. */
        too_many_requests,

        /** No internet connection is available. */
        no_internet,

        /** A general server-side error occurred. */
        server,

        /** Error occurred while serializing or deserializing data. */
        serialization,

        /** The host could not be found, possibly due to DNS issues. */
        unknown_host,

        /** The request was unauthorized (e.g., invalid credentials or expired session). */
        unauthorized
    }

    /**
     * Enum class representing errors that occur locally on the device,
     * such as storage issues or unknown internal failures.
     */
    enum class Local : ErrorDataTypes {
        /** The device's disk storage is full, preventing data operations. */
        disk_full,

        /** An unspecified or unknown error occurred. */
        unknown_error
    }
}