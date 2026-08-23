package io.nicolaszurbuchen.yadlo.infra.time

import kotlin.time.Clock
import kotlin.time.Instant

/**
 * The clock the operating system's scheduler agrees with, and the only one a reminder may be
 * planned against.
 *
 * Everything on screen reads [AppClock], which the debug panel can move to any instant so a live
 * pill or a countdown can be checked in August. **A reminder cannot follow it.** `AlarmManager` and
 * `UNUserNotificationCenter` take an absolute instant and compare it to wall time, so a reminder
 * planned from a simulated Saturday evening would be handed a July instant and fire in eleven
 * months — or, once the simulated date is behind the real one, never.
 *
 * This is therefore a second *name* for [Clock.System] rather than a second clock, and the naming is
 * the whole point: asking for [Clock] gets the one that time-travels, asking for this one gets the
 * one that does not, and neither call site can get the other by accident. It is also why the rule
 * against reaching for [Clock.System] at a use site still holds — the reach happens once, in
 * [timeModule].
 *
 * What it costs is that reminders cannot be checked by moving [TimeTravelClock]; they are checked by
 * moving the device's own clock, which makes both clocks agree again. See `ReminderScheduler`.
 */
class WallClock(
    private val source: Clock,
) : Clock {
    override fun now(): Instant = source.now()
}
