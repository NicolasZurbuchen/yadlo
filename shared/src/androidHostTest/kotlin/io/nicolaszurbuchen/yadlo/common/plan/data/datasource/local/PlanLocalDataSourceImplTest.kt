package io.nicolaszurbuchen.yadlo.common.plan.data.datasource.local

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
import kotlin.test.assertTrue

class PlanLocalDataSourceImplTest {
    private lateinit var driver: SqlDriver
    private lateinit var dataSource: PlanLocalDataSourceImpl

    @BeforeTest
    fun setup() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        val database = AppDatabase(driver = driver)
        dataSource = PlanLocalDataSourceImpl(queries = database.savedEntryQueries)
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun toggle_onSomethingUnsaved_writesTheRowWithBothTheKindAndTheEdition() =
        runTest {
            dataSource.toggle(id = "2026:dj-alf-fri", kind = "SLOT", editionId = "2026")

            val rows = dataSource.observeAll().first()

            assertEquals(1, rows.size)
            assertEquals("2026:dj-alf-fri", rows.single().id)
            assertEquals("SLOT", rows.single().kind)
            assertEquals("2026", rows.single().edition_id)
        }

    @Test
    fun toggle_onSomethingSaved_removesIt() =
        runTest {
            dataSource.toggle(id = "vegan-fabrik", kind = "STAND", editionId = "2026")

            dataSource.toggle(id = "vegan-fabrik", kind = "STAND", editionId = "2026")

            assertTrue(dataSource.observeAll().first().isEmpty())
        }

    @Test
    fun toggle_removesOnlyTheRowItNames() =
        runTest {
            dataSource.toggle(id = "2026:dj-alf-fri", kind = "SLOT", editionId = "2026")
            dataSource.toggle(id = "2026:caesure-fri", kind = "SLOT", editionId = "2026")

            dataSource.toggle(id = "2026:dj-alf-fri", kind = "SLOT", editionId = "2026")

            assertEquals(listOf("2026:caesure-fri"), dataSource.observeAll().first().map { it.id })
        }

    @Test
    fun toggle_theSameIdUnderTwoEditions_isOneRowBecauseTheIdIsTheKey() =
        runTest {
            // Only reachable for a Stand, whose id carries no year. The second tap is a toggle of
            // the first row rather than a second heart, which is what the primary key is for.
            dataSource.toggle(id = "vegan-fabrik", kind = "STAND", editionId = "2026")
            dataSource.toggle(id = "vegan-fabrik", kind = "STAND", editionId = "2027")

            assertTrue(dataSource.observeAll().first().isEmpty())
        }

    @Test
    fun deleteAll_removesBothKindsAtOnce() =
        runTest {
            dataSource.toggle(id = "2026:dj-alf-fri", kind = "SLOT", editionId = "2026")
            dataSource.toggle(id = "vegan-fabrik", kind = "STAND", editionId = "2026")

            dataSource.deleteAll()

            assertTrue(dataSource.observeAll().first().isEmpty())
        }

    @Test
    fun deleteAll_takesEveryEditionRatherThanTheCurrentOne() =
        runTest {
            // *Effacer mes données* is not the Plan lifecycle sweep. It answers "forget what I
            // kept", which does not mean "forget this year's".
            dataSource.toggle(id = "2025:old-set", kind = "SLOT", editionId = "2025")
            dataSource.toggle(id = "2026:dj-alf-fri", kind = "SLOT", editionId = "2026")

            dataSource.deleteAll()

            assertTrue(dataSource.observeAll().first().isEmpty())
        }

    @Test
    fun deleteAll_onAnEmptyTable_isAQuietNoOp() =
        runTest {
            // The screen disables the button at zero, but the store is reachable from a restored
            // state and a delete of nothing must not be an error.
            dataSource.deleteAll()

            assertTrue(dataSource.observeAll().first().isEmpty())
        }

    @Test
    fun deleteAll_publishesToAnExistingSubscriber() =
        runTest {
            // What makes the count on the screen fall to zero without the screen asking again.
            dataSource.toggle(id = "vegan-fabrik", kind = "STAND", editionId = "2026")

            dataSource.observeAll().test {
                assertEquals(listOf("vegan-fabrik"), awaitItem().map { it.id })

                dataSource.deleteAll()

                assertTrue(awaitItem().isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun observeAll_whenNothingIsSaved_emitsEmptyRatherThanNeverEmitting() =
        runTest {
            assertEquals(emptyList(), dataSource.observeAll().first())
        }

    @Test
    fun observeAll_emitsAgainWhenSomethingIsSavedAfterSubscription() =
        runTest {
            dataSource.observeAll().test {
                assertEquals(emptyList(), awaitItem())

                dataSource.toggle(id = "vegan-fabrik", kind = "STAND", editionId = "2026")

                assertEquals(listOf("vegan-fabrik"), awaitItem().map { it.id })
                cancelAndIgnoreRemainingEvents()
            }
        }
}
