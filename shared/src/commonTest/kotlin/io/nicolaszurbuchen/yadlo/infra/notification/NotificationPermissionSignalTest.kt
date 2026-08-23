package io.nicolaszurbuchen.yadlo.infra.notification

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class NotificationPermissionSignalTest {
    @Test
    fun notifyAnswered_reachesACollector() =
        runTest {
            val signal = NotificationPermissionSignal()

            signal.answers.test {
                signal.notifyAnswered()

                awaitItem()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun notifyAnswered_withNobodyListening_doesNotBlockOrThrow() =
        runTest {
            // The permission callback arrives on whatever thread the platform chose and cannot be
            // made to wait for a collector. Dropping is the right failure: the answer is an event,
            // and the only collector is composed for as long as the tab shell is.
            val signal = NotificationPermissionSignal()

            signal.notifyAnswered()
            signal.notifyAnswered()
        }

    @Test
    fun notifyAnswered_twice_isDeliveredTwice() =
        runTest {
            // Refusing and then allowing from system settings is two answers, and the second one is
            // the one that schedules anything.
            val signal = NotificationPermissionSignal()

            signal.answers.test {
                signal.notifyAnswered()
                awaitItem()

                signal.notifyAnswered()
                awaitItem()

                cancelAndIgnoreRemainingEvents()
            }
        }
}
