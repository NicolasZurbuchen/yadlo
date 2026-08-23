package io.nicolaszurbuchen.yadlo.common.reminder.data.repository

import io.nicolaszurbuchen.yadlo.common.reminder.data.datasource.local.ReminderSettingsLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReminderSettingsRepositoryImplTest {
    @Test
    fun observeEnabled_nothingEverWritten_isOn() =
        runTest {
            // The upgrade case, and the only reason this class is more than a passthrough: reminders
            // were already being scheduled for everybody who had granted the permission, so an
            // unwritten store has to mean "as before".
            assertTrue(repository(stored = null).observeEnabled().first())
        }

    @Test
    fun observeEnabled_writtenOff_isOff() =
        runTest {
            assertEquals(false, repository(stored = false).observeEnabled().first())
        }

    @Test
    fun observeEnabled_writtenOn_isOn() =
        runTest {
            assertTrue(repository(stored = true).observeEnabled().first())
        }

    @Test
    fun setEnabled_reachesTheStore() =
        runTest {
            val dataSource = RecordingDataSource(stored = null)

            ReminderSettingsRepositoryImpl(dataSource).setEnabled(false)

            assertEquals(listOf(false), dataSource.written)
        }

    @Test
    fun observeEnabled_emitsAgainWhenTheStoreChanges() =
        runTest {
            // The switch on Plus is not the only reader: the shell observes this to decide whether
            // to sync, so a write that did not re-emit would leave alarms scheduled after the
            // visitor turned them off.
            val dataSource = RecordingDataSource(stored = true)
            val repository = ReminderSettingsRepositoryImpl(dataSource)

            repository.setEnabled(false)

            assertEquals(false, repository.observeEnabled().first())
        }

    private fun repository(stored: Boolean?) = ReminderSettingsRepositoryImpl(RecordingDataSource(stored))

    private class RecordingDataSource(
        stored: Boolean?,
    ) : ReminderSettingsLocalDataSource {
        private val state = MutableStateFlow(stored)

        val written: MutableList<Boolean> = mutableListOf()

        override fun observeEnabled(): Flow<Boolean?> = state.asStateFlow()

        override suspend fun setEnabled(enabled: Boolean) {
            written += enabled
            state.value = enabled
        }
    }
}
