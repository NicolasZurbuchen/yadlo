package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.notifications

/**
 * *Notifications* — one switch, and the sentence that explains why it will not move.
 *
 * **One switch rather than a category per kind.** The app sends two sorts of notification — the
 * reminder before a saved Slot and the three that mark the festival year — and splitting them would
 * offer a choice nobody has: somebody who wants to be told a set is starting is not a different
 * person from somebody who wants to be told the festival is tomorrow. A settings screen earns a
 * second row when the two rows would be answered differently.
 *
 * [isBlockedBySystem] is the state that makes this screen more than a switch. The visitor wants
 * reminders and the operating system will not allow them, which the switch alone cannot say — it
 * would simply refuse to move.
 */
data class NotificationsUiModel(
    val isLoading: Boolean,
    val isEnabled: Boolean,
    val isBlockedBySystem: Boolean,
)
