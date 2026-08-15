package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandDirectory
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandGroup
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandListing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StandsReducerTest {
    private val reducer = StandsStoreFactory.ReducerImpl

    @Test
    fun directoryUpdated_beforeAnyEmission_thereIsNoDirectoryAndNoFilter() {
        assertNull(StandsState().directory)
        assertNull(StandsState().selectedMark)
    }

    @Test
    fun directoryUpdated_firstEmission_holdsTheStands() {
        val result = with(reducer) { StandsState().reduce(StandsMessage.DirectoryUpdated(directory())) }

        assertEquals(listOf("restauration"), result.directory?.groups?.map { it.categoryId })
    }

    @Test
    fun markSelected_replacesTheChipRatherThanAddingToIt() {
        val state = StandsState(directory = directory(), selectedMark = "végan")

        val result = with(reducer) { state.reduce(StandsMessage.MarkSelected("végé")) }

        // One mark at a time: two chips read as an intersection to whoever wrote them and a union
        // to whoever reads them, and on eight stands the difference is one scroll.
        assertEquals("végé", result.selectedMark)
    }

    @Test
    fun markSelected_null_isTheChipThatClearsTheFilter() {
        val state = StandsState(directory = directory(), selectedMark = "végan")

        assertNull(with(reducer) { state.reduce(StandsMessage.MarkSelected(null)) }.selectedMark)
    }

    @Test
    fun directoryUpdated_aRefreshLandsUnderTheReader_leavesTheirChipAlone() {
        val state = StandsState(directory = directory(), selectedMark = "végan")

        val result = with(reducer) { state.reduce(StandsMessage.DirectoryUpdated(directory())) }

        // Widening back to everything because content moved would silently undo something the
        // reader did, on the screen where they are least likely to notice.
        assertEquals("végan", result.selectedMark)
    }

    private fun directory() =
        StandDirectory(
            marks = listOf("végan"),
            groups =
                listOf(
                    StandGroup(
                        categoryId = "restauration",
                        categoryName = "Restauration",
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
                    ),
                ),
        )
}
