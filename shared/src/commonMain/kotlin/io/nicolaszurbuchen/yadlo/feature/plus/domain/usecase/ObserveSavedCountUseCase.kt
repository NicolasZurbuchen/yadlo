package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedKind
import io.nicolaszurbuchen.yadlo.common.plan.domain.repository.PlanRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.SavedCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * What *Effacer mes données* is offering to remove, counted rather than listed.
 *
 * Observed rather than read once, so the two numbers fall to zero on the same screen the button was
 * pressed on. That is the whole of the feedback this screen gives: there is no toast and no banner,
 * because a count that has become "rien d'enregistré" has already said it, in the place the visitor
 * was looking.
 *
 * It counts what the app can actually show, which is what [PlanRepository.observeSaved] hands back —
 * a row of a kind this build has no bucket for is not in either number. [PlanRepository.clear]
 * deletes it anyway, and the two are right about different things on purpose: this one answers
 * "what have I kept", and that one answers "leave nothing behind".
 */
class ObserveSavedCountUseCase(
    private val planRepository: PlanRepository,
) {
    operator fun invoke(): Flow<SavedCount> =
        planRepository.observeSaved().map { saved ->
            SavedCount(
                slots = saved.count { it.kind == SavedKind.SLOT },
                stands = saved.count { it.kind == SavedKind.STAND },
            )
        }
}
