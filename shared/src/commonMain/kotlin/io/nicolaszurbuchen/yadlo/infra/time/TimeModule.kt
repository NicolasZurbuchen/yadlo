package io.nicolaszurbuchen.yadlo.infra.time

import io.nicolaszurbuchen.yadlo.infra.platform.BuildFlags
import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * The clock is a dependency, never a call to [Clock.System] at a use site.
 *
 * The festival runs three days a year and the next one is eleven months out, so "now" is the
 * single input that decides what almost every screen shows: which FestivalDay is current, which
 * Phase a saved Slot is in, whether a live pill reads *en cours*. None of that is testable
 * against a real clock outside of one weekend in July. Injecting it means a test can sit at
 * 23:45 on the Friday and assert what the user would see.
 *
 * The three bindings are one object under three names. Whoever only reads the time asks for
 * [Clock]; the stores that recompute on a ticker ask for [AppClock] so a debug-time jump wakes
 * them; the debug panel asks for [TimeTravelClock] because it is the only thing allowed to move it.
 *
 * [WallClock] is the deliberate fourth, and the only binding here that is not that same object:
 * whatever schedules against the OS has to read time the OS agrees with, so it cannot be given a
 * clock a debug panel can move. Its own KDoc has the reasoning.
 */
@OptIn(ExperimentalTime::class)
val timeModule =
    module {
        single { TimeTravelClock(source = Clock.System, enabled = get<BuildFlags>().isDebug) }
        single<AppClock> { get<TimeTravelClock>() }
        single<Clock> { get<AppClock>() }

        single { WallClock(source = Clock.System) }
    }
