package io.nicolaszurbuchen.yadlo.common.content.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Phase
import io.nicolaszurbuchen.yadlo.common.time.FESTIVAL_TIME_ZONE
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

/**
 * Derives [Phase] from the clock and the content last fetched, and nothing else.
 *
 * The clock is injected because the next edition is eleven months away and [Phase.LIVE] is
 * otherwise untestable until then.
 */
class DerivePhaseUseCase(
    private val clock: Clock,
) {
    /**
     * Takes what it reads rather than an Edition, so the signature says what changes a phase and a
     * test does not have to build a festival to exercise one boundary.
     */
    operator fun invoke(
        days: List<FestivalDay>,
        hasPublishedProgramme: Boolean,
    ): Phase {
        if (days.isEmpty()) return Phase.OFF_SEASON

        val now = clock.now()
        val firstDate = days.minBy { it.start }.start.toLocalDateTime(FESTIVAL_TIME_ZONE).date
        val lastDate = days.maxBy { it.start }.start.toLocalDateTime(FESTIVAL_TIME_ZONE).date

        val liveStart = firstDate.atStartOfDayIn(FESTIVAL_TIME_ZONE)
        // From the last day's start, never its end: Friday ends at 02:00 on Saturday, so an
        // end-based "morning after" lands a day early the moment any day crosses midnight.
        val liveEnd =
            lastDate.plus(DatePeriod(days = 1)).atTime(MORNING_AFTER).toInstant(FESTIVAL_TIME_ZONE)
        val endedEnd = liveEnd.plus(ENDED_DURATION)

        // Both pre-festival phases require a published programme: ANNOUNCED triggers a hero
        // claiming the programme is there, and APPROACHING points at a Plan there is nothing to
        // build. LIVE and ENDED stay clock-only — the festival happens either way.
        return when {
            now >= endedEnd -> Phase.OFF_SEASON
            now >= liveEnd -> Phase.ENDED
            now >= liveStart -> Phase.LIVE
            !hasPublishedProgramme -> Phase.OFF_SEASON
            now >= liveStart.minus(APPROACHING_LEAD) -> Phase.APPROACHING
            else -> Phase.ANNOUNCED
        }
    }

    private companion object {
        /** So the handover to ENDED lands over breakfast, not at 22:01 while people are still out. */
        val MORNING_AFTER = LocalTime(hour = 11, minute = 0)
        val APPROACHING_LEAD = 7.days
        val ENDED_DURATION = 42.days
    }
}
