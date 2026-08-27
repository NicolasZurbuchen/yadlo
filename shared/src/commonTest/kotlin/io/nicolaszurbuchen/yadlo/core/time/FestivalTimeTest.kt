package io.nicolaszurbuchen.yadlo.core.time

import kotlinx.datetime.UtcOffset
import kotlinx.datetime.offsetAt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class FestivalTimeTest {
    @Test
    fun festivalTimeZone_resolves_toEuropeZurich() {
        assertEquals("Europe/Zurich", FESTIVAL_TIME_ZONE.id)
    }

    @Test
    fun festivalTimeZone_duringTheFestival_isTwoHoursAheadOfUtc() {
        // Saturday 11 July 2026, 14:00 in Préverenges. This is the offset every instant in the
        // content is written with, so a mismatch here means every published time is an hour out.
        val duringFestival = Instant.parse("2026-07-11T12:00:00Z")

        assertEquals(UtcOffset(hours = 2), FESTIVAL_TIME_ZONE.offsetAt(duringFestival))
    }

    @Test
    fun festivalTimeZone_inWinter_isOneHourAheadOfUtc() {
        // Deliberately outside the festival: a hardcoded +02:00 would satisfy the test above and
        // fail this one, so this is what proves the zone carries real daylight-saving rules
        // rather than a fixed offset that happens to be right in July.
        val midWinter = Instant.parse("2026-01-15T12:00:00Z")

        assertEquals(UtcOffset(hours = 1), FESTIVAL_TIME_ZONE.offsetAt(midWinter))
    }
}
