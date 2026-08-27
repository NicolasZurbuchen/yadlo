package io.nicolaszurbuchen.yadlo.core.reminder.data.repository

import io.nicolaszurbuchen.yadlo.core.reminder.data.datasource.local.ReminderSettingsLocalDataSource
import io.nicolaszurbuchen.yadlo.core.reminder.domain.repository.ReminderSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReminderSettingsRepositoryImpl(
    private val localDataSource: ReminderSettingsLocalDataSource,
) : ReminderSettingsRepository {
    /**
     * The one line in this class that is a decision rather than plumbing: an unwritten store means
     * on. See [ReminderSettingsRepository] for why it cannot mean off.
     */
    override fun observeEnabled(): Flow<Boolean> = localDataSource.observeEnabled().map { it ?: DEFAULT_ENABLED }

    override suspend fun setEnabled(enabled: Boolean) {
        localDataSource.setEnabled(enabled)
    }

    private companion object {
        const val DEFAULT_ENABLED = true
    }
}
