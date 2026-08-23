package io.nicolaszurbuchen.yadlo.common.reminder.domain.usecase

import io.nicolaszurbuchen.yadlo.common.reminder.domain.repository.ReminderSettingsRepository

class SetRemindersEnabledUseCase(
    private val settingsRepository: ReminderSettingsRepository,
) {
    suspend operator fun invoke(enabled: Boolean) {
        settingsRepository.setEnabled(enabled)
    }
}
