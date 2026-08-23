package io.nicolaszurbuchen.yadlo.infra.notification

import androidx.compose.runtime.Composable

/**
 * Opens the operating system's own notification settings for this app.
 *
 * It exists because of the one state the switch on *Notifications* cannot get itself out of. Both
 * platforms treat a refused permission as final: asking again returns the old answer without
 * showing anything, so a visitor who said no once — or who turned Yadlo's notifications off in
 * system settings months ago — would tap the switch and watch it fall straight back to off, with
 * nothing on screen explaining why. The only way back is a screen this app does not own, and the
 * only decent thing to do is offer to open it.
 *
 * A `@Composable` factory for the same reason [rememberShareLauncher] is one: Android needs
 * something to start an activity from.
 */
fun interface SystemNotificationSettingsLauncher {
    fun open()
}

@Composable
expect fun rememberSystemNotificationSettingsLauncher(): SystemNotificationSettingsLauncher
