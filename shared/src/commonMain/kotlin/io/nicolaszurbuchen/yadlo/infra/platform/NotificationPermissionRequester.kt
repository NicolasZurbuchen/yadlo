package io.nicolaszurbuchen.yadlo.infra.platform

import androidx.compose.runtime.Composable

/**
 * Asks for permission to post notifications, once, at the moment it can be justified.
 *
 * A `@Composable` factory rather than a method on [Notifier] because Android needs an Activity to
 * launch the permission contract from, and holding one in a Koin singleton is how an Activity leaks
 * — the same reasoning as [rememberShareLauncher], and the reason both live on this seam.
 *
 * **Where it is called from is the design decision, not how it works.** Asked on launch, this is a
 * prompt to let an app the visitor has not used yet interrupt them, and most people say no — which
 * also loses the reminders they would have wanted. Asked when the first Slot is hearted, it is a
 * prompt about the thing they just asked for. Denial is permanent on both platforms, so there is
 * one attempt to spend.
 */
fun interface NotificationPermissionRequester {
    fun request(onResult: (Boolean) -> Unit)
}

@Composable
expect fun rememberNotificationPermissionRequester(): NotificationPermissionRequester
