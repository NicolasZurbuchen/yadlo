package io.nicolaszurbuchen.yadlo.common.content.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Phase
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Instant

class DerivePhaseUseCaseTest {
    // The 2026 edition ran 10-12 July. Every boundary below is written as an absolute instant rather
    // than computed from the same expression the production code uses, so a derivation that drifts
    // by an hour still fails.

    @Test
    fun noDays_isOffSeason() {
        // Either no edition has been fetched, or one was published as a placeholder before the dates
        // were known. From here the two are the same thing.
        assertEquals(
            Phase.OFF_SEASON,
            derive(at = "2026-07-11T14:00:00+02:00", days = emptyList()),
        )
    }

    @Test
    fun everyBoundary_resolvesToTheExpectedPhase() {
        val cases =
            listOf(
                // Well before the festival, with a programme already published.
                "2026-06-01T12:00:00+02:00" to Phase.ANNOUNCED,
                // The last instant before J-7.
                "2026-07-02T23:59:59+02:00" to Phase.ANNOUNCED,
                // J-7 exactly.
                "2026-07-03T00:00:00+02:00" to Phase.APPROACHING,
                "2026-07-09T23:59:59+02:00" to Phase.APPROACHING,
                // Midnight on day one. The gates do not open until 16:00, and that gap is the point:
                // a phase says where the user head is, not when content happens.
                "2026-07-10T00:00:00+02:00" to Phase.LIVE,
                "2026-07-10T08:00:00+02:00" to Phase.LIVE,
                // 01:30 on the Saturday morning, which belongs to Friday window.
                "2026-07-11T01:30:00+02:00" to Phase.LIVE,
                // The dead hours between two days. Still LIVE.
                "2026-07-11T05:00:00+02:00" to Phase.LIVE,
                // The last day closes at 22:00; the phase does not.
                "2026-07-12T23:00:00+02:00" to Phase.LIVE,
                "2026-07-13T10:59:59+02:00" to Phase.LIVE,
                // 11:00 the morning after, so the handover lands over breakfast.
                "2026-07-13T11:00:00+02:00" to Phase.ENDED,
                "2026-08-24T10:59:59+02:00" to Phase.ENDED,
                // Six weeks later the year starts again.
                "2026-08-24T11:00:00+02:00" to Phase.OFF_SEASON,
            )

        cases.forEach { (instant, expected) ->
            assertEquals(expected, derive(at = instant), "at $instant")
        }
    }

    @Test
    fun daysButNoProgramme_staysOffSeasonBeforeTheFestival() {
        // The hero ANNOUNCED triggers claims the programme is there. A date cannot make that claim
        // honestly, so an edition published early with dates and an empty programme stays in
        // OFF_SEASON, which already shows a countdown.
        assertEquals(
            Phase.OFF_SEASON,
            derive(at = "2026-06-01T12:00:00+02:00", hasPublishedProgramme = false),
        )
        assertEquals(
            Phase.OFF_SEASON,
            derive(at = "2026-07-05T12:00:00+02:00", hasPublishedProgramme = false),
        )
    }

    @Test
    fun daysButNoProgramme_isStillLiveDuringTheFestival() {
        // The festival happens whether or not anyone published a programme, and LIVE is what makes
        // the app open on Programme. Only the two pre-festival phases key off publication.
        assertEquals(
            Phase.LIVE,
            derive(at = "2026-07-11T14:00:00+02:00", hasPublishedProgramme = false),
        )
    }

    @Test
    fun deviceInAnotherTimezone_derivesTheSamePhase() {
        // The same absolute instant, written with a Tokyo offset. Someone flying in must not see a
        // different phase from someone already on the beach, which is why nothing here reads a wall
        // clock.
        val zurichMidnight = derive(at = "2026-07-10T00:00:00+02:00")
        val sameInstantInTokyo = derive(at = "2026-07-10T07:00:00+09:00")

        assertEquals(zurichMidnight, sameInstantInTokyo)
        assertEquals(Phase.LIVE, sameInstantInTokyo)
    }

    @Test
    fun boundariesHold_whenTheEditionIsNotInSummer() {
        // A winter edition sits in CET rather than CEST. A hardcoded +02:00 anywhere in the
        // derivation would move every boundary by an hour, and nothing else in this suite would say
        // so, because the real edition is in July.
        val winter =
            listOf(
                day(
                    id = "2027:sat",
                    start = "2027-01-16T16:00:00+01:00",
                    end = "2027-01-17T02:00:00+01:00",
                ),
            )

        assertEquals(Phase.APPROACHING, derive(at = "2027-01-09T00:00:00+01:00", days = winter))
        assertEquals(Phase.LIVE, derive(at = "2027-01-16T00:00:00+01:00", days = winter))
        assertEquals(Phase.ENDED, derive(at = "2027-01-17T11:00:00+01:00", days = winter))
    }

    private fun derive(
        at: String,
        days: List<FestivalDay> = defaultDays(),
        hasPublishedProgramme: Boolean = true,
    ): Phase = DerivePhaseUseCase(FixedClock(Instant.parse(at))).invoke(days, hasPublishedProgramme)

    private fun defaultDays() =
        listOf(
            day(id = "2026:fri", start = "2026-07-10T16:00:00+02:00", end = "2026-07-11T02:00:00+02:00"),
            day(id = "2026:sat", start = "2026-07-11T12:00:00+02:00", end = "2026-07-12T03:00:00+02:00"),
            day(id = "2026:sun", start = "2026-07-12T12:00:00+02:00", end = "2026-07-12T22:00:00+02:00"),
        )

    private fun day(
        id: String,
        start: String,
        end: String,
    ) = FestivalDay(
        id = id,
        name = id,
        date = start.substringBefore("T"),
        start = Instant.parse(start),
        end = Instant.parse(end),
        provenance = Provenance.CONFIRMED,
    )

    /** Not named Fake* on purpose: that prefix is reserved for shared doubles in domain/fake. */
    private class FixedClock(
        private val instant: Instant,
    ) : Clock {
        override fun now(): Instant = instant
    }
}
