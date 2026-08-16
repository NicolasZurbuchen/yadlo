package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FaqEntry
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * The questions the festival answers in its own words.
 *
 * One entry today — whether entry is free — and that one is the reason the screen exists at all:
 * the plainest question a first-time visitor asks had no home in any of the four tabs, because the
 * association's information is split between a stale website and a live Instagram.
 */
class ObserveFaqUseCase(
    private val contentRepository: ContentRepository,
) {
    operator fun invoke(): Flow<List<FaqEntry>> =
        contentRepository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { it.bundle.festival.faq }
}
