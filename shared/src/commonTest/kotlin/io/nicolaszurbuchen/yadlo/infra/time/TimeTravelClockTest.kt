package io.nicolaszurbuchen.yadlo.infra.time

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Instant

private class FixedClock(
    private val instant: Instant,
) : Clock {
    override fun now(): Instant = instant
}

class TimeTravelClockTest {
    @Test
    fun now_untouched_readsTheClockUnderneath() {
        val clock = TimeTravelClock(source = FixedClock(REAL_NOW), enabled = true)

        assertEquals(REAL_NOW, clock.now())
        assertNull(clock.simulated.value)
    }

    @Test
    fun simulateAt_debugBuild_movesEveryLaterReading() {
        val clock = TimeTravelClock(source = FixedClock(REAL_NOW), enabled = true)

        clock.simulateAt(SATURDAY_AFTERNOON)

        assertEquals(SATURDAY_AFTERNOON, clock.now())
        assertEquals(SATURDAY_AFTERNOON, clock.simulated.value)
    }

    @Test
    fun resume_afterSimulating_goesBackToWallTime() {
        val clock = TimeTravelClock(source = FixedClock(REAL_NOW), enabled = true)
        clock.simulateAt(SATURDAY_AFTERNOON)

        clock.resume()

        assertEquals(REAL_NOW, clock.now())
        assertNull(clock.simulated.value)
    }

    @Test
    fun simulateAt_releaseBuild_isIgnored() {
        // The debug panel never draws in release, but the clock refuses independently: two reasons
        // rather than one for something that would silently misdate a whole festival weekend.
        val clock = TimeTravelClock(source = FixedClock(REAL_NOW), enabled = false)

        clock.simulateAt(SATURDAY_AFTERNOON)

        assertEquals(REAL_NOW, clock.now())
        assertNull(clock.simulated.value)
    }

    @Test
    fun jumps_emitsOnEachMoveButNotOnSubscribing() =
        runTest {
            val clock = TimeTravelClock(source = FixedClock(REAL_NOW), enabled = true)

            clock.jumps.test {
                // A collector subscribing at composition must not be told the clock has moved.
                expectNoEvents()

                clock.simulateAt(SATURDAY_AFTERNOON)
                awaitItem()

                clock.resume()
                awaitItem()

                cancel()
            }
        }

    @Test
    fun jumps_releaseBuild_neverEmits() =
        runTest {
            val clock = TimeTravelClock(source = FixedClock(REAL_NOW), enabled = false)

            clock.jumps.test {
                clock.simulateAt(SATURDAY_AFTERNOON)

                // Which is what makes the collectors in the stores free rather than merely cheap.
                expectNoEvents()
                cancel()
            }
        }

    private companion object {
        val REAL_NOW = Instant.parse("2026-08-15T12:00:00+02:00")
        val SATURDAY_AFTERNOON = Instant.parse("2026-07-11T15:45:00+02:00")
    }
}
