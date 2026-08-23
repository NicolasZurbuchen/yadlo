package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.notifications

sealed interface NotificationsIntent {
    data class EnabledChanged(
        val enabled: Boolean,
    ) : NotificationsIntent

    /** The answer to a prompt the Route launched, on its way back in. */
    data class PermissionAnswered(
        val granted: Boolean,
    ) : NotificationsIntent

    /** On every resume: the visitor may have changed the answer in system settings and come back. */
    data object PermissionRechecked : NotificationsIntent

    data object SystemSettingsClicked : NotificationsIntent
}

sealed interface NotificationsLabel {
    /**
     * Asking is the Route's job, not the store's: the prompt needs an Activity on Android, and the
     * store has no business holding one.
     */
    data object RequestPermission : NotificationsLabel

    data object OpenSystemSettings : NotificationsLabel
}

sealed interface NotificationsAction {
    data object ObserveSettings : NotificationsAction

    data object CheckPermission : NotificationsAction
}

sealed interface NotificationsMessage {
    data class EnabledUpdated(
        val enabled: Boolean,
    ) : NotificationsMessage

    data class PermissionUpdated(
        val granted: Boolean,
    ) : NotificationsMessage
}

/**
 * Both halves of the answer, and neither may be assumed from the other.
 *
 * [isEnabled] is what the visitor said in this app; [isPermissionGranted] is what the operating
 * system allows, which can change while the app is in the background and can never be set from
 * here. Null in either means not yet known, which is what keeps the switch from drawing itself in
 * the wrong position for a frame and then flicking.
 */
data class NotificationsState(
    val isEnabled: Boolean? = null,
    val isPermissionGranted: Boolean? = null,
)
