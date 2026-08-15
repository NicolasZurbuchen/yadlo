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

    @Test
    fun formatAsTimeOfDay_padsBothTheHourAndTheMinute() {
        val instant = Instant.parse("2026-07-11T09:05:00+02:00")

        assertEquals("09:05", instant.formatAsTimeOfDay(ZURICH))
    }

    @Test
    fun formatAsTimeOfDay_afterMidnight_readsAsTheSmallHoursRatherThanRollingPast24() {
        // Friday's window runs to 02:00 on Saturday morning, so a set at 01:30 is one this screen
        // has to write out — and it writes it as 01:30, not 25:30.
        val lateSet = Instant.parse("2026-07-11T01:30:00+02:00")

        assertEquals("01:30", lateSet.formatAsTimeOfDay(ZURICH))
    }

    @Test
    fun formatAsTimeOfDay_resolvesInTheZoneItIsGiven_notTheDeviceOne() {
        val instant = Instant.parse("2026-07-11T20:00:00Z")

        assertEquals("22:00", instant.formatAsTimeOfDay(ZURICH))
        assertEquals("20:00", instant.formatAsTimeOfDay(TimeZone.UTC))
    }

    private companion object {
        val ZURICH = TimeZone.of("Europe/Zurich")
    }
}
