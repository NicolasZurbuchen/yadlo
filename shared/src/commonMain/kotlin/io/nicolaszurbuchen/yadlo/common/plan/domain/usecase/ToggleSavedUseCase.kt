package io.nicolaszurbuchen.yadlo.common.plan.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedKind
import io.nicolaszurbuchen.yadlo.common.plan.domain.repository.PlanRepository

/**
 * One heart tap.
 *
 * The edition is read here rather than passed in, so no screen has to carry a field it never draws
 * in order to save something. It is the one fact a saved row needs that the tap itself cannot
 * supply, and the content is already in memory by the time any heart is on screen.
 */
class ToggleSavedUseCase(
    private val planRepository: PlanRepository,
    private val contentRepository: ContentRepository,
) {
    suspend operator fun invoke(
        id: String,
        kind: SavedKind,
    ) {
        // Not reachable from the app as it stands — the tab shell is not composed until the bundle
        // is ready, so nothing with a heart on it has been drawn yet. Saving a row that could not
        // name its edition is the one outcome worth refusing outright rather than guessing at.
        val ready = contentRepository.observeStatus().value as? ContentStatus.Ready ?: return

        planRepository.toggle(SavedItem(id = id, kind = kind, editionId = ready.bundle.edition.id))
    }
}
