package io.nicolaszurbuchen.yadlo.core.content.domain.fake

import io.nicolaszurbuchen.yadlo.core.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.core.content.domain.repository.ContentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared by every feature that reads the content bundle, which is eventually all of them — the
 * repository is in `common/`, so its fake belongs beside it rather than inside the first feature
 * that happened to need one.
 */
class FakeContentRepository : ContentRepository {
    private val status = MutableStateFlow<ContentStatus>(ContentStatus.Loading)

    var refreshCallCount: Int = 0
        private set

    override fun observeStatus(): StateFlow<ContentStatus> = status.asStateFlow()

    override suspend fun refresh() {
        refreshCallCount++
    }

    fun emitStatus(value: ContentStatus) {
        status.value = value
    }
}
