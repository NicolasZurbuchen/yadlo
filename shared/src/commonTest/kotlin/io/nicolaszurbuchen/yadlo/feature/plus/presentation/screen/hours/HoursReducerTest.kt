package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.OpeningDay
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

class HoursReducerTest {
    private val reducer = HoursStoreFactory.ReducerImpl

    @Test
    fun daysUpdated_beforeAnyEmission_thereIsNoListRatherThanAnEmptyOne() {
        // Null is "not read yet"; an edition with no days published arrives as an empty list, and
        // the screen owes those two different answers.
        assertNull(HoursState().days)
    }

    @Test
    fun daysUpdated_firstEmission_holdsTheDays() {
        val result = with(reducer) { HoursState().reduce(HoursMessage.DaysUpdated(listOf(friday()))) }

        assertEquals(listOf("2026:fri"), result.days?.map { it.id })
    }

    @Test
    fun daysUpdated_anEditionWithNoDaysYet_becomesAnEmptyListNotANullOne() {
        val state = HoursState(days = listOf(friday()))

        val result = with(reducer) { state.reduce(HoursMessage.DaysUpdated(emptyList())) }

        assertTrue(result.days?.isEmpty() == true)
    }

    private fun friday() =
        OpeningDay(
            id = "2026:fri",
            name = "Vendredi",
            opensAt = Instant.parse("2026-07-10T16:00:00+02:00"),
            closesAt = Instant.parse("2026-07-11T02:00:00+02:00"),
            firstStartsAt = null,
            lastEndsAt = null,
            hoursAreConfirmed = true,
        )
}
