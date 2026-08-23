package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.notifications

/**
 * **The switch reads both halves and shows their conjunction**, which is the only honest position
 * for it: a switch sitting at on while the operating system drops everything the app posts is a
 * control that lies. Off, plus the line underneath saying why, is the truth.
 *
 * The blocked line appears only when the two disagree in that one direction — wanted here, refused
 * there. Somebody who turned the switch off themselves is not told their phone is blocking
 * anything, because it is not.
 */
fun NotificationsState.toUiModel(): NotificationsUiModel {
    val enabled = isEnabled
    val granted = isPermissionGranted

    if (enabled == null || granted == null) {
        return NotificationsUiModel(isLoading = true, isEnabled = false, isBlockedBySystem = false)
    }

    return NotificationsUiModel(
        isLoading = false,
        isEnabled = enabled && granted,
        isBlockedBySystem = enabled && !granted,
    )
}
