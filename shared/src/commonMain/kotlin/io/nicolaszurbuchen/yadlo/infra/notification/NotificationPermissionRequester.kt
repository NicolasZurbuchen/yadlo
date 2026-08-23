package io.nicolaszurbuchen.yadlo.infra.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.compose.koinInject

/**
 * Asks for permission to post notifications, once, at the moment it can be justified.
 *
 * A `@Composable` factory rather than a method on [Notifier] because Android needs an Activity to
 * launch the permission contract from, and holding one in a Koin singleton is how an Activity leaks
 * — the same reasoning as `rememberShareLauncher` over in `infra/platform`, and the reason both are
 * composable factories rather than injected services.
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

/**
 * The platform's own prompt, wrapped so that **every answer is published whether or not the caller
 * cares about it**.
 *
 * Granting the permission changes what should be scheduled, and the thing that schedules is not the
 * thing that asked. Leaving each call site to remember that is how the switch on *Plus ›
 * Notifications* came to grant the permission and schedule nothing — rescued on Android only because
 * the system dialog pauses the activity underneath, and not rescued at all on iOS, where the alert
 * is drawn in-app. Publishing here makes it one rule that a new call site cannot fail to follow
 * rather than a step three of them have to repeat.
 */
@Composable
fun rememberNotificationPermissionRequester(): NotificationPermissionRequester {
    val platformRequester = rememberPlatformNotificationPermissionRequester()
    val signal = koinInject<NotificationPermissionSignal>()

    return remember(platformRequester, signal) {
        NotificationPermissionRequester { onResult ->
            platformRequester.request { granted ->
                signal.notifyAnswered()
                onResult(granted)
            }
        }
    }
}

@Composable
internal expect fun rememberPlatformNotificationPermissionRequester(): NotificationPermissionRequester
