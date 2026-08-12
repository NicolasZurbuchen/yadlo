package io.nicolaszurbuchen.yadlo.common.content.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Edition
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Phase
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Derives [Phase] from two inputs and no others: the clock, and the content last fetched.
 *
 * Every boundary but one moves on the clock alone. The exception is [Phase.ANNOUNCED], which moves
 * when the programme is *published* — so the trigger is the work itself rather than a step someone
 * has to remember on the one weekend they are not at a laptop.
 *
 * The clock is injected rather than read from the system because the next edition is eleven months
 * away and [Phase.LIVE] is otherwise untestable until then.
 */
class DerivePhaseUseCase(
    private val clock: Clock,
) {
    operator fun invoke(edition: Edition?): Phase {
        if (edition == null || edition.days.isEmpty()) return Phase.OFF_SEASON

        val days = edition.days
        val now = clock.now()

        // The festival's own days, not the device's. A phone in Tokyo must derive the same phase as
        // a phone in Preverenges, so every boundary below is an instant in Zurich.
        val firstDate = days.minBy { it.start }.start.toLocalDateTime(ZURICH).date
        val lastDate = days.maxBy { it.start }.start.toLocalDateTime(ZURICH).date

        val liveStart = firstDate.atStartOfDayIn(ZURICH)
        // Derived from the last day's *start*, never its end: Friday ends at 02:00 on Saturday, so
        // an end-based "morning after" lands a day early the moment a day crosses midnight.
        val liveEnd = lastDate.plus(DatePeriod(days = 1)).atTime(MORNING_AFTER).toInstant(ZURICH)
        val endedEnd = liveEnd.plus(ENDED_DURATION)

        // The order matters: the two clock-only end boundaries are tested before the festival itself,
        // and publication is only consulted once we know we are before it. Both pre-festival phases
        // require a published programme. ANNOUNCED triggers a hero claiming the programme is there,
        // and a date cannot make that claim honestly — a countdown threshold can fire before it is
        // true, an empty slot list cannot. APPROACHING then points at Mon Yadlo to build a Plan,
        // which is equally hollow with nothing to plan. So an edition published early with dates and
        // an empty programme stays OFF_SEASON, which already shows the countdown.
        return when {
            now >= endedEnd -> Phase.OFF_SEASON
            now >= liveEnd -> Phase.ENDED
            now >= liveStart -> Phase.LIVE
            edition.slots.isEmpty() -> Phase.OFF_SEASON
            now >= liveStart.minus(APPROACHING_LEAD) -> Phase.APPROACHING
            else -> Phase.ANNOUNCED
        }
    }

    private companion object {
        val ZURICH = TimeZone.of("Europe/Zurich")

        /**
         * LIVE ends late the next morning rather than when the last day's window closes, so the
         * handover to ENDED lands over breakfast instead of at 22:01 while people are still on the
         * beach finishing a beer.
         */
        val MORNING_AFTER = LocalTime(hour = 11, minute = 0)

        /** J-7. The week before is the only time anyone realistically builds their Plan. */
        val APPROACHING_LEAD = kotlin.time.Duration.parse("7d")

        /** Six weeks of thank-you, then the year starts again. */
        val ENDED_DURATION = kotlin.time.Duration.parse("42d")
    }
}
