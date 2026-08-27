package io.nicolaszurbuchen.yadlo.core.plan.data.repository

import app.cash.turbine.test
import io.nicolaszurbuchen.yadlo.core.plan.data.datasource.local.PlanLocalDataSource
import io.nicolaszurbuchen.yadlo.core.plan.data.datasource.local.SavedEntry
import io.nicolaszurbuchen.yadlo.core.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.core.plan.domain.model.SavedKind
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlanRepositoryImplTest {
    private val local = StubPlanLocal()
    private val repository = PlanRepositoryImpl(local)

    @Test
    fun observeSaved_readsBothKindsBackOutOfTheOneTable() =
        runTest {
            local.emit(
                listOf(
                    SavedEntry(id = "2026:dj-alf-fri", kind = "SLOT", edition_id = "2026"),
                    SavedEntry(id = "vegan-fabrik", kind = "STAND", edition_id = "2026"),
                ),
            )

            val result = repository.observeSaved().first()

            assertEquals(
                listOf(
                    SavedItem(id = "2026:dj-alf-fri", kind = SavedKind.SLOT, editionId = "2026"),
                    SavedItem(id = "vegan-fabrik", kind = SavedKind.STAND, editionId = "2026"),
                ),
                result,
            )
        }

    @Test
    fun observeSaved_aKindThisBuildDoesNotKnow_isLeftOutRatherThanThrown() =
        runTest {
            // Local storage outlives the version that wrote it. A row from a later build is a
            // reason to show one fewer heart, not to fail every screen that recalls anything.
            local.emit(
                listOf(
                    SavedEntry(id = "2026:dj-alf-fri", kind = "SLOT", edition_id = "2026"),
                    SavedEntry(id = "bus-701-morges", kind = "DEPARTURE", edition_id = "2026"),
                ),
            )

            val result = repository.observeSaved().first()

            assertEquals(listOf("2026:dj-alf-fri"), result.map { it.id })
        }

    @Test
    fun observeSaved_whenNothingIsSaved_emitsAnEmptyListRatherThanWaiting() =
        runTest {
            assertTrue(repository.observeSaved().first().isEmpty())
        }

    @Test
    fun observeSaved_followsTheStorageAfterSubscription() =
        runTest {
            repository.observeSaved().test {
                assertTrue(awaitItem().isEmpty())

                local.emit(listOf(SavedEntry(id = "vegan-fabrik", kind = "STAND", edition_id = "2026")))

                assertEquals(listOf("vegan-fabrik"), awaitItem().map { it.id })
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun toggle_handsTheKindDownAsTheNameTheTableStores() =
        runTest {
            repository.toggle(SavedItem(id = "vegan-fabrik", kind = SavedKind.STAND, editionId = "2026"))

            assertEquals(listOf(Triple("vegan-fabrik", "STAND", "2026")), local.toggled)
        }

    @Test
    fun clear_goesStraightToTheTableRatherThanRemovingWhatItCanRead() =
        runTest {
            // The read drops a row whose kind this build has no bucket for, so a clear built out of
            // reads would leave behind exactly the rows the visitor was never shown. `deleteAll` is
            // one statement over the table, and that is the point of it.
            repository.clear()

            assertEquals(1, local.deletedAll)
        }
}

private class StubPlanLocal : PlanLocalDataSource {
    private val rows = MutableStateFlow<List<SavedEntry>>(emptyList())

    val toggled: MutableList<Triple<String, String, String>> = mutableListOf()

    var deletedAll: Int = 0
        private set

    override fun observeAll(): Flow<List<SavedEntry>> = rows.asStateFlow()

    override suspend fun toggle(
        id: String,
        kind: String,
        editionId: String,
    ) {
        toggled += Triple(id, kind, editionId)
    }

    override suspend fun deleteAll() {
        deletedAll++
        rows.value = emptyList()
    }

    fun emit(value: List<SavedEntry>) {
        rows.value = value
    }
}
