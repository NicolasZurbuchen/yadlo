package io.nicolaszurbuchen.yadlo.feature.home.domain.usecase

import io.nicolaszurbuchen.yadlo.core.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Phase
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.feature.home.domain.model.SiteMoment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/**
 * The 2026 edition's real windows, because the boundaries only mean something against real ones:
 * Friday opens at 16:00 and runs to 02:00, Saturday from 12:00 to 03:00, Sunday from 12:00 to
 * 22:00. Two of the three cross midnight and the last does not, which is exactly the shape that
 * catches a naive implementation.
 */
class DeriveSiteMomentUseCaseTest {
    @Test
    fun invoke_noDaysPublished_hasNothingToSay() {
        val useCase = DeriveSiteMomentUseCase(FixedClock(FRIDAY_MORNING))

        assertNull(useCase(days = emptyList()))
    }

    // region before the first day

    @Test
    fun invoke_onTheOpeningMorning_isBeforeTheFirstDay() {
        // LIVE has already begun — it starts at midnight — and the beach has not.
        val useCase = DeriveSiteMomentUseCase(FixedClock(FRIDAY_MORNING))

        assertEquals(SiteMoment.BeforeFirstDay(FRIDAY_OPENS), useCase(days = days()))
    }

    @Test
    fun invoke_oneMinuteBeforeTheGates_isStillBeforeTheFirstDay() {
        val useCase = DeriveSiteMomentUseCase(FixedClock(FRIDAY_OPENS.minus(ONE_MINUTE)))

        assertEquals(SiteMoment.BeforeFirstDay(FRIDAY_OPENS), useCase(days = days()))
    }

    // endregion

    // region open

    @Test
    fun invoke_atTheInstantTheGatesOpen_isAlreadyOpen() {
        // The window is half-open at its start: the minute it begins belongs to the day.
        val useCase = DeriveSiteMomentUseCase(FixedClock(FRIDAY_OPENS))

        assertEquals(SiteMoment.Open(FRIDAY_CLOSES), useCase(days = days()))
    }

    @Test
    fun invoke_afterMidnightOnAWindowThatCrossesIt_isStillTheSameDayAndStillOpen() {
        // 01:30 on the Saturday morning is Friday, and Friday is open until 02:00. A day derived by
        // truncating an instant to a calendar date gets this wrong, which is why nothing here does.
        val useCase = DeriveSiteMomentUseCase(FixedClock(Instant.parse("2026-07-11T01:30:00+02:00")))

        assertEquals(SiteMoment.Open(FRIDAY_CLOSES), useCase(days = days()))
    }

    // endregion

    // region closed between two days

    @Test
    fun invoke_atTheInstantAWindowEnds_hasAlreadyClosed() {
        // Half-open at its end too, so 02:00 belongs to the gap rather than to both.
        val useCase = DeriveSiteMomentUseCase(FixedClock(FRIDAY_CLOSES))

        assertEquals(SiteMoment.Closed(SATURDAY_OPENS), useCase(days = days()))
    }

    @Test
    fun invoke_theMorningAfterANight_isClosedAndNamesTheReopening() {
        // 10:00 on the Saturday. Not "before the first day" — the festival has started — and not
        // "over", because there are two days left.
        val useCase = DeriveSiteMomentUseCase(FixedClock(Instant.parse("2026-07-11T10:00:00+02:00")))

        assertEquals(SiteMoment.Closed(SATURDAY_OPENS), useCase(days = days()))
    }

    // endregion

    // region finished

    @Test
    fun invoke_afterTheLastWindowCloses_isFinished() {
        // 22:00 on the Sunday, and the last day is the one that does *not* run past midnight — so
        // this begins far earlier in the evening than the two nights before it did.
        val useCase = DeriveSiteMomentUseCase(FixedClock(SUNDAY_CLOSES))

        assertEquals(SiteMoment.Finished, useCase(days = days()))
    }

    @Test
    fun invoke_theMondayMorningBeforeTheHandover_isStillFinishedRatherThanNull() {
        // Phase.ENDED takes over at 11:00. Until then this is what Accueil has to say, and it must
        // not fall through to nothing.
        val useCase = DeriveSiteMomentUseCase(FixedClock(Instant.parse("2026-07-13T09:00:00+02:00")))

        assertEquals(SiteMoment.Finished, useCase(days = days()))
    }

    // endregion

    @Test
    fun invoke_daysPublishedOutOfOrder_readsThemInTimeOrderAnyway() {
        // The order of the array in a published file is not a guarantee, and every branch of this
        // reads "the first" or "the next".
        val useCase = DeriveSiteMomentUseCase(FixedClock(FRIDAY_MORNING))

        assertEquals(SiteMoment.BeforeFirstDay(FRIDAY_OPENS), useCase(days = days().reversed()))
    }

    private fun days() =
        listOf(
            day("2026:fri", "Vendredi", "2026-07-10", FRIDAY_OPENS, FRIDAY_CLOSES),
            day("2026:sat", "Samedi", "2026-07-11", SATURDAY_OPENS, SATURDAY_CLOSES),
            day("2026:sun", "Dimanche", "2026-07-12", SUNDAY_OPENS, SUNDAY_CLOSES),
        )

    private fun day(
        id: String,
        name: String,
        date: String,
        start: Instant,
        end: Instant,
    ) = FestivalDay(
        id = id,
        name = name,
        date = date,
        start = start,
        end = end,
        provenance = Provenance.CONFIRMED,
    )

    /** The same shape DerivePhaseUseCaseTest uses: this reads a clock and nothing else. */
    private class FixedClock(
        private val instant: Instant,
    ) : Clock {
        override fun now(): Instant = instant
    }

    private companion object {
        val FRIDAY_OPENS = Instant.parse("2026-07-10T16:00:00+02:00")
        val FRIDAY_CLOSES = Instant.parse("2026-07-11T02:00:00+02:00")
        val SATURDAY_OPENS = Instant.parse("2026-07-11T12:00:00+02:00")
        val SATURDAY_CLOSES = Instant.parse("2026-07-12T03:00:00+02:00")
        val SUNDAY_OPENS = Instant.parse("2026-07-12T12:00:00+02:00")
        val SUNDAY_CLOSES = Instant.parse("2026-07-12T22:00:00+02:00")

        /** 09:00 on the opening Friday: inside LIVE, seven hours outside the gates. */
        val FRIDAY_MORNING = Instant.parse("2026-07-10T09:00:00+02:00")

        val ONE_MINUTE = 1.minutes
    }
}
