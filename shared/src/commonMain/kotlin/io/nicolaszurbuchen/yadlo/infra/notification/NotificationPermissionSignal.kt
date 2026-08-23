package io.nicolaszurbuchen.yadlo.infra.notification

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Emits whenever the app learns a new answer to the notification permission.
 *
 * **Permission is the one input to scheduling that cannot be observed.** The Plan is a database, the
 * content is a StateFlow, the visitor's own switch is a table — all three publish when they change,
 * and the shell reschedules off them. The operating system publishes nothing: it can only be asked,
 * and only at a moment somebody chose. This is that moment, made into something to react to.
 *
 * Nothing raises it by hand. [rememberNotificationPermissionRequester] wraps every request the app
 * can make and emits here as the answer comes back, so a call site cannot ask for the permission and
 * forget to act on getting it. That was the actual bug: the switch on *Plus › Notifications* granted
 * the permission and told nobody, and the reminders were scheduled only because the Android system
 * dialog happens to pause the activity underneath it. On iOS the alert is drawn in-app, nothing
 * pauses, and nothing was scheduled until the next foregrounding.
 *
 * Replay is zero on purpose: this is an event, not a state, and the collector — `ReminderEffects` —
 * is composed for as long as the tab shell exists, which is every moment a permission can be
 * requested.
 */
class NotificationPermissionSignal {
    private val answered =
        MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val answers: Flow<Unit> = answered.asSharedFlow()

    fun notifyAnswered() {
        answered.tryEmit(Unit)
    }
}
