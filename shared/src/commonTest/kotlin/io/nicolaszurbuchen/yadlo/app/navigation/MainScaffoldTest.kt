package io.nicolaszurbuchen.yadlo.app.navigation

import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

/**
 * The app bar's date range. It reads from the FestivalDay *start* instants in Zurich rather than
 * from the display `date` strings, which is the same rule the rest of the app follows: a day that
 * ends at 02:00 the next morning must not widen the range by a day.
 */
class MainScaffoldTest {
    @Test
    fun formatEditionDates_threeDaysInOneMonth_collapsesTheSharedMonthAndYear() {
        val days = listOf(day("2026-07-10T16:00:00+02:00"), day("2026-07-11T12:00:00+02:00"), day("2026-07-12T12:00:00+02:00"))

        assertEquals("10 – 12.07.2026", formatEditionDates(days))
    }

    @Test
    fun formatEditionDates_daysGivenOutOfOrder_stillReadsFirstToLast() {
        val days = listOf(day("2026-07-12T12:00:00+02:00"), day("2026-07-10T16:00:00+02:00"))

        assertEquals("10 – 12.07.2026", formatEditionDates(days))
    }

    @Test
    fun formatEditionDates_theEditionStraddlesAMonth_keepsBothMonths() {
        val days = listOf(day("2027-06-30T16:00:00+02:00"), day("2027-07-02T12:00:00+02:00"))

        assertEquals("30.06 – 02.07.2027", formatEditionDates(days))
    }

    @Test
    fun formatEditionDates_theEditionStraddlesAYear_keepsBothYears() {
        val days = listOf(day("2026-12-31T16:00:00+01:00"), day("2027-01-01T12:00:00+01:00"))

        assertEquals("31.12.2026 – 01.01.2027", formatEditionDates(days))
    }

    @Test
    fun formatEditionDates_aSingleDay_readsAsOneDateRatherThanARange() {
        assertEquals("10.07.2026", formatEditionDates(listOf(day("2026-07-10T16:00:00+02:00"))))
    }

    @Test
    fun formatEditionDates_aDayEndingAfterMidnight_doesNotWidenTheRange() {
        // Friday runs 16:00 to 02:00 on Saturday. Reading the end instant instead of the start
        // would print a three-day festival as four.
        val friday = day(start = "2026-07-10T16:00:00+02:00", end = "2026-07-11T02:00:00+02:00")

        assertEquals("10.07.2026", formatEditionDates(listOf(friday)))
    }

    @Test
    fun formatEditionDates_noDaysPublished_isNullSoTheBarSimplyOmitsThem() {
        assertNull(formatEditionDates(emptyList()))
    }

    private fun day(
        start: String,
        end: String = start,
    ) = FestivalDay(
        id = start,
        name = "Jour",
        date = start.take(10),
        start = Instant.parse(start),
        end = Instant.parse(end),
        provenance = Provenance.CONFIRMED,
    )
}
