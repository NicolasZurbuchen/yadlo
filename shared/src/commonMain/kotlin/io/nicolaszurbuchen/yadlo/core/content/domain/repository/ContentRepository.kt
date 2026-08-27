package io.nicolaszurbuchen.yadlo.core.content.domain.repository

import io.nicolaszurbuchen.yadlo.core.content.domain.model.ContentStatus
import kotlinx.coroutines.flow.StateFlow

interface ContentRepository {
    /**
     * A [StateFlow] rather than a plain Flow because a late subscriber must see what is already
     * loaded: the content is fetched once at launch and every screen joins afterwards.
     */
    fun observeStatus(): StateFlow<ContentStatus>

    /** Publishes the cache first, then whatever the network improves on. Never throws. */
    suspend fun refresh()
}
