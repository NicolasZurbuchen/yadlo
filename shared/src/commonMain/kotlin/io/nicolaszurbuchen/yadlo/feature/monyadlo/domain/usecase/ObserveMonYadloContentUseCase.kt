package io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedKind
import io.nicolaszurbuchen.yadlo.common.plan.domain.repository.PlanRepository
import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model.MonYadloContent
import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model.PlannedDay
import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model.PlannedSlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance

/**
 * The visitor's own festival: the Slots they kept, in the order they happen, under the days they
 * happen on.
 *
 * **The join is the whole screen.** Saved ids come from one repository and everything else from the
 * other, so a Slot that a refresh has dropped simply stops appearing and comes back if the content
 * does — never a silent removal from someone's Plan (SPEC.md § Refresh).
 *
 * Stands are excluded by construction rather than by filter: only [SavedKind.SLOT] ids are matched
 * against `edition.slots`, and DECISIONS.md § What lands on the timeline says why — the bar being
 * open from 12:00 to 02:00 is not a fourteen-hour appointment.
 */
class ObserveMonYadloContentUseCase(
    private val contentRepository: ContentRepository,
    private val planRepository: PlanRepository,
) {
    operator fun invoke(): Flow<MonYadloContent> =
        combine(
            contentRepository.observeStatus().filterIsInstance<ContentStatus.Ready>(),
            planRepository.observeSaved(),
        ) { status, saved ->
            status.bundle.toMonYadloContent(saved)
        }

    private fun ContentBundle.toMonYadloContent(saved: List<SavedItem>): MonYadloContent {
        val plannedIds = saved.filter { it.kind == SavedKind.SLOT }.mapTo(mutableSetOf()) { it.id }
        val wishlistedIds = saved.filter { it.kind == SavedKind.STAND }.mapTo(mutableSetOf()) { it.id }

        val planned =
            edition.slots
                .filter { it.id in plannedIds }
                // Time, then the shorter of two things starting together — the Programme's order,
                // because this is the same day read back.
                .sortedWith(compareBy({ it.start }, { it.end }))
                .groupBy { it.day.id }

        return MonYadloContent(
            // Days the visitor saved nothing on are absent rather than empty. Three headers with one
            // row under them says less about someone's festival than one header does.
            days =
                edition.days
                    .sortedBy { it.start }
                    .filter { planned.containsKey(it.id) }
                    .map { day ->
                        PlannedDay(
                            id = day.id,
                            name = day.name,
                            start = day.start,
                            slots =
                                planned.getValue(day.id).map { slot ->
                                    PlannedSlot(
                                        id = slot.id,
                                        happeningId = slot.happening.id,
                                        name = slot.happening.name,
                                        categoryId = slot.happening.category.id,
                                        categoryName = slot.happening.category.name,
                                        start = slot.start,
                                        end = slot.end,
                                        // The variant test stays in the layer allowed to ask it,
                                        // as on the Programme: only an Activity has a price.
                                        price = (slot.happening as? Happening.Activity)?.price,
                                    )
                                },
                        )
                    },
            // Counted against the content rather than off the saved list, so a Stand the edition no
            // longer declares does not inflate a tile that would then open onto fewer rows.
            wishlistCount =
                edition.happenings.count { it is Happening.Stand && it.id in wishlistedIds },
        )
    }
}
