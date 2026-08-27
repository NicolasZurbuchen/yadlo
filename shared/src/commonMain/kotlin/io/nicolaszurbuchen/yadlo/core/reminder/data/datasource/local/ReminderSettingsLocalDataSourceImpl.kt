package io.nicolaszurbuchen.yadlo.core.reminder.data.datasource.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReminderSettingsLocalDataSourceImpl(
    private val queries: ReminderSettingQueries,
) : ReminderSettingsLocalDataSource {
    override fun observeEnabled(): Flow<Boolean?> =
        queries.selectEnabled()
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { stored -> stored?.let { it != FALSE } }

    override suspend fun setEnabled(enabled: Boolean) {
        queries.setEnabled(if (enabled) TRUE else FALSE)
    }

    private companion object {
        const val TRUE = 1L
        const val FALSE = 0L
    }
}
