package io.nicolaszurbuchen.yadlo.feature.happening.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.dietaryCoverage
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedKind
import io.nicolaszurbuchen.yadlo.common.plan.domain.repository.PlanRepository
import io.nicolaszurbuchen.yadlo.feature.happening.domain.model.HappeningDetail
import io.nicolaszurbuchen.yadlo.feature.happening.domain.model.HappeningSlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance

/**
 * One Happening's fiche, assembled from the bundle and from what the visitor has kept.
 *
 * The two repositories are combined here rather than stored together — the heart is a join, not a
 * field (SPEC.md § Domain). Nothing in the content model knows whether something is saved, which is
 * what lets a refresh replace the whole bundle without touching a Plan.
 *
 * Emits null when the edition holds no Happening with that id. That is reachable without any bug on
 * the app's side: a fiche is pushed onto a tab and survives process death, so someone can restore
 * the app onto a Happening that a content refresh has since dropped.
 */
class ObserveHappeningDetailUseCase(
    private val contentRepository: ContentRepository,
    private val planRepository: PlanRepository,
) {
    operator fun invoke(happeningId: String): Flow<HappeningDetail?> =
        combine(
            contentRepository.observeStatus().filterIsInstance<ContentStatus.Ready>(),
            planRepository.observeSaved(),
        ) { status, saved ->
            status.bundle.toHappeningDetail(happeningId, saved)
        }

    private fun ContentBundle.toHappeningDetail(
        happeningId: String,
        saved: List<SavedItem>,
    ): HappeningDetail? {
        val happening = edition.happenings.firstOrNull { it.id == happeningId } ?: return null

        val plannedSlotIds = saved.filter { it.kind == SavedKind.SLOT }.mapTo(mutableSetOf()) { it.id }
        val wishlistedStandIds = saved.filter { it.kind == SavedKind.STAND }.mapTo(mutableSetOf()) { it.id }

        val slots =
            edition.slots
                .filter { it.happening.id == happeningId }
                .sortedBy { it.start }
                .map {
                    HappeningSlot(
                        id = it.id,
                        dayName = it.day.name,
                        dayStart = it.day.start,
                        start = it.start,
                        end = it.end,
                        planned = it.id in plannedSlotIds,
                    )
                }

        val activity = happening as? Happening.Activity
        val stand = happening as? Happening.Stand

        return HappeningDetail(
            id = happening.id,
            name = happening.name,
            editionName = edition.name,
            festivalWebsite = festival.website,
            categoryId = happening.category.id,
            categoryName = happening.category.name,
            // The first image or none. Which one is the lead photograph is an authoring decision
            // made by the order they are written in, not one the app should be re-deciding.
            imageUrl = happening.images.firstOrNull()?.url,
            description = happening.description,
            tags =
                when (happening) {
                    is Happening.Artist -> happening.genres

                    is Happening.Activity -> happening.genres

                    // The offering alone: "Cuisine libanaise" answers what someone walking the row
                    // is asking. What the stand can feed you is no longer a word in this list —
                    // it is derived from the menu and drawn with its own glyphs, below.
                    is Happening.Stand -> listOfNotNull(happening.offering)
                },
            dietary = stand?.dietaryCoverage().orEmpty(),
            slots = slots,
            price = activity?.price,
            bookingUrl = activity?.bookingUrl,
            bookingRequired = activity?.bookingRequired == true,
            equipmentProvided = activity?.equipmentProvided,
            suitability = activity?.suitability,
            supervised = activity?.supervised,
            menu = stand?.menu.orEmpty(),
            links =
                when (happening) {
                    is Happening.Artist -> happening.links

                    is Happening.Stand -> happening.links

                    // An Activity's one outward link is its booking page, which is an action rather
                    // than a reference and belongs beside the price it commits you to.
                    is Happening.Activity -> emptyList()
                },
            // Null for anything that is not a Stand, so the fiche knows there is no single heart to
            // draw without being told which of the three kinds it is showing.
            wishlisted = stand?.let { it.id in wishlistedStandIds },
        )
    }
}
