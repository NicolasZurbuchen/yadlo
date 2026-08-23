package io.nicolaszurbuchen.yadlo.infra.notification

/**
 * Whether the operating system currently allows this app to post notifications.
 *
 * A separate, read-only capability rather than handing [Notifier] itself to whoever needs the
 * answer. The switch on *Plus › Notifications* has to know it and has no business being able to
 * cancel every alarm on the device, which is what the full seam would let it do.
 *
 * It is also the half that is testable. [Notifier] is an `expect class` — the right shape for
 * something whose Android constructor takes a `Context` and whose iOS one takes nothing, and the
 * wrong shape for anything a common test needs to stand in for. A `fun interface` here is a lambda
 * in a test and a one-line delegation in the module.
 */
fun interface NotificationPermissionStatus {
    suspend fun isGranted(): Boolean
}
