package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import io.nicolaszurbuchen.yadlo.common.content.domain.model.DietaryCoverage
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandDirectory
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandListing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StandsReducerTest {
    private val reducer = StandsStoreFactory.ReducerImpl

    @Test
    fun directoryUpdated_beforeAnyEmission_thereIsNoDirectoryAndNoFilterButThereIsAKind() {
        val state = StandsState(kind = StandsKindUiModel.MAKERS)

        // Which half this is arrives with the destination, not with the content, so the title is
        // known before anything has been read.
        assertNull(state.directory)
        assertTrue(state.selectedMarks.isEmpty())
        assertEquals(StandsKindUiModel.MAKERS, state.kind)
    }

    @Test
    fun directoryUpdated_firstEmission_holdsTheStands() {
        val result = with(reducer) { state().reduce(StandsMessage.DirectoryUpdated(directory())) }

        assertEquals(listOf("vegan-fabrik"), result.directory?.stands?.map { it.id })
    }

    @Test
    fun markToggled_aSecondMark_addsToTheFirstRatherThanReplacingIt() {
        val result = with(reducer) { state(setOf("vegan")).reduce(StandsMessage.MarkToggled("sans-gluten")) }

        // Someone who needs both needs both. See StandsState for why the union is the dangerous
        // reading of two chips and the intersection the safe one.
        assertEquals(setOf("vegan", "sans-gluten"), result.selectedMarks)
    }

    @Test
    fun markToggled_aMarkAlreadyOn_turnsItOff() {
        val result = with(reducer) { state(setOf("vegan", "sans-gluten")).reduce(StandsMessage.MarkToggled("vegan")) }

        assertEquals(setOf("sans-gluten"), result.selectedMarks)
    }

    @Test
    fun markToggled_null_isTheChipThatClearsTheLot() {
        // *Tout* clears rather than toggling, which is what makes it the way back to the whole list
        // from any combination.
        val result = with(reducer) { state(setOf("vegan", "sans-gluten")).reduce(StandsMessage.MarkToggled(null)) }

        assertTrue(result.selectedMarks.isEmpty())
    }

    @Test
    fun directoryUpdated_aRefreshLandsUnderTheReader_leavesTheirChipAndTheirHalfAlone() {
        val result =
            with(reducer) { state(setOf("vegan")).reduce(StandsMessage.DirectoryUpdated(directory())) }

        // Widening back to everything because content moved would silently undo something the
        // reader did, on the screen where they are least likely to notice.
        assertEquals(setOf("vegan"), result.selectedMarks)
        assertEquals(StandsKindUiModel.FOOD, result.kind)
    }

    private fun state(selectedMarks: Set<String> = emptySet()) =
        StandsState(kind = StandsKindUiModel.FOOD, directory = directory(), selectedMarks = selectedMarks)

    private fun directory() =
        StandDirectory(
            marks = listOf("vegan"),
            stands =
                listOf(
                    StandListing(
                        id = "vegan-fabrik",
                        name = "Vegan Fabrik",
                        offering = null,
                        dietary = mapOf("vegan" to DietaryCoverage.ALL),
                    ),
                ),
        )
}
