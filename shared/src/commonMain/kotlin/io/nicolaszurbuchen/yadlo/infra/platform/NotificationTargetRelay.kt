package io.nicolaszurbuchen.yadlo.infra.platform

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Where a notification tap lands before the shell has anywhere to put it.
 *
 * A tap arrives outside the composition and often outside the process: on Android in an `Intent`
 * handed to the launcher activity, on iOS in a delegate callback, either of which can happen before
 * a single composable exists. Both write here, and the shell reads it when it is ready.
 *
 * [consume] rather than a plain read, because a target is an event and not a state. Leaving it set
 * would send the visitor back to the same fiche on every recomposition, and again after a rotation.
 */
class NotificationTargetRelay {
    private val pending = MutableStateFlow<NotificationTarget?>(null)

    val target: StateFlow<NotificationTarget?> = pending.asStateFlow()

    fun post(target: NotificationTarget) {
        pending.value = target
    }

    fun consume() {
        pending.value = null
    }
}
