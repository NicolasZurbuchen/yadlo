package io.nicolaszurbuchen.yadlo.core.reminder.data.datasource.local

import kotlinx.coroutines.flow.Flow

interface ReminderSettingsLocalDataSource {
    /**
     * Null when nothing has ever been written, which the repository turns into the default. Kept
     * nullable this far down because "never asked" and "asked and said no" are genuinely different
     * rows and only one layer should be deciding what the first one means.
     */
    fun observeEnabled(): Flow<Boolean?>

    suspend fun setEnabled(enabled: Boolean)
}
