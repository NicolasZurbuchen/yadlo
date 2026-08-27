package io.nicolaszurbuchen.yadlo.core.content.domain.usecase

import io.nicolaszurbuchen.yadlo.core.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Phase
import io.nicolaszurbuchen.yadlo.core.time.APPROACHING_LEAD
import io.nicolaszurbuchen.yadlo.core.time.liveEnd
import io.nicolaszurbuchen.yadlo.core.time.liveStart
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
        val liveStart = days.liveStart() ?: return Phase.OFF_SEASON
        val liveEnd = days.liveEnd() ?: return Phase.OFF_SEASON

        val now = clock.now()
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
        val ENDED_DURATION = 42.days
    }
}
