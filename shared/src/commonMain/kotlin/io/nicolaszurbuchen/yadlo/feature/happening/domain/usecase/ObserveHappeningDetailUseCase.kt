package io.nicolaszurbuchen.yadlo.feature.happening.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.happening.domain.model.HappeningDetail
import io.nicolaszurbuchen.yadlo.feature.happening.domain.model.HappeningSlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * One Happening's fiche, assembled from the bundle.
 *
 * Emits null when the edition holds no Happening with that id. That is reachable without any bug on
 * the app's side: a fiche is pushed onto a tab and survives process death, so someone can restore
 * the app onto a Happening that a content refresh has since dropped.
 */
class ObserveHappeningDetailUseCase(
    private val repository: ContentRepository,
) {
    operator fun invoke(happeningId: String): Flow<HappeningDetail?> =
        repository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { it.bundle.toHappeningDetail(happeningId) }

    private fun ContentBundle.toHappeningDetail(happeningId: String): HappeningDetail? {
        val happening = edition.happenings.firstOrNull { it.id == happeningId } ?: return null

        val slots =
            edition.slots
                .filter { it.happening.id == happeningId }
                .sortedBy { it.start }
                .map { HappeningSlot(id = it.id, dayName = it.day.name, start = it.start, end = it.end) }

        val activity = happening as? Happening.Activity
        val stand = happening as? Happening.Stand

        return HappeningDetail(
            id = happening.id,
            name = happening.name,
            categoryId = happening.category.id,
            categoryName = happening.category.name,
            description = happening.description,
            tags =
                when (happening) {
                    is Happening.Artist -> happening.genres

                    is Happening.Activity -> happening.genres

                    // The offering first, because it is the one every stand has: "Cuisine
                    // libanaise" answers what someone walking the row is asking, and the marks
                    // qualify it. A Stand's marks describe everything it sells, so they are
                    // written as they are authored — `végan` here means the whole truck.
                    is Happening.Stand -> listOfNotNull(happening.offering) + happening.marks
                },
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
        )
    }
}
