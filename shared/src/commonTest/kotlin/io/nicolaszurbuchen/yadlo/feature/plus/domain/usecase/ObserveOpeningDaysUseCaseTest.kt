package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

class ObserveOpeningDaysUseCaseTest {
    @Test
    fun invoke_noDaysPublished_isEmpty() =
        runTest {
            assertTrue(daysFrom(FakeContentRepository().apply { emitStatus(ready()) }).isEmpty())
        }

    @Test
    fun invoke_theWindow_isTheFestivalDaysOwnStartAndEnd() =
        runTest {
            val friday = FRIDAY
            val repository = FakeContentRepository().apply { emitStatus(ready(days = listOf(friday))) }

            val day = daysFrom(repository).single()

            // A FestivalDay's start and end *are* the opening hours, which is what let this screen
            // ship without a single new content field.
            assertEquals(friday.start, day.opensAt)
            assertEquals(friday.end, day.closesAt)
        }

    @Test
    fun invoke_daysAreSortedByWhenTheyOpen() =
        runTest {
            val repository =
                FakeContentRepository().apply { emitStatus(ready(days = listOf(SATURDAY, FRIDAY))) }

            assertEquals(listOf("2026:fri", "2026:sat"), daysFrom(repository).map { it.id })
        }

    @Test
    fun invoke_theProgrammeWindow_spansTheFirstAndLastSlotOfThatDay() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            days = listOf(FRIDAY),
                            slots =
                                listOf(
                                    slot("2026:b", FRIDAY, "2026-07-10T20:00:00+02:00", "2026-07-10T22:00:00+02:00"),
                                    slot("2026:a", FRIDAY, "2026-07-10T17:00:00+02:00", "2026-07-10T18:30:00+02:00"),
                                ),
                        ),
                    )
                }

            val day = daysFrom(repository).single()

            // Read off the programme rather than declared order: the earliest start and the latest
            // end, whatever sequence the file happens to list them in.
            assertEquals(Instant.parse("2026-07-10T17:00:00+02:00"), day.firstStartsAt)
            assertEquals(Instant.parse("2026-07-10T22:00:00+02:00"), day.lastEndsAt)
        }

    @Test
    fun invoke_aSlotPastMidnight_stillBelongsToTheDayItWasProgrammedOn() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            days = listOf(FRIDAY),
                            // 01:30 by the calendar is Saturday. It is Friday's last set, because a
                            // FestivalDay is a window and its dayId is authored, never derived.
                            slots =
                                listOf(
                                    slot("2026:late", FRIDAY, "2026-07-11T01:00:00+02:00", "2026-07-11T01:30:00+02:00"),
                                ),
                        ),
                    )
                }

            assertEquals(Instant.parse("2026-07-11T01:30:00+02:00"), daysFrom(repository).single().lastEndsAt)
        }

    @Test
    fun invoke_slotsAreNotBorrowedFromAnotherDay() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            days = listOf(FRIDAY, SATURDAY),
                            slots =
                                listOf(
                                    slot("2026:fri-set", FRIDAY, "2026-07-10T17:00:00+02:00", "2026-07-10T18:00:00+02:00"),
                                    slot("2026:sat-set", SATURDAY, "2026-07-11T14:00:00+02:00", "2026-07-11T15:00:00+02:00"),
                                ),
                        ),
                    )
                }

            val days = daysFrom(repository)

            assertEquals(Instant.parse("2026-07-10T17:00:00+02:00"), days.first().firstStartsAt)
            assertEquals(Instant.parse("2026-07-11T14:00:00+02:00"), days.last().firstStartsAt)
        }

    @Test
    fun invoke_aDayWithNothingProgrammed_stillListsItsOpeningHours() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(days = listOf(FRIDAY))) }

            val day = daysFrom(repository).single()

            // The site being open is a fact of its own, and a day with no programme is what a
            // half-published edition looks like in March.
            assertNull(day.firstStartsAt)
            assertNull(day.lastEndsAt)
            assertEquals(FRIDAY.start, day.opensAt)
        }

    @Test
    fun invoke_derivedHours_areFlaggedSoTheScreenCanSaySo() =
        runTest {
            val estimated =
                day(
                    id = "2026:fri",
                    name = "Vendredi",
                    start = "2026-07-10T16:00:00+02:00",
                    end = "2026-07-11T02:00:00+02:00",
                    provenance = Provenance.UNVERIFIED,
                )
            val repository = FakeContentRepository().apply { emitStatus(ready(days = listOf(estimated))) }

            assertFalse(daysFrom(repository).single().hoursAreConfirmed)
        }

    @Test
    fun invoke_publishedHours_carryNoCaveat() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(days = listOf(FRIDAY))) }

            assertTrue(daysFrom(repository).single().hoursAreConfirmed)
        }

    private suspend fun daysFrom(repository: FakeContentRepository) = ObserveOpeningDaysUseCase(repository)().first()

    private companion object {
        val FRIDAY =
            day(
                id = "2026:fri",
                name = "Vendredi",
                start = "2026-07-10T16:00:00+02:00",
                end = "2026-07-11T02:00:00+02:00",
            )

        val SATURDAY =
            day(
                id = "2026:sat",
                name = "Samedi",
                start = "2026-07-11T12:00:00+02:00",
                end = "2026-07-12T03:00:00+02:00",
            )
    }
}
