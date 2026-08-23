package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import app.cash.turbine.test
import io.nicolaszurbuchen.yadlo.common.plan.domain.fake.FakePlanRepository
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedKind
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.SavedCount
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObserveSavedCountUseCaseTest {
    private val repository = FakePlanRepository()
    private val useCase = ObserveSavedCountUseCase(repository)

    @Test
    fun invoke_countsTheTwoKindsSeparately() =
        runTest {
            repository.emitSaved(
                listOf(
                    slot("2026:dj-alf-fri"),
                    slot("2026:caesure-fri"),
                    stand("vegan-fabrik"),
                ),
            )

            assertEquals(SavedCount(slots = 2, stands = 1), useCase().first())
        }

    @Test
    fun invoke_nothingSaved_isZeroAndZeroRatherThanNoEmission() =
        runTest {
            // The screen draws a sentence for this case, so it has to arrive as an answer rather
            // than as a flow that has not spoken yet.
            assertEquals(SavedCount(slots = 0, stands = 0), useCase().first())
        }

    @Test
    fun invoke_oneKindOnly_leavesTheOtherAtZero() =
        runTest {
            repository.emitSaved(listOf(stand("vegan-fabrik")))

            assertEquals(SavedCount(slots = 0, stands = 1), useCase().first())
        }

    @Test
    fun invoke_followsThePlanAfterSubscription() =
        runTest {
            // What makes the line fall to *rien d'enregistré* on the screen the button was pressed
            // on, rather than on the next visit to it.
            useCase().test {
                assertEquals(SavedCount(slots = 0, stands = 0), awaitItem())

                repository.emitSaved(listOf(slot("2026:dj-alf-fri")))

                assertEquals(SavedCount(slots = 1, stands = 0), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun isEmpty_isTrueOnlyWhenBothHalvesAre() =
        runTest {
            assertTrue(SavedCount(slots = 0, stands = 0).isEmpty)
            assertFalse(SavedCount(slots = 0, stands = 1).isEmpty)
            assertFalse(SavedCount(slots = 1, stands = 0).isEmpty)
        }

    private fun slot(id: String) = SavedItem(id = id, kind = SavedKind.SLOT, editionId = "2026")

    private fun stand(id: String) = SavedItem(id = id, kind = SavedKind.STAND, editionId = "2026")
}
