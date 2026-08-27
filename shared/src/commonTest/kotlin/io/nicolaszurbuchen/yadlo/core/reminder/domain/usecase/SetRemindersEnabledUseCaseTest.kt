package io.nicolaszurbuchen.yadlo.core.reminder.domain.usecase

import io.nicolaszurbuchen.yadlo.core.reminder.domain.fake.FakeReminderSettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SetRemindersEnabledUseCaseTest {
    @Test
    fun invoke_writesBothWays() =
        runTest {
            val repository = FakeReminderSettingsRepository()
            val setEnabled = SetRemindersEnabledUseCase(repository)

            setEnabled(false)
            assertEquals(false, repository.observeEnabled().first())

            setEnabled(true)
            assertTrue(repository.observeEnabled().first())
        }
}
