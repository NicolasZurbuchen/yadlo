package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.plan.domain.repository.PlanRepository

/**
 * Forgets the Plan and the Wishlist together — the destructive half of *Effacer mes données*.
 *
 * **Nothing here cancels a reminder, and that is deliberate rather than missed.** `ReminderEffects`
 * recomputes the whole schedule from what is saved every time the Plan changes, and
 * `PlanRemindersUseCase` returns the complete desired state rather than a delta, so an emptied Plan
 * cancels its reminders by omission — the same path an unhearted Slot already takes. A cancellation
 * written here would be a second mechanism doing the first one's job, and the two would only be
 * noticed when they disagreed.
 */
class ClearSavedUseCase(
    private val planRepository: PlanRepository,
) {
    suspend operator fun invoke() {
        planRepository.clear()
    }
}
