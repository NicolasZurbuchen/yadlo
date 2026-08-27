package io.nicolaszurbuchen.yadlo.core.content.domain.model

import io.nicolaszurbuchen.yadlo.core.error.AppError

/**
 * What the app currently has to show. A sealed class rather than an interface because domain models
 * may not be interfaces here.
 */
sealed class ContentStatus {
    /** Nothing fetched and nothing cached — only ever the very first launch. */
    data object Loading : ContentStatus()

    data class Ready(
        val bundle: ContentBundle,
        /**
         * The published content declares a schema this build cannot read, so the cache was kept and
         * the newer file discarded. Drives a **soft** row in Plus and nothing harder: an unofficial
         * festival app that bricks itself on the Saturday afternoon is worse than one showing
         * week-old data.
         */
        val updateRequired: Boolean,
    ) : ContentStatus()

    /**
     * No cache and no successful fetch. Reached on a first launch with no signal, which is the one
     * case where the app has to ask for a connection rather than spin.
     */
    data class Unavailable(
        val error: AppError,
    ) : ContentStatus()
}
