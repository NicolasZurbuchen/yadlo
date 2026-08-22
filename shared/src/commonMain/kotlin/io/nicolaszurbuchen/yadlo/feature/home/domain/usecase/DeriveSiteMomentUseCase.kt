package io.nicolaszurbuchen.yadlo.feature.home.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.feature.home.domain.model.SiteMoment
import kotlin.time.Clock

/**
 * Derives [SiteMoment] from the clock and the Edition's days, and nothing else.
 *
 * The clock is injected for the same reason `DerivePhaseUseCase`'s is: every boundary this function
 * has is inside one weekend eleven months away, and the interesting ones — a window that ends at
 * 02:00 the following morning, the gap after the last day — are untestable otherwise.
 *
 * Total by construction rather than only correct inside `Phase.LIVE`. It answers "where is now
 * against these windows" whenever it is asked, so a caller cannot get a wrong answer by asking at
 * the wrong time; it simply has nothing to say when no day has been published.
 */
class DeriveSiteMomentUseCase(
    private val clock: Clock,
) {
    /**
     * Null when the Edition publishes no days, which is the between-editions case rather than an
     * error — the same answer `DerivePhaseUseCase` gives it.
     */
    operator fun invoke(days: List<FestivalDay>): SiteMoment? {
        if (days.isEmpty()) return null

        val now = clock.now()
        // Sorted here rather than trusted from the content: the order of the array in a published
        // file is not a guarantee, and every branch below reads "the first" or "the next".
        val sorted = days.sortedBy { it.start }

        // Half-open, so the instant a window ends belongs to the gap after it rather than to both.
        val open = sorted.firstOrNull { now >= it.start && now < it.end }
        if (open != null) return SiteMoment.Open(open.end)

        val next = sorted.firstOrNull { now < it.start }

        return when {
            next == null -> SiteMoment.Finished
            now < sorted.first().start -> SiteMoment.BeforeFirstDay(next.start)
            else -> SiteMoment.Closed(next.start)
        }
    }
}
