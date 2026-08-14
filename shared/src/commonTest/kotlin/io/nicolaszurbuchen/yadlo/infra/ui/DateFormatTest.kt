package io.nicolaszurbuchen.yadlo.infra.ui

import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class DateFormatTest {
    @Test
    fun formatAsShortDate_padsBothTheDayAndTheMonth() {
        val instant = Instant.parse("2026-06-02T12:00:00+02:00")

        assertEquals("02.06.2026", instant.formatAsShortDate(ZURICH))
    }

    @Test
    fun formatAsShortDate_resolvesInTheZoneItIsGiven_notTheDeviceOne() {
        // 23:30 UTC on the first is 01:30 the next morning in Zurich. A visitor whose phone is
        // still on another zone must read the same date as everyone else on the beach.
        val lateNight = Instant.parse("2026-07-01T23:30:00Z")

        assertEquals("02.07.2026", lateNight.formatAsShortDate(ZURICH))
        assertEquals("01.07.2026", lateNight.formatAsShortDate(TimeZone.UTC))
    }

    private companion object {
        val ZURICH = TimeZone.of("Europe/Zurich")
    }
}
