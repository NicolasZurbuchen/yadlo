package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.cleardata

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.SavedCount
import io.nicolaszurbuchen.yadlo.infra.text.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.clear_data_images_empty
import yadlo.shared.generated.resources.clear_data_images_kilobytes
import yadlo.shared.generated.resources.clear_data_images_megabytes
import yadlo.shared.generated.resources.clear_data_saved_empty
import yadlo.shared.generated.resources.clear_data_saved_slots_one
import yadlo.shared.generated.resources.clear_data_saved_slots_other
import yadlo.shared.generated.resources.clear_data_saved_stands_one
import yadlo.shared.generated.resources.clear_data_saved_stands_other
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ClearDataUiMapperTest {
    // region loading

    @Test
    fun toUiModel_neitherNumberRead_isLoading() {
        assertTrue(ClearDataState().toUiModel().isLoading)
    }

    @Test
    fun toUiModel_onlyOneNumberRead_isStillLoading() {
        // The screen is one card of two rows. Filling in half of it while the other half shimmers
        // reads as the second one being empty, which is the wrong answer to look at.
        assertTrue(ClearDataState(savedCount = SavedCount(1, 1)).toUiModel().isLoading)
        assertTrue(ClearDataState(imageCacheBytes = 0L).toUiModel().isLoading)
    }

    @Test
    fun toUiModel_bothNumbersReadAsZero_isNotLoading() {
        // Zero is an answer. A fresh install must reach the finished screen, not sit in a skeleton.
        assertFalse(state(slots = 0, stands = 0, bytes = 0L).toUiModel().isLoading)
    }

    // endregion

    // region what has been kept

    @Test
    fun toUiModel_bothKinds_readAsTwoHalvesJoinedByAMiddot() {
        val detail = state(slots = 7, stands = 2, bytes = 0L).toUiModel().saved.detail

        assertEquals(
            UiText.Composite(
                listOf(
                    UiText.Resource(Res.string.clear_data_saved_slots_other, listOf(7)),
                    UiText.Raw(" · "),
                    UiText.Resource(Res.string.clear_data_saved_stands_other, listOf(2)),
                ),
            ),
            detail,
        )
    }

    @Test
    fun toUiModel_oneOfEach_takesTheSingularOnBothHalves() {
        // French puts 1 in the singular, and the two halves are counted independently.
        assertEquals(
            UiText.Composite(
                listOf(
                    UiText.Resource(Res.string.clear_data_saved_slots_one, listOf(1)),
                    UiText.Raw(" · "),
                    UiText.Resource(Res.string.clear_data_saved_stands_one, listOf(1)),
                ),
            ),
            state(slots = 1, stands = 1, bytes = 0L).toUiModel().saved.detail,
        )
    }

    @Test
    fun toUiModel_aHalfThatIsZero_isAbsentRatherThanWrittenAsZero() {
        // "0 créneaux · 2 stands" spends the loudest half of the line on something they do not have.
        assertEquals(
            UiText.Composite(listOf(UiText.Resource(Res.string.clear_data_saved_stands_other, listOf(2)))),
            state(slots = 0, stands = 2, bytes = 0L).toUiModel().saved.detail,
        )
    }

    @Test
    fun toUiModel_nothingKept_saysSoInWordsRatherThanShowingNothing() {
        // A line that vanishes reads as a screen that has not finished loading, which is exactly
        // the doubt this screen must not create.
        assertEquals(
            UiText.Resource(Res.string.clear_data_saved_empty),
            state(slots = 0, stands = 0, bytes = 0L).toUiModel().saved.detail,
        )
    }

    @Test
    fun toUiModel_theSavedButton_isEnabledOnlyWhenThereIsSomethingToRemove() {
        assertTrue(state(slots = 0, stands = 1, bytes = 0L).toUiModel().saved.isEnabled)
        assertTrue(state(slots = 1, stands = 0, bytes = 0L).toUiModel().saved.isEnabled)
        assertFalse(state(slots = 0, stands = 0, bytes = 0L).toUiModel().saved.isEnabled)
    }

    // endregion

    // region what is on disk

    @Test
    fun toUiModel_anEmptyCache_saysSoRatherThanReadingZeroKo() {
        assertEquals(
            UiText.Resource(Res.string.clear_data_images_empty),
            state(slots = 0, stands = 0, bytes = 0L).toUiModel().images.detail,
        )
    }

    @Test
    fun toUiModel_underAMegabyte_readsInKilobytes() {
        assertEquals(
            UiText.Resource(Res.string.clear_data_images_kilobytes, listOf(94L)),
            state(slots = 0, stands = 0, bytes = 96_000L).toUiModel().images.detail,
        )
    }

    @Test
    fun toUiModel_aSingleByte_roundsUpRatherThanReadingAsEmpty() {
        // Zero Ko beside an enabled button is the one contradiction this screen can produce.
        assertEquals(
            UiText.Resource(Res.string.clear_data_images_kilobytes, listOf(1L)),
            state(slots = 0, stands = 0, bytes = 1L).toUiModel().images.detail,
        )
    }

    @Test
    fun toUiModel_aboveAMegabyte_keepsOneDecimalInFrenchNotation() {
        // The cap is 128 MB and a realistic edition fills single digits, so whole megabytes would
        // put most of the picture bank at the same number whatever it held.
        assertEquals(
            UiText.Resource(Res.string.clear_data_images_megabytes, listOf("4,5")),
            state(slots = 0, stands = 0, bytes = 4_823_000L).toUiModel().images.detail,
        )
    }

    @Test
    fun toUiModel_exactlyOneMegabyte_isTheFirstReadingInMegabytes() {
        assertEquals(
            UiText.Resource(Res.string.clear_data_images_megabytes, listOf("1,0")),
            state(slots = 0, stands = 0, bytes = 1024L * 1024L).toUiModel().images.detail,
        )
    }

    @Test
    fun toUiModel_theImagesButton_isEnabledOnlyWhenThereIsSomethingOnDisk() {
        assertTrue(state(slots = 0, stands = 0, bytes = 1L).toUiModel().images.isEnabled)
        assertFalse(state(slots = 0, stands = 0, bytes = 0L).toUiModel().images.isEnabled)
    }

    // endregion

    // region the question

    @Test
    fun toUiModel_theConfirmation_isCarriedThroughUntouched() {
        assertFalse(state(slots = 1, stands = 0, bytes = 0L).toUiModel().isConfirmingSaved)
        assertTrue(state(slots = 1, stands = 0, bytes = 0L, asking = true).toUiModel().isConfirmingSaved)
    }

    // endregion

    private fun state(
        slots: Int,
        stands: Int,
        bytes: Long,
        asking: Boolean = false,
    ) = ClearDataState(
        savedCount = SavedCount(slots = slots, stands = stands),
        imageCacheBytes = bytes,
        isAskingAboutSaved = asking,
    )
}
