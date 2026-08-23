package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.cleardata

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.SavedCount
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ClearDataReducerTest {
    @Test
    fun reduce_initially_knowsNeitherNumberAndIsNotAsking() {
        val state = ClearDataState()

        assertNull(state.savedCount)
        assertNull(state.imageCacheBytes)
        assertFalse(state.isAskingAboutSaved)
    }

    @Test
    fun reduce_savedUpdated_holdsIt() {
        val state = reduce(ClearDataState(), ClearDataMessage.SavedUpdated(SavedCount(slots = 7, stands = 2)))

        assertEquals(SavedCount(slots = 7, stands = 2), state.savedCount)
    }

    @Test
    fun reduce_savedUpdated_leavesTheCacheSizeAlone() {
        // The two numbers arrive from two different places and neither may reset the other, or the
        // screen would drop back into its skeleton every time a heart is tapped elsewhere.
        val state =
            reduce(
                ClearDataState(imageCacheBytes = 4_823_000L),
                ClearDataMessage.SavedUpdated(SavedCount(slots = 0, stands = 0)),
            )

        assertEquals(4_823_000L, state.imageCacheBytes)
    }

    @Test
    fun reduce_imageCacheSizeUpdated_holdsIt() {
        val state = reduce(ClearDataState(), ClearDataMessage.ImageCacheSizeUpdated(96_000L))

        assertEquals(96_000L, state.imageCacheBytes)
    }

    @Test
    fun reduce_imageCacheSizeUpdated_leavesTheSavedCountAlone() {
        val state =
            reduce(
                ClearDataState(savedCount = SavedCount(slots = 7, stands = 2)),
                ClearDataMessage.ImageCacheSizeUpdated(0L),
            )

        assertEquals(SavedCount(slots = 7, stands = 2), state.savedCount)
    }

    @Test
    fun reduce_zeroBytes_isAnAnswerRatherThanAnAbsence() {
        // Null and zero are two different screens: one is still loading, the other says there is
        // nothing to empty. The reducer must never turn the second into the first.
        val state = reduce(ClearDataState(imageCacheBytes = 96_000L), ClearDataMessage.ImageCacheSizeUpdated(0L))

        assertEquals(0L, state.imageCacheBytes)
    }

    @Test
    fun reduce_confirmationChanged_opensAndClosesTheQuestion() {
        val asking = reduce(ClearDataState(), ClearDataMessage.ConfirmationChanged(isAsking = true))

        assertTrue(asking.isAskingAboutSaved)
        assertFalse(reduce(asking, ClearDataMessage.ConfirmationChanged(isAsking = false)).isAskingAboutSaved)
    }

    @Test
    fun reduce_confirmationChanged_removesNothingByItself() {
        // The question is the only thing the message moves. Opening it must not be able to become a
        // deletion by a reducer that did too much.
        val state =
            reduce(
                ClearDataState(savedCount = SavedCount(slots = 7, stands = 2)),
                ClearDataMessage.ConfirmationChanged(isAsking = true),
            )

        assertEquals(SavedCount(slots = 7, stands = 2), state.savedCount)
    }

    private fun reduce(
        state: ClearDataState,
        message: ClearDataMessage,
    ): ClearDataState = with(ClearDataStoreFactory.ReducerImpl) { state.reduce(message) }
}
