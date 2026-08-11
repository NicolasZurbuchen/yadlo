package io.nicolaszurbuchen.yadlo.infra.time

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
 */
@OptIn(ExperimentalTime::class)
val timeModule =
    module {
        single<Clock> { Clock.System }
    }
