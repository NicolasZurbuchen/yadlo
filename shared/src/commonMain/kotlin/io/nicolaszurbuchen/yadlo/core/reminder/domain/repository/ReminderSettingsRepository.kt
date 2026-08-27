package io.nicolaszurbuchen.yadlo.core.reminder.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Whether the visitor wants reminders — their answer, not the operating system's.
 *
 * **Two switches govern a notification and only one of them is here.** The other is the OS
 * permission, which the app can ask for and never set, and which the visitor can revoke from
 * outside the app entirely. This is the half Yadlo owns: somebody who granted the permission and
 * later decided the reminders were not for them has nowhere else to say so without turning off
 * every notification the app might ever send.
 *
 * **Defaults to true**, which is the whole reason this returns a Boolean rather than a nullable one.
 * Reminders were being scheduled for everybody who had granted the permission before the switch
 * existed, so a store that has never been written has to mean "as before" — anything else would
 * turn the feature off for its existing users on upgrade.
 */
interface ReminderSettingsRepository {
    fun observeEnabled(): Flow<Boolean>

    suspend fun setEnabled(enabled: Boolean)
}
