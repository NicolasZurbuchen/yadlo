package io.nicolaszurbuchen.yadlo.common.error

sealed interface AppError {
    sealed interface Network : AppError {
        data object Unavailable : Network

        data object Timeout : Network

        data class Http(
            val code: Int,
            val serverMessage: String? = null,
        ) : Network
    }

    sealed interface Database : AppError {
        data class QueryFailed(
            val cause: Throwable,
        ) : Database

        data class InsertFailed(
            val cause: Throwable,
        ) : Database
    }

    /**
     * A bundle that parsed but does not hold together. Never a network fault — the bytes arrived
     * intact and say something the app cannot act on, so retrying the same URL changes nothing.
     */
    sealed interface Content : AppError {
        /** A reference naming something its own bundle never declares. */
        data class UnresolvedReference(
            val field: String,
            val id: String,
        ) : Content

        /** A field this build cannot read: an unknown value, or a payload its kind promised. */
        data class MalformedField(
            val field: String,
            val detail: String,
        ) : Content
    }

    sealed interface PokemonExplorer : AppError {
        data object FetchFailed : PokemonExplorer
    }

    data class Unexpected(
        val cause: Throwable,
    ) : AppError
}
