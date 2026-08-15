package io.nicolaszurbuchen.yadlo.infra.time

import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock

/**
 * The clock the app reads, plus a signal for when its reading moves for a reason other than time
 * passing.
 *
 * Everything that recomputes against the clock does it on a ticker measured in minutes, which is
 * the right resolution for a festival and the wrong one for a debug panel: a screen that waits out
 * its interval before noticing you jumped to the Saturday evening is a control you cannot use to
 * check anything. [jumps] is what lets those tickers wake immediately instead.
 *
 * Nothing emits on it in a release build — see [TimeTravelClock] — so the collectors are a
 * subscription that never fires, and the domain still sees a plain [Clock].
 */
interface AppClock : Clock {
    val jumps: Flow<Unit>
}
