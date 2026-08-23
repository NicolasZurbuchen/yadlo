package io.nicolaszurbuchen.yadlo.common.reminder.domain.fake

import io.nicolaszurbuchen.yadlo.common.reminder.domain.repository.ReminderSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Starts on, which is what the real store means by never having been written.
 *
 * [setEnabled] really writes rather than recording the call and standing still, so a test can turn
 * the switch off and read what everything downstream then sees.
 */
class FakeReminderSettingsRepository : ReminderSettingsRepository {
    private val enabled = MutableStateFlow(true)

    override fun observeEnabled(): Flow<Boolean> = enabled.asStateFlow()

    override suspend fun setEnabled(enabled: Boolean) {
        this.enabled.value = enabled
    }

    fun emitEnabled(value: Boolean) {
        enabled.value = value
    }
}
