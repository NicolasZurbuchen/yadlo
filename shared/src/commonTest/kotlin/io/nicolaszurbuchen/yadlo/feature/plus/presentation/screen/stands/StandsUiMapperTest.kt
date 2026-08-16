package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandDirectory
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandListing
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.stands_empty
import yadlo.shared.generated.resources.stands_filter_all
import yadlo.shared.generated.resources.stands_no_match
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StandsUiMapperTest {
    @Test
    fun toUiModel_beforeAnythingIsRead_isLoadingButAlreadyKnowsItsTitle() {
        val model = StandsState(kind = StandsKind.FOOD).toUiModel()

        // The title comes from the entry that was tapped, not from the content, so the bar reads
        // correctly while the list is still arriving.
        assertTrue(model.isLoading)
        assertEquals(UiText.Resource(StandsKind.FOOD.title), model.title)
        assertTrue(model.stands.isEmpty())
        assertNull(model.emptyMessage)
    }

    @Test
    fun toUiModel_theTitle_matchesTheRowThatOpenedIt() {
        val model = StandsState(kind = StandsKind.MAKERS, directory = directory()).toUiModel()

        assertEquals(UiText.Resource(StandsKind.MAKERS.title), model.title)
    }

    @Test
    fun toUiModel_nothingPublished_saysSoRatherThanBlamingTheFilter() {
        val model =
            StandsState(
                kind = StandsKind.FOOD,
                directory = StandDirectory(stands = emptyList(), marks = emptyList()),
            ).toUiModel()

        assertEquals(UiText.Resource(Res.string.stands_empty), model.emptyMessage)
    }

    @Test
    fun toUiModel_aFilterThatMatchesNothing_saysSomethingTheReaderCanActOn() {
        val model = state(selectedMark = "sans lactose").toUiModel()

        // Two different empties. Only one of them is the reader's to fix, and telling them the
        // stands are unpublished when they have simply over-filtered would be a lie.
        assertEquals(UiText.Resource(Res.string.stands_no_match), model.emptyMessage)
    }

    @Test
    fun toUiModel_theFirstChip_clearsTheFilterRatherThanBeingAMark() {
        val first = state().toUiModel().chips.first()

        assertNull(first.mark)
        assertEquals(UiText.Resource(Res.string.stands_filter_all), first.label)
        assertTrue(first.isSelected)
    }

    @Test
    fun toUiModel_theChips_areTheContentsOwnWords() {
        // `végé` and `sans gluten` are how the festival writes them and how a menu board writes
        // them. Prettifying either would be inventing a vocabulary the stands do not use.
        assertEquals(listOf(null, "végan", "bio", "végé"), state().toUiModel().chips.map { it.mark })
    }

    @Test
    fun toUiModel_aHalfWithNoMarks_offersOnlyTout() {
        // Créateurs publishes none, and the screen reads that as "no chip row at all" rather than
        // drawing a lone *Tout* that filters nothing.
        val model =
            StandsState(
                kind = StandsKind.MAKERS,
                directory = StandDirectory(stands = listOf(listing("la-fanfrelucherie")), marks = emptyList()),
            ).toUiModel()

        assertEquals(1, model.chips.size)
    }

    @Test
    fun toUiModel_aSelectedChip_isTheOnlyOneMarkedSo() {
        val model = state(selectedMark = "végé").toUiModel()

        assertEquals(listOf("végé"), model.chips.filter { it.isSelected }.map { it.mark })
    }

    @Test
    fun toUiModel_noFilter_showsEveryStandOfThatHalf() {
        assertEquals(listOf("vegan-fabrik", "de-lor-bokit", "guliko"), state().toUiModel().stands.map { it.id })
    }

    @Test
    fun toUiModel_filteringByAMarkOnlyOneDishCarries_keepsTheStand() {
        // De l'Or Bokit carries no stand mark and sells one végé bokit. "Can I eat here" is
        // answered yes, which is the only question the chip was asked.
        assertEquals(listOf("de-lor-bokit"), state(selectedMark = "végé").toUiModel().stands.map { it.id })
    }

    @Test
    fun toUiModel_aStandMatchedThroughItsMenu_stillShowsOnlyItsOwnMarks() {
        // Widening the line would turn "sells one vegan bokit" into "is vegan", which is exactly
        // the claim the stand/item split exists to prevent.
        assertNull(state(selectedMark = "végé").toUiModel().stands.single().marks)
    }

    @Test
    fun toUiModel_marks_readAsOneLineWithTheFichesSeparator() {
        assertEquals("végan · bio", state().toUiModel().stands.first().marks)
    }

    @Test
    fun toUiModel_aStandWithNoMarks_writesNothingRatherThanAnEmptyLine() {
        assertNull(state().toUiModel().stands.last().marks)
    }

    @Test
    fun toUiModel_withSomethingListed_saysNothingAboutBeingEmpty() {
        assertNull(state().toUiModel().emptyMessage)
    }

    private fun state(selectedMark: String? = null) =
        StandsState(kind = StandsKind.FOOD, directory = directory(), selectedMark = selectedMark)

    private fun directory() =
        StandDirectory(
            marks = listOf("végan", "bio", "végé"),
            stands =
                listOf(
                    listing(
                        id = "vegan-fabrik",
                        offering = "Cuisine végétale",
                        marks = listOf("végan", "bio"),
                        dietaryMatches = setOf("végan", "bio"),
                    ),
                    listing(
                        id = "de-lor-bokit",
                        offering = "Cuisine guadeloupéenne",
                        dietaryMatches = setOf("végé"),
                    ),
                    listing(id = "guliko", offering = "Cuisine géorgienne"),
                ),
        )

    private fun listing(
        id: String,
        offering: String? = null,
        marks: List<String> = emptyList(),
        dietaryMatches: Set<String> = emptySet(),
    ) = StandListing(
        id = id,
        name = id,
        offering = offering,
        marks = marks,
        dietaryMatches = dietaryMatches,
    )
}
