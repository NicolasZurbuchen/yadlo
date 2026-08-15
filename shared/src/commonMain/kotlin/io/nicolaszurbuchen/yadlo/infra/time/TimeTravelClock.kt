package io.nicolaszurbuchen.yadlo.infra.time

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * The app's clock, with a debug-only override.
 *
 * Yadlo runs for three days a year and the next edition is eleven months out, so almost everything
 * on screen — the Phase, the countdown, which day the Programme opens on, every live pill and every
 * progress bar — is a function of an instant nobody can reach by waiting. The tests inject their
 * own clock; this is the same lever for a human holding a device.
 *
 * [enabled] is false in a release build and [simulateAt] is a no-op then, so the override cannot be
 * reached from a shipped app even if something calls it. What remains in release is one null check
 * per reading of the clock, which is the price of not having two clocks to keep in step.
 */
class TimeTravelClock(
    private val source: Clock,
    private val enabled: Boolean,
) : AppClock {
    private val simulatedInstant = MutableStateFlow<Instant?>(null)

    /** Null when the app is on wall time, which is always the case in release. */
    val simulated: StateFlow<Instant?> = simulatedInstant.asStateFlow()

    // Dropping the replayed current value matters: a collector subscribing at composition would
    // otherwise be told the clock had jumped the moment it started listening.
    override val jumps: Flow<Unit> = simulatedInstant.drop(1).map { }

    override fun now(): Instant = simulatedInstant.value ?: source.now()

    fun simulateAt(instant: Instant) {
        if (!enabled) return

        simulatedInstant.value = instant
    }

    /** Back to wall time. */
    fun resume() {
        if (!enabled) return

        simulatedInstant.value = null
    }
}
