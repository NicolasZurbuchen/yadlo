package io.nicolaszurbuchen.yadlo.core.time

import kotlinx.datetime.TimeZone

/**
 * Every instant the app resolves to a wall-clock time resolves in the festival's own zone, never
 * in [TimeZone.currentSystemDefault].
 *
 * The festival is at Préverenges and its programme is written in local time. A phone that is
 * still on another zone — someone flying in, or a device whose automatic zone has not caught up —
 * must still agree on which FestivalDay a 01:30 set belongs to and on whether that set is running
 * now. Reading the device zone would make those answers depend on the phone rather than the
 * festival, and the discrepancy would surface exactly once, on site, where nobody can fix it.
 */
val FESTIVAL_TIME_ZONE: TimeZone = TimeZone.of("Europe/Zurich")
