package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.OpeningDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * *Horaires*, deduced rather than authored.
 *
 * A FestivalDay's `start` and `end` **are** the opening hours — not a bounding box around the
 * programme — so the one question this screen exists to answer is already in the content. The two
 * programme instants are read off that day's Slots, which is why the screen needed no new field and
 * could ship before the association published anything.
 *
 * Days are sorted by when they open. Slots are searched by instant rather than filtered by the
 * calendar: a Slot belongs to the day its `dayId` names, and a 01:30 set on Saturday morning is
 * Friday's last one.
 */
class ObserveOpeningDaysUseCase(
    private val contentRepository: ContentRepository,
) {
    operator fun invoke(): Flow<List<OpeningDay>> =
        contentRepository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { status ->
                val edition = status.bundle.edition
                val slotsByDay = edition.slots.groupBy { it.day.id }

                edition.days.sortedBy { it.start }.map { day ->
                    val slots = slotsByDay[day.id].orEmpty()

                    OpeningDay(
                        id = day.id,
                        name = day.name,
                        opensAt = day.start,
                        closesAt = day.end,
                        firstStartsAt = slots.minOfOrNull { it.start },
                        lastEndsAt = slots.maxOfOrNull { it.end },
                        hoursAreConfirmed = day.provenance == Provenance.CONFIRMED,
                    )
                }
            }
}
