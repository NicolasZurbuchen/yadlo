package io.nicolaszurbuchen.yadlo.common.reminder.domain.usecase

import io.nicolaszurbuchen.yadlo.common.reminder.domain.repository.ReminderSettingsRepository
import kotlinx.coroutines.flow.Flow

class ObserveRemindersEnabledUseCase(
    private val settingsRepository: ReminderSettingsRepository,
) {
    operator fun invoke(): Flow<Boolean> = settingsRepository.observeEnabled()
}
