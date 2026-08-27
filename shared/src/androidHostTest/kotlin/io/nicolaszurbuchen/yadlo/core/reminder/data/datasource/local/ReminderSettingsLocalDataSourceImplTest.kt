package io.nicolaszurbuchen.yadlo.core.reminder.data.datasource.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.turbine.test
import io.nicolaszurbuchen.yadlo.cache.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReminderSettingsLocalDataSourceImplTest {
    private lateinit var driver: SqlDriver
    private lateinit var dataSource: ReminderSettingsLocalDataSourceImpl

    @BeforeTest
    fun setup() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        val database = AppDatabase(driver = driver)
        dataSource = ReminderSettingsLocalDataSourceImpl(queries = database.reminderSettingQueries)
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun observeEnabled_beforeAnythingIsWritten_isNullRatherThanFalse() =
        runTest {
            // The distinction the repository turns into the default. A false here would read as the
            // visitor having turned reminders off, which nobody did.
            assertNull(dataSource.observeEnabled().first())
        }

    @Test
    fun setEnabled_writesBothWays() =
        runTest {
            dataSource.setEnabled(false)
            assertEquals(false, dataSource.observeEnabled().first())

            dataSource.setEnabled(true)
            assertEquals(true, dataSource.observeEnabled().first())
        }

    @Test
    fun setEnabled_twice_leavesOneRowRatherThanTwo() =
        runTest {
            // The constant primary key doing its job. A second row would make the read ambiguous and
            // mapToOneOrNull would start throwing rather than answering.
            dataSource.setEnabled(false)
            dataSource.setEnabled(true)

            assertTrue(dataSource.observeEnabled().first() == true)
        }

    @Test
    fun observeEnabled_emitsAgainWhenTheAnswerChangesAfterSubscription() =
        runTest {
            dataSource.observeEnabled().test {
                assertNull(awaitItem())

                dataSource.setEnabled(false)

                assertEquals(false, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
}
