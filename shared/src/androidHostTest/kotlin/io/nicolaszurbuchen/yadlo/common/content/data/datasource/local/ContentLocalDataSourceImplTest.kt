package io.nicolaszurbuchen.yadlo.common.content.data.datasource.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.nicolaszurbuchen.yadlo.cache.AppDatabase
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ContentLocalDataSourceImplTest {
    private lateinit var driver: SqlDriver
    private lateinit var dataSource: ContentLocalDataSourceImpl

    @BeforeTest
    fun setup() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        val database = AppDatabase(driver = driver)
        dataSource = ContentLocalDataSourceImpl(queries = database.cachedDocumentQueries)
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun write_thenRead_roundTripsTheBodyAndTheEtag() =
        runTest {
            dataSource.write(path = "festival.json", body = """{"schemaVersion":1}""", etag = "\"v1\"", fetchedAt = 1_000L)

            val cached = dataSource.read("festival.json")

            assertEquals("""{"schemaVersion":1}""", cached?.body)
            assertEquals("\"v1\"", cached?.etag)
            assertEquals(1_000L, cached?.fetched_at)
        }

    @Test
    fun read_unknownPath_returnsNull() =
        runTest {
            assertNull(dataSource.read("editions/1999/edition.json"))
        }

    @Test
    fun write_sameDocumentTwice_replacesItRatherThanAccumulating() =
        runTest {
            // The cache holds the current copy of each document, not its history. Without the
            // upsert this would fail the primary key on every single refresh.
            dataSource.write(path = "festival.json", body = "old", etag = "\"v1\"", fetchedAt = 1_000L)
            dataSource.write(path = "festival.json", body = "new", etag = "\"v2\"", fetchedAt = 2_000L)

            val cached = dataSource.read("festival.json")

            assertEquals("new", cached?.body)
            assertEquals("\"v2\"", cached?.etag)
        }

    @Test
    fun write_differentPaths_keepsThemApart() =
        runTest {
            dataSource.write(path = "festival.json", body = "festival", etag = null, fetchedAt = 1_000L)
            dataSource.write(path = "editions/2026/edition.json", body = "edition", etag = null, fetchedAt = 1_000L)

            assertEquals("festival", dataSource.read("festival.json")?.body)
            assertEquals("edition", dataSource.read("editions/2026/edition.json")?.body)
        }

    @Test
    fun write_withoutAnEtag_storesNull() =
        runTest {
            dataSource.write(path = "festival.json", body = "{}", etag = null, fetchedAt = 1_000L)

            assertNull(dataSource.read("festival.json")?.etag)
        }

    @Test
    fun clear_emptiesEveryDocument() =
        runTest {
            dataSource.write(path = "festival.json", body = "{}", etag = null, fetchedAt = 1_000L)
            dataSource.write(path = "announcements.json", body = "{}", etag = null, fetchedAt = 1_000L)

            dataSource.clear()

            assertNull(dataSource.read("festival.json"))
            assertNull(dataSource.read("announcements.json"))
        }
}
