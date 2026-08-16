package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandDirectory
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandListing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StandsReducerTest {
    private val reducer = StandsStoreFactory.ReducerImpl

    @Test
    fun directoryUpdated_beforeAnyEmission_thereIsNoDirectoryAndNoFilterButThereIsAKind() {
        val state = StandsState(kind = StandsKind.MAKERS)

        // Which half this is arrives with the destination, not with the content, so the title is
        // known before anything has been read.
        assertNull(state.directory)
        assertNull(state.selectedMark)
        assertEquals(StandsKind.MAKERS, state.kind)
    }

    @Test
    fun directoryUpdated_firstEmission_holdsTheStands() {
        val result = with(reducer) { state().reduce(StandsMessage.DirectoryUpdated(directory())) }

        assertEquals(listOf("vegan-fabrik"), result.directory?.stands?.map { it.id })
    }

    @Test
    fun markSelected_replacesTheChipRatherThanAddingToIt() {
        val result = with(reducer) { state(selectedMark = "végan").reduce(StandsMessage.MarkSelected("végé")) }

        // One mark at a time: two chips read as an intersection to whoever wrote them and a union
        // to whoever reads them, and on six stands the difference is one scroll.
        assertEquals("végé", result.selectedMark)
    }

    @Test
    fun markSelected_null_isTheChipThatClearsTheFilter() {
        assertNull(with(reducer) { state(selectedMark = "végan").reduce(StandsMessage.MarkSelected(null)) }.selectedMark)
    }

    @Test
    fun directoryUpdated_aRefreshLandsUnderTheReader_leavesTheirChipAndTheirHalfAlone() {
        val result =
            with(reducer) { state(selectedMark = "végan").reduce(StandsMessage.DirectoryUpdated(directory())) }

        // Widening back to everything because content moved would silently undo something the
        // reader did, on the screen where they are least likely to notice.
        assertEquals("végan", result.selectedMark)
        assertEquals(StandsKind.FOOD, result.kind)
    }

    private fun state(selectedMark: String? = null) =
        StandsState(kind = StandsKind.FOOD, directory = directory(), selectedMark = selectedMark)

    private fun directory() =
        StandDirectory(
            marks = listOf("végan"),
            stands =
                listOf(
                    StandListing(
                        id = "vegan-fabrik",
                        name = "Vegan Fabrik",
                        offering = null,
                        marks = listOf("végan"),
                        dietaryMatches = setOf("végan"),
                    ),
                ),
        )
}
