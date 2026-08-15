package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.AccessibilityGuide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * *Accessibilité*, split into what works and what does not, with an address to write to.
 *
 * The email is resolved here rather than passed to the screen as an id, because the id is a join
 * the content asked for and the screen has no business knowing that `contactEmailId` exists. It
 * resolves to null when the content names an address its own list does not hold — a content bug the
 * screen renders as one fewer row rather than as a `mailto:` to nowhere.
 */
class ObserveAccessibilityGuideUseCase(
    private val contentRepository: ContentRepository,
) {
    operator fun invoke(): Flow<AccessibilityGuide?> =
        contentRepository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { status ->
                val festival = status.bundle.festival

                festival.accessibility?.let { accessibility ->
                    AccessibilityGuide(
                        available = accessibility.items.filter { it.available },
                        unavailable = accessibility.items.filterNot { it.available },
                        contactEmail =
                            festival.contact
                                ?.emails
                                ?.firstOrNull { it.id == accessibility.contactEmailId }
                                ?.address,
                    )
                }
            }
}
