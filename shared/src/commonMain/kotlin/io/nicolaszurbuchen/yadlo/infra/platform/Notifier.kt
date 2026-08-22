package io.nicolaszurbuchen.yadlo.infra.platform

/**
 * The platform's notification scheduler, reduced to the two things this app does with one.
 *
 * **[replaceScheduled] replaces rather than reconciles**, which is the decision that makes the rest
 * of the feature small. Asking each platform what it currently holds is possible and awkward — iOS
 * answers asynchronously, Android does not answer at all — and every caller would then need diffing
 * logic that has to be right about cancellation. Cancelling everything and rescheduling the desired
 * set is correct by construction, and at the scale involved (tens of items, on app start) the cost
 * is not measurable. Unhearting a Slot, a Slot the content dropped and a reminder whose moment has
 * passed then stop being three cases and become one: absent from the list.
 *
 * Requesting permission is deliberately *not* here — it needs an Activity on Android, so it lives on
 * a composable seam beside [rememberShareLauncher] for the same reason that one does.
 */
expect class Notifier {
    /**
     * Whether notifications may be posted at all. False is ordinary rather than exceptional: the
     * visitor may not have been asked yet, or may have said no, and scheduling into a revoked
     * permission would be work that silently does nothing.
     */
    suspend fun isPermissionGranted(): Boolean

    /**
     * Cancels everything this app has scheduled, schedules [notifications] in its place, and clears
     * anything already delivered that [ScheduledNotification.staleAfter] says is past.
     */
    suspend fun replaceScheduled(notifications: List<ScheduledNotification>)
}
