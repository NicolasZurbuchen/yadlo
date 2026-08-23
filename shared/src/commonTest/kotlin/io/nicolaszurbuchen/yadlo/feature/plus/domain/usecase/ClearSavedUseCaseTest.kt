package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.plan.domain.fake.FakePlanRepository
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedKind
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClearSavedUseCaseTest {
    private val repository = FakePlanRepository()
    private val useCase = ClearSavedUseCase(repository)

    @Test
    fun invoke_removesBothKinds() =
        runTest {
            repository.emitSaved(
                listOf(
                    SavedItem(id = "2026:dj-alf-fri", kind = SavedKind.SLOT, editionId = "2026"),
                    SavedItem(id = "vegan-fabrik", kind = SavedKind.STAND, editionId = "2026"),
                ),
            )

            useCase()

            assertTrue(repository.observeSaved().first().isEmpty())
        }

    @Test
    fun invoke_asksTheRepositoryToClearRatherThanUntogglingEachRow() =
        runTest {
            // Toggling every id back off would be the same result by a path that reads what is
            // there first — and a row of a kind this build cannot read would survive it.
            repository.emitSaved(listOf(SavedItem(id = "vegan-fabrik", kind = SavedKind.STAND, editionId = "2026")))

            useCase()

            assertEquals(1, repository.cleared)
            assertTrue(repository.toggled.isEmpty())
        }

    @Test
    fun invoke_withNothingSaved_isAQuietNoOp() =
        runTest {
            useCase()

            assertEquals(1, repository.cleared)
            assertTrue(repository.observeSaved().first().isEmpty())
        }
}
