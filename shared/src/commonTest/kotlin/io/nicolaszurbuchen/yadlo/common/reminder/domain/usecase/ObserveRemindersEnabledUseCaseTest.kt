package io.nicolaszurbuchen.yadlo.common.reminder.domain.usecase

import io.nicolaszurbuchen.yadlo.common.reminder.domain.fake.FakeReminderSettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ObserveRemindersEnabledUseCaseTest {
    @Test
    fun invoke_readsTheStoredAnswer() =
        runTest {
            val repository = FakeReminderSettingsRepository()

            assertTrue(ObserveRemindersEnabledUseCase(repository).invoke().first())

            repository.emitEnabled(false)

            assertEquals(false, ObserveRemindersEnabledUseCase(repository).invoke().first())
        }
}
